package com.example.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlin.random.Random

class CallNotificationService : Service() {

    private var firestoreListener: ListenerRegistration? = null
    private var activeRingtone: Ringtone? = null
    private var currentRingingSenderId: Int? = null
    private val handledMessageIds = mutableSetOf<String>()

    companion object {
        private const val TAG = "CallNotificationService"
        private const val PERSISTENT_CHANNEL_ID = "call_sync_channel"
        private const val RINGING_CHANNEL_ID = "incoming_call_channel"
        private const val PERSISTENT_NOTIF_ID = 8001
        private const val RINGING_NOTIF_ID = 8002

        const val ACTION_ANSWER = "com.example.ACTION_ANSWER_CALL"
        const val ACTION_DECLINE = "com.example.ACTION_DECLINE_CALL"
        const val ACTION_STOP_RINGING = "com.example.ACTION_STOP_RINGING"
        const val EXTRA_SENDER_ID = "incoming_call_sender_id"
        const val EXTRA_CALL_TYPE = "incoming_call_type"
        const val EXTRA_MESSAGE_ID = "incoming_call_message_id"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "CallNotificationService created")
        createNotificationChannels()
        startPersistentForeground()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "CallNotificationService onStartCommand action=${intent?.action}")
        
        when (intent?.action) {
            ACTION_DECLINE -> {
                val senderId = intent.getIntExtra(EXTRA_SENDER_ID, -1)
                val messageId = intent.getStringExtra(EXTRA_MESSAGE_ID)
                if (messageId != null) {
                    markMessageAsRead(messageId)
                }
                handleDeclineCall(senderId)
            }
            ACTION_STOP_RINGING -> {
                val messageId = intent.getStringExtra(EXTRA_MESSAGE_ID)
                if (messageId != null) {
                    markMessageAsRead(messageId)
                }
                stopRingingAndDismissAlert()
            }
        }

        startListeningForIncomingCalls()
        return START_STICKY
    }

    private fun getFirestoreSafe(): FirebaseFirestore? {
        return if (com.google.firebase.FirebaseApp.getApps(this).isNotEmpty()) {
            try {
                FirestoreService.db
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get Firestore instance", e)
                null
            }
        } else {
            null
        }
    }

    private fun markMessageAsRead(messageId: String) {
        val db = getFirestoreSafe() ?: return
        db.collection("chat_messages").document(messageId)
            .update("isRead", true)
            .addOnSuccessListener {
                Log.d(TAG, "Message $messageId marked as read")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to mark message $messageId as read: ${e.message}")
            }
    }

    private fun startPersistentForeground() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, PERSISTENT_CHANNEL_ID)
            .setContentTitle("Real-time call listener active")
            .setContentText("Connected and listening for incoming voice & video chats")
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    PERSISTENT_NOTIF_ID,
                    notification,
                    android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL
                )
            } else {
                startForeground(PERSISTENT_NOTIF_ID, notification)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start foreground service: ${e.message}", e)
        }
    }

    private fun startListeningForIncomingCalls() {
        // Stop any old listeners
        firestoreListener?.remove()

        val sessionManager = SessionManager(this)
        val loggedUser = sessionManager.getLoggedInUser()
        if (loggedUser == null) {
            Log.d(TAG, "No logged in user found. Stopping call listening service.")
            stopSelf()
            return
        }

        Log.d(TAG, "Starting live incoming call listening service for user: ${loggedUser.username} (ID: ${loggedUser.id})")
        val db = getFirestoreSafe() ?: return

        // Sync and listen to chat messages directed to this user
        val serviceStartTime = System.currentTimeMillis()

        firestoreListener = db.collection("chat_messages")
            .whereEqualTo("receiverId", loggedUser.id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error in snapshot call listener: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val documents = snapshot.documents
                    // Handle incoming calls
                    for (doc in documents) {
                        val messageId = doc.id
                        val type = doc.getString("type") ?: ""
                        val senderId = doc.getLong("senderId")?.toInt() ?: -1
                        val timestamp = doc.getLong("timestamp") ?: 0L
                        val isRead = doc.getBoolean("isRead") ?: false

                        // Check if it is a call invite and is active and recent (within 45 seconds)
                        if (type == "call_invite" && timestamp >= (System.currentTimeMillis() - 45_000)) {
                            if (!isRead) {
                                if (!handledMessageIds.contains(messageId)) {
                                    val callType = doc.getString("text") ?: "audio"
                                    handledMessageIds.add(messageId)
                                    triggerIncomingCallAlert(senderId, callType, messageId)
                                }
                            } else {
                                // If it was ringing for this message but now it is read, stop ringing
                                if (currentRingingSenderId == senderId) {
                                    stopRingingAndDismissAlert()
                                }
                            }
                        }

                        // Check if call was cancelled dynamically by caller sending a missed_ call or decline
                        if (type.startsWith("missed_") && senderId == currentRingingSenderId) {
                            stopRingingAndDismissAlert()
                        }
                    }
                }
            }
    }

    private fun triggerIncomingCallAlert(senderId: Int, callType: String, messageId: String) {
        if (currentRingingSenderId == senderId) return // Already ringing for this user

        currentRingingSenderId = senderId
        Log.d(TAG, "Ringing incoming $callType call from sender ID: $senderId")

        // 1. Play Ringtone Sound
        try {
            if (activeRingtone == null) {
                val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                activeRingtone = RingtoneManager.getRingtone(applicationContext, uri)
                activeRingtone?.play()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Fetch Sender username or display info
        val firestore = getFirestoreSafe() ?: return
        firestore.collection("users").document(senderId.toString()).get()
            .addOnSuccessListener { doc ->
                val fullName = doc.getString("fullName") ?: ""
                val username = doc.getString("username") ?: ""
                val callerName = fullName.ifBlank { username }.ifBlank { "User #$senderId" }
                showIncomingCallNotification(senderId, callerName, callType, messageId)
            }
            .addOnFailureListener {
                showIncomingCallNotification(senderId, "Incoming Call", callType, messageId)
            }
    }

    private fun showIncomingCallNotification(senderId: Int, callerName: String, callType: String, messageId: String) {
        // Pending Intents for Answer / Decline
        val answerIntent = Intent(this, MainActivity::class.java).apply {
            action = ACTION_ANSWER
            putExtra(EXTRA_SENDER_ID, senderId)
            putExtra(EXTRA_CALL_TYPE, callType)
            putExtra(EXTRA_MESSAGE_ID, messageId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val answerPendingIntent = PendingIntent.getActivity(
            this, 102, answerIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val declineIntent = Intent(this, CallNotificationService::class.java).apply {
            action = ACTION_DECLINE
            putExtra(EXTRA_SENDER_ID, senderId)
            putExtra(EXTRA_MESSAGE_ID, messageId)
        }
        val declinePendingIntent = PendingIntent.getService(
            this, 103, declineIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, RINGING_CHANNEL_ID)
            .setContentTitle("Incoming ${callType.uppercase()} Call")
            .setContentText("$callerName is calling you...")
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setOngoing(true)
            .setAutoCancel(false)
            .setFullScreenIntent(answerPendingIntent, true) // Show overlays on lock screens!
            .addAction(android.R.drawable.ic_media_play, "Answer", answerPendingIntent)
            .addAction(android.R.drawable.ic_delete, "Decline", declinePendingIntent)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(RINGING_NOTIF_ID, notification)
    }

    private fun handleDeclineCall(senderId: Int) {
        Log.d(TAG, "Declining incoming call from sender ID: $senderId")
        if (senderId != -1) {
            // Write a missed call message back to Firestore to notify them we declined
            val db = getFirestoreSafe() ?: return
            val sessionManager = SessionManager(this)
            val loggedUser = sessionManager.getLoggedInUser()
            if (loggedUser != null) {
                val generatedId = (System.currentTimeMillis() % 100000000).toInt() + Random.nextInt(5000)
                val chatMessage = mapOf(
                    "id" to generatedId,
                    "senderId" to loggedUser.id,
                    "receiverId" to senderId,
                    "text" to "",
                    "type" to "missed_audio_call",
                    "timestamp" to System.currentTimeMillis(),
                    "isRead" to false,
                    "isEdited" to false,
                    "isReported" to false
                )
                db.collection("chat_messages").document(generatedId.toString())
                    .set(chatMessage)
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to write missed call on decline: ${e.message}")
                    }
            }
        }
        stopRingingAndDismissAlert()
    }

    private fun stopRingingAndDismissAlert() {
        Log.d(TAG, "Stopping ringtone and dismissing call notifications")
        try {
            activeRingtone?.stop()
            activeRingtone = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        currentRingingSenderId = null

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(RINGING_NOTIF_ID)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. Persistent Foreground Channel
            val persistentChannel = NotificationChannel(
                PERSISTENT_CHANNEL_ID,
                "Call Sync Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Runs background real-time synchronization of inbound chat voice/video calls"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(persistentChannel)

            // 2. Ringing Call Channel
            val ringingChannel = NotificationChannel(
                RINGING_CHANNEL_ID,
                "Incoming Calls Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Rings and flashes call actions on screen for inbound voice and video calls"
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(ringingChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.d(TAG, "CallNotificationService onDestroy")
        firestoreListener?.remove()
        try {
            activeRingtone?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
}
