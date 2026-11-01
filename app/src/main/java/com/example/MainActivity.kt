package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.AppDatabase
import com.example.data.BlogRepository
import com.example.data.SessionManager
import com.example.ui.BlogViewModel
import com.example.ui.HomeScreen
import com.example.ui.LoginScreen
import com.example.ui.theme.MyApplicationTheme

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.graphics.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.shape.*
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            android.util.Log.e("FATAL_CRASH", "Uncaught exception in thread ${thread.name}", exception)
            defaultHandler?.uncaughtException(thread, exception)
        }

        enableEdgeToEdge()

        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val apiKey = com.example.BuildConfig.FIREBASE_API_KEY
                val appId = com.example.BuildConfig.FIREBASE_APP_ID
                val projectId = com.example.BuildConfig.FIREBASE_PROJECT_ID
                if (apiKey.isNotBlank() && apiKey != "YOUR_FIREBASE_API_KEY") {
                    val options = FirebaseOptions.Builder()
                        .setApiKey(apiKey)
                        .setApplicationId(appId)
                        .setProjectId(projectId)
                        .build()
                    FirebaseApp.initializeApp(this, options)
                } else {
                    android.util.Log.e("Firebase", "Firebase not initialized. Missing API Key from .env")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val dbId = getString(R.string.firestore_database_id)
            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance(dbId)
            com.example.data.FirestoreService.setFirestore(firestore)
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Failed to set custom Firestore db: ${e.message}", e)
        }

        val database = AppDatabase.getDatabase(this)
        val repository = BlogRepository(
            database.userDao(),
            database.postDao(),
            database.commentDao(),
            database.bookmarkDao(),
            database.likeDao(),
            database.followDao(),
            database.chatMessageDao(),
            database.storyDao()
        )
        val sessionManager = SessionManager(this)
        val factory = BlogViewModel.Factory(repository, sessionManager)

        setContent {
            val viewModel: BlogViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)
            mainViewModel = viewModel

            LaunchedEffect(intent) {
                handleIncomingCallIntent(intent)
            }

            LaunchedEffect(Unit) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    val permission = android.Manifest.permission.POST_NOTIFICATIONS
                    if (checkSelfPermission(permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(arrayOf(permission), 101)
                    }
                }
            }

            val currentUser by viewModel.currentUser.collectAsState()
            LaunchedEffect(currentUser) {
                if (currentUser != null) {
                    val serviceIntent = android.content.Intent(this@MainActivity, com.example.data.CallNotificationService::class.java)
                    try {
                        androidx.core.content.ContextCompat.startForegroundService(this@MainActivity, serviceIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    val serviceIntent = android.content.Intent(this@MainActivity, com.example.data.CallNotificationService::class.java)
                    try {
                        stopService(serviceIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            MyApplicationTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BlogApp(factory, viewModel)
                }
            }
        }
    }

    private var mainViewModel: BlogViewModel? = null

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingCallIntent(intent)
    }

    private fun handleIncomingCallIntent(intent: android.content.Intent) {
        val action = intent.action
        android.util.Log.d("MainActivity", "handleIncomingCallIntent: action=$action")
        if (action == com.example.data.CallNotificationService.ACTION_ANSWER) {
            val senderId = intent.getIntExtra(com.example.data.CallNotificationService.EXTRA_SENDER_ID, -1)
            val callType = intent.getStringExtra(com.example.data.CallNotificationService.EXTRA_CALL_TYPE)
            val messageId = intent.getStringExtra(com.example.data.CallNotificationService.EXTRA_MESSAGE_ID)
            android.util.Log.d("MainActivity", "Matched answer action for sender=$senderId type=$callType")
            if (senderId != -1 && callType != null) {
                mainViewModel?.let { vm ->
                    vm.pendingCallAnswerFromServiceUser.value = senderId
                    vm.pendingCallAnswerType.value = callType
                }
                
                // Stop the ringing notification!
                val stopRingingIntent = android.content.Intent(this, com.example.data.CallNotificationService::class.java).apply {
                    this.action = com.example.data.CallNotificationService.ACTION_STOP_RINGING
                    putExtra(com.example.data.CallNotificationService.EXTRA_MESSAGE_ID, messageId)
                }
                try {
                    startService(stopRingingIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}

@Composable
fun BlogApp(factory: BlogViewModel.Factory, viewModel: BlogViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)) {
    val navController = rememberNavController()
    
    val currentUser by viewModel.currentUser.collectAsState()
    var isSplashFinished by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    val pendingMsgUser = viewModel.pendingCallAnswerFromServiceUser.value
    val allMessages by viewModel.allChatMessages.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    // Global Incoming Call State for in-app overlay
    val incomingCallMessage = remember(allMessages, currentUser) {
        val currentId = currentUser?.id ?: -1
        allMessages.find { 
            it.receiverId == currentId && 
            it.type == "call_invite" && 
            !it.isRead && 
            it.timestamp >= (System.currentTimeMillis() - 45_000)
        }
    }

    LaunchedEffect(pendingMsgUser) {
        if (pendingMsgUser != null) {
            isSplashFinished = true
            navController.navigate("messaging?userId=$pendingMsgUser")
        }
    }

    LaunchedEffect(currentUser, isSplashFinished) {
        if (!isSplashFinished) return@LaunchedEffect
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (currentUser == null) {
            if (currentRoute != "login" && currentRoute != "forgot_password" && currentRoute != "splash") {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
        } else {
            if (currentRoute == "login" || currentRoute == "splash" || currentRoute == null) {
                navController.navigate("home") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            ZoozSplashScreen(
                onSplashFinished = {
                    isSplashFinished = true
                    val currentRoute = navController.currentBackStackEntry?.destination?.route
                    if (currentRoute == "splash" || currentRoute == null) {
                        if (currentUser == null) {
                            navController.navigate("login") {
                                popUpTo("splash") { inclusive = true }
                            }
                        } else {
                            navController.navigate("home") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate("forgot_password")
                }
            )
        }

        composable("forgot_password") {
            com.example.ui.ForgotPasswordScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToCreatePost = { navController.navigate("create_story") },
                onNavigateToPostDetail = { postId -> navController.navigate("post_detail/$postId") },
                onNavigateToMenuDest = { route -> navController.navigate(route) },
                onNavigateToUserProfile = { userId -> navController.navigate("user_profile/$userId") }
            )
        }
        
        composable("profile") {
            com.example.ui.ProfileScreen(
                userId = null,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostDetail = { postId -> navController.navigate("post_detail/$postId") },
                onNavigateToFollowers = { userId -> navController.navigate("followers/$userId") },
                onNavigateToFollowing = { userId -> navController.navigate("following/$userId") },
                onNavigateToMessaging = { userId -> navController.navigate("messaging?userId=$userId") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToMenuDest = { route -> navController.navigate(route) }
            )
        }

        composable(
            route = "user_profile/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: return@composable
            com.example.ui.ProfileScreen(
                userId = userId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostDetail = { postId -> navController.navigate("post_detail/$postId") },
                onNavigateToFollowers = { id -> navController.navigate("followers/$id") },
                onNavigateToFollowing = { id -> navController.navigate("following/$id") },
                onNavigateToMessaging = { id -> navController.navigate("messaging?userId=$id") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToMenuDest = { route -> navController.navigate(route) }
            )
        }

        composable(
            route = "messaging?userId={userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType; defaultValue = -1 })
        ) { backStackEntry ->
            val targetUserId = backStackEntry.arguments?.getInt("userId") ?: -1
            com.example.ui.MessagingScreen(
                targetUserId = if (targetUserId == -1) null else targetUserId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToChatInfo = { id -> navController.navigate("chat_info/$id") },
                onNavigateToStory = { id -> navController.navigate("stories/$id") },
                onCreateStory = { navController.navigate("create_story") }
            )
        }

        composable(
            route = "chat_info/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: return@composable
            com.example.ui.ChatProfileInfoScreen(
                userId = userId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { id -> navController.navigate("user_profile/$id") }
            )
        }
        
        composable("bookmarks") {
            com.example.ui.BookmarksScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostDetail = { postId -> navController.navigate("post_detail/$postId") },
                onNavigateToUserProfile = { userId -> navController.navigate("user_profile/$userId") }
            )
        }
        
        composable("settings") {
            com.example.ui.SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("account_settings") {
            com.example.ui.AccountSettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "followers/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: return@composable
            com.example.ui.FollowersScreen(
                userId = userId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToUserProfile = { id -> navController.navigate("user_profile/$id") }
            )
        }
        
        composable(
            route = "following/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: return@composable
            com.example.ui.FollowingScreen(
                userId = userId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToUserProfile = { id -> navController.navigate("user_profile/$id") }
            )
        }
        
        composable("help") {
            com.example.ui.HelpScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable("creator_eligibility") {
            com.example.ui.CreatorToolsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostDetail = { postId -> navController.navigate("post_detail/$postId") }
            )
        }

        composable("professional_dashboard") {
            com.example.ui.CreatorToolsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostDetail = { postId -> navController.navigate("post_detail/$postId") }
            )
        }

        composable("creator_wallet") {
            com.example.ui.CreatorWalletScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("watch_history") {
            com.example.ui.WatchHistoryScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostDetail = { postId -> navController.navigate("post_detail/$postId") }
            )
        }

        composable(
            route = "sound_detail?trackName={trackName}&trackArtist={trackArtist}&previewUrl={previewUrl}&imageUrl={imageUrl}",
            arguments = listOf(
                navArgument("trackName") { type = NavType.StringType; defaultValue = "Original Sound" },
                navArgument("trackArtist") { type = NavType.StringType; defaultValue = "Zooz Creator" },
                navArgument("previewUrl") { type = NavType.StringType; defaultValue = "" },
                navArgument("imageUrl") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val trackName = backStackEntry.arguments?.getString("trackName") ?: "Original Sound"
            val trackArtist = backStackEntry.arguments?.getString("trackArtist") ?: "Zooz Creator"
            val previewUrl = backStackEntry.arguments?.getString("previewUrl")?.takeIf { it.isNotBlank() }
            val imageUrl = backStackEntry.arguments?.getString("imageUrl")?.takeIf { it.isNotBlank() }
            com.example.ui.SoundDetailScreen(
                trackName = trackName,
                trackArtist = trackArtist,
                previewUrl = previewUrl,
                imageUrl = imageUrl,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostDetail = { postId -> navController.navigate("post_detail/$postId") },
                onUseThisSound = { sTrackName, sTrackArtist, sPreviewUrl, sImageUrl ->
                    val encTrack = java.net.URLEncoder.encode(sTrackName, "UTF-8")
                    val encArtist = java.net.URLEncoder.encode(sTrackArtist, "UTF-8")
                    val encPreview = java.net.URLEncoder.encode(sPreviewUrl ?: "", "UTF-8")
                    val encImg = java.net.URLEncoder.encode(sImageUrl ?: "", "UTF-8")
                    navController.navigate("create_story?trackName=$encTrack&trackArtist=$encArtist&previewUrl=$encPreview&imageUrl=$encImg")
                }
            )
        }

        composable(
            route = "create_story?trackName={trackName}&trackArtist={trackArtist}&previewUrl={previewUrl}&imageUrl={imageUrl}",
            arguments = listOf(
                navArgument("trackName") { type = NavType.StringType; defaultValue = "" },
                navArgument("trackArtist") { type = NavType.StringType; defaultValue = "" },
                navArgument("previewUrl") { type = NavType.StringType; defaultValue = "" },
                navArgument("imageUrl") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val trackName = backStackEntry.arguments?.getString("trackName")?.takeIf { it.isNotBlank() }
            val trackArtist = backStackEntry.arguments?.getString("trackArtist")?.takeIf { it.isNotBlank() }
            val previewUrl = backStackEntry.arguments?.getString("previewUrl")?.takeIf { it.isNotBlank() }
            val imageUrl = backStackEntry.arguments?.getString("imageUrl")?.takeIf { it.isNotBlank() }

            val initialTrack = if (!trackName.isNullOrEmpty()) {
                com.example.ui.SpotifyTrack(
                    id = "sound_" + System.currentTimeMillis(),
                    name = trackName,
                    artist = trackArtist ?: "Creator",
                    previewUrl = previewUrl,
                    albumImageUrl = imageUrl
                )
            } else null

            com.example.ui.CreateStoryScreen(
                viewModel = viewModel,
                initialSpotifyTrack = initialTrack,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "stories/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: return@composable
            com.example.ui.StoryViewScreen(
                authorId = userId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("creator_tools") {
            com.example.ui.CreatorToolsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostDetail = { postId -> navController.navigate("post_detail/$postId") }
            )
        }
        
        // Standalone Auth Module Routes
        composable("standalone_auth") {
            com.example.authentication.ZoozSecureAuthScreen(
                viewModel = viewModel,
                onAuthSuccess = {
                    navController.navigate("standalone_auth_success") {
                        popUpTo("standalone_auth") { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = "post_detail/{postId}?openComments={openComments}",
            arguments = listOf(
                navArgument("postId") { type = NavType.IntType },
                navArgument("openComments") { type = NavType.BoolType; defaultValue = false }
            )
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getInt("postId") ?: return@composable
            val openComments = backStackEntry.arguments?.getBoolean("openComments") ?: false
            com.example.ui.PostDetailScreen(
                postId = postId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToUserProfile = { id -> navController.navigate("user_profile/$id") },
                autoOpenComments = openComments
            )
        }
    }

    // Modern In-App Call Alert
    incomingCallMessage?.let { msg ->
        val caller = allUsers.find { it.id == msg.senderId }
        val context = androidx.compose.ui.platform.LocalContext.current
        
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            androidx.compose.material3.Card(
                modifier = Modifier
                    .padding(16.dp)
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .animateContentSize(),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 12.dp),
                shape = RoundedCornerShape(24.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.foundation.layout.Box {
                        androidx.compose.foundation.Canvas(modifier = Modifier.size(60.dp)) {
                            drawCircle(
                                color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                                radius = size.minDimension / 1.5f
                            )
                        }
                        AsyncImage(
                            model = caller?.profilePicUri ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop",
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .align(Alignment.Center),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = caller?.fullName?.ifBlank { caller.username } ?: "Incoming Call",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "is ${msg.text} calling you...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = {
                            viewModel.markMessagesAsRead(msg.senderId)
                            val stopIntent = android.content.Intent(context, com.example.data.CallNotificationService::class.java).apply {
                                action = com.example.data.CallNotificationService.ACTION_STOP_RINGING
                                putExtra(com.example.data.CallNotificationService.EXTRA_MESSAGE_ID, msg.id.toString())
                            }
                            context.startService(stopIntent)
                        },
                        modifier = Modifier
                            .background(Color.Red.copy(alpha = 0.15f), CircleShape)
                            .testTag("decline_call_button")
                    ) {
                        Icon(Icons.Default.CallEnd, contentDescription = "Decline", tint = Color.Red)
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = {
                            viewModel.markMessagesAsRead(msg.senderId)
                            viewModel.pendingCallAnswerFromServiceUser.value = msg.senderId
                            viewModel.pendingCallAnswerType.value = msg.text
                            
                            val stopIntent = android.content.Intent(context, com.example.data.CallNotificationService::class.java).apply {
                                action = com.example.data.CallNotificationService.ACTION_STOP_RINGING
                                putExtra(com.example.data.CallNotificationService.EXTRA_MESSAGE_ID, msg.id.toString())
                            }
                            context.startService(stopIntent)
                        },
                        modifier = Modifier
                            .background(Color(0xFF4CAF50).copy(alpha = 0.15f), CircleShape)
                            .testTag("answer_call_button")
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Answer", tint = Color(0xFF4CAF50))
                    }
                }
            }
        }
    }
}

@Composable
fun ZoozSplashScreen(
    onSplashFinished: () -> Unit
) {
    var progress by remember { mutableStateOf(0f) }
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1500, easing = LinearEasing),
        label = "SplashProgress"
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "Heartbeat")
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LogoScale"
    )

    LaunchedEffect(Unit) {
        progress = 1f
        kotlinx.coroutines.delay(1500)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Aesthetic dynamic background circle glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            val color = Color(0x0C4285F4)
            drawCircle(
                color = color,
                radius = size.minDimension * 0.70f,
                center = center
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .scale(logoScale),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color(0xFF4285F4))) { append("z") } 
                        withStyle(style = SpanStyle(color = Color(0xFFEA4335))) { append("o") } 
                        withStyle(style = SpanStyle(color = Color(0xFFFBBC05))) { append("o") } 
                        withStyle(style = SpanStyle(color = Color(0xFF34A853))) { append("z") }
                    },
                    fontSize = 110.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    softWrap = false
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "The Real-time Social Hub",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Initializing social hub...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontWeight = FontWeight.Light
            )
        }
    }
}

