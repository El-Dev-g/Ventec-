package com.example.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.google.android.gms.tasks.Task

/**
 * Extension to await a standard play-services Task safely in coroutines.
 * Avoids extra library dependencies and ensures 100% build compatibility.
 */
suspend fun <T> Task<T>.awaitResult(): T = suspendCoroutine { continuation ->
    addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(task.result)
        } else {
            continuation.resumeWithException(task.exception ?: RuntimeException("Task failed without exception"))
        }
    }
}
object FirestoreService {
    private const val TAG = "FirestoreService"
    private var customDb: FirebaseFirestore? = null

    fun setFirestore(firestore: FirebaseFirestore) {
        customDb = firestore
    }

    val db: FirebaseFirestore
        get() {
            customDb?.let { return it }
            return try {
                FirebaseFirestore.getInstance("ai-studio-android-zoozoffi-66e80b6e-5bc8-457f-84ec-04815044538b")
            } catch (e: Exception) {
                FirebaseFirestore.getInstance()
            }
        }

    // Collection paths
    private const val COLL_USERS = "users"
    private const val COLL_POSTS = "posts"
    private const val COLL_COMMENTS = "comments"
    private const val COLL_LIKES = "likes"
    private const val COLL_BOOKMARKS = "bookmarks"
    private const val COLL_FOLLOWS = "follows"
    private const val COLL_CHAT_MESSAGES = "chat_messages"
    private const val COLL_TYPING_STATUS = "typing_status"
    private const val COLL_USER_EMAILS = "user_emails"
    private const val COLL_STORIES = "stories"

    // Mapping Helpers for robust serialization
    private fun userToMap(user: User): Map<String, Any?> = mapOf(
        "id" to user.id,
        "username" to user.username,
        "fullName" to user.fullName,
        "bio" to user.bio,
        "profilePicUri" to user.profilePicUri,
        "lastSeen" to user.lastSeen
    )

    private fun storyToMap(story: Story): Map<String, Any?> = mapOf(
        "id" to story.id,
        "authorId" to story.authorId,
        "authorName" to story.authorName,
        "mediaUri" to story.mediaUri,
        "text" to story.text,
        "spotifyTrackId" to story.spotifyTrackId,
        "spotifyTrackName" to story.spotifyTrackName,
        "spotifyTrackArtist" to story.spotifyTrackArtist,
        "spotifyTrackImageUrl" to story.spotifyTrackImageUrl,
        "spotifyTrackPreviewUrl" to story.spotifyTrackPreviewUrl,
        "timestamp" to story.timestamp,
        "expiresAt" to story.expiresAt
    )

    private fun postToMap(post: Post): Map<String, Any?> = mapOf(
        "id" to post.id,
        "authorId" to post.authorId,
        "authorName" to post.authorName,
        "title" to post.title,
        "content" to post.content,
        "categories" to post.categories,
        "tags" to post.tags,
        "mediaUri" to post.mediaUri,
        "spotifyTrackId" to post.spotifyTrackId,
        "spotifyTrackName" to post.spotifyTrackName,
        "spotifyTrackArtist" to post.spotifyTrackArtist,
        "spotifyTrackImageUrl" to post.spotifyTrackImageUrl,
        "spotifyTrackPreviewUrl" to post.spotifyTrackPreviewUrl,
        "isDraft" to post.isDraft,
        "draft" to post.isDraft,
        "timestamp" to post.timestamp,
        "commentsDisabled" to post.commentsDisabled,
        "isPromoted" to post.isPromoted,
        "promoteFee" to post.promoteFee,
        "clicks" to post.clicks,
        "impressions" to post.impressions
    )

    private fun commentToMap(comment: Comment): Map<String, Any?> = mapOf(
        "id" to comment.id,
        "postId" to comment.postId,
        "authorId" to comment.authorId,
        "authorName" to comment.authorName,
        "content" to comment.content,
        "timestamp" to comment.timestamp,
        "isPinned" to comment.isPinned,
        "pinned" to comment.isPinned
    )

    private fun likeToMap(like: Like): Map<String, Any?> = mapOf(
        "id" to like.id,
        "userId" to like.userId,
        "postId" to like.postId
    )

    private fun bookmarkToMap(bookmark: Bookmark): Map<String, Any?> = mapOf(
        "id" to bookmark.id,
        "userId" to bookmark.userId,
        "postId" to bookmark.postId
    )

    private fun followToMap(follow: Follow): Map<String, Any?> = mapOf(
        "id" to follow.id,
        "followerId" to follow.followerId,
        "followingId" to follow.followingId
    )

    private fun chatMessageToMap(chatMessage: ChatMessage): Map<String, Any?> = mapOf(
        "id" to chatMessage.id,
        "senderId" to chatMessage.senderId,
        "receiverId" to chatMessage.receiverId,
        "text" to chatMessage.text,
        "type" to chatMessage.type,
        "mediaUri" to chatMessage.mediaUri,
        "spotifyTrackId" to chatMessage.spotifyTrackId,
        "spotifyTrackName" to chatMessage.spotifyTrackName,
        "spotifyTrackArtist" to chatMessage.spotifyTrackArtist,
        "spotifyTrackImageUrl" to chatMessage.spotifyTrackImageUrl,
        "spotifyTrackPreviewUrl" to chatMessage.spotifyTrackPreviewUrl,
        "timestamp" to chatMessage.timestamp,
        "isEdited" to chatMessage.isEdited,
        "isReported" to chatMessage.isReported,
        "replyToId" to chatMessage.replyToId,
        "isRead" to chatMessage.isRead
    )

    // ==========================================
    // 1. USER METHODS
    // ==========================================

    suspend fun saveUser(user: User) {
        try {
            db.collection(COLL_USERS)
                .document(user.id.toString())
                .set(userToMap(user), SetOptions.merge())
                .awaitResult()
            Log.d(TAG, "Successfully saved user: ${user.username}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save user", e)
        }
    }

    suspend fun getUserById(userId: Int): User? {
        return try {
            val doc = db.collection(COLL_USERS)
                .document(userId.toString())
                .get()
                .awaitResult()
            if (doc.exists()) {
                val username = doc.getString("username") ?: ""
                val fullName = doc.getString("fullName") ?: ""
                val bio = doc.getString("bio") ?: ""
                val profilePicUri = doc.getString("profilePicUri")
                val lastSeen = doc.getLong("lastSeen") ?: System.currentTimeMillis()
                User(userId, username, fullName, bio, profilePicUri, lastSeen = lastSeen)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get user by id $userId", e)
            null
        }
    }

    suspend fun fetchAllUsers(): List<User> {
        return try {
            val snapshot = db.collection(COLL_USERS)
                .get()
                .awaitResult()
            snapshot.documents.mapNotNull { doc ->
                val idLong = doc.getLong("id") ?: doc.id.toLongOrNull() ?: return@mapNotNull null
                val id = idLong.toInt()
                val username = doc.getString("username") ?: ""
                val fullName = doc.getString("fullName") ?: ""
                val bio = doc.getString("bio") ?: ""
                val profilePicUri = doc.getString("profilePicUri")
                val lastSeen = doc.getLong("lastSeen") ?: System.currentTimeMillis()
                User(id, username, fullName, bio, profilePicUri, lastSeen = lastSeen)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch all users", e)
            emptyList()
        }
    }

    suspend fun updateLastSeen(userId: Int) {
        try {
            db.collection(COLL_USERS)
                .document(userId.toString())
                .set(mapOf("lastSeen" to System.currentTimeMillis()), SetOptions.merge())
                .awaitResult()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update lastSeen for user $userId", e)
        }
    }

    // ==========================================
    // 2. POST METHODS
    // ==========================================

    suspend fun savePost(post: Post) {
        try {
            db.collection(COLL_POSTS)
                .document(post.id.toString())
                .set(postToMap(post), SetOptions.merge())
                .awaitResult()
            Log.d(TAG, "Successfully saved post: ${post.title}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save post", e)
        }
    }

    suspend fun fetchAllPosts(): List<Post> {
        return try {
            val snapshot = db.collection(COLL_POSTS)
                .get()
                .awaitResult()
            snapshot.documents.mapNotNull { doc ->
                val id = doc.getLong("id")?.toInt() ?: doc.id.toIntOrNull() ?: return@mapNotNull null
                val authorId = doc.getLong("authorId")?.toInt() ?: 0
                val authorName = doc.getString("authorName") ?: ""
                val title = doc.getString("title") ?: ""
                val content = doc.getString("content") ?: ""
                val categories = doc.getString("categories") ?: ""
                val tags = doc.getString("tags") ?: ""
                val mediaUri = doc.getString("mediaUri")
                val spotifyTrackId = doc.getString("spotifyTrackId")
                val spotifyTrackName = doc.getString("spotifyTrackName")
                val spotifyTrackArtist = doc.getString("spotifyTrackArtist")
                val spotifyTrackImageUrl = doc.getString("spotifyTrackImageUrl")
                val spotifyTrackPreviewUrl = doc.getString("spotifyTrackPreviewUrl")
                val isDraft = doc.getBoolean("draft") ?: doc.getBoolean("isDraft") ?: false
                val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                val commentsDisabled = doc.getBoolean("commentsDisabled") ?: false
                val isPromoted = doc.getBoolean("isPromoted") ?: doc.getBoolean("promoted") ?: false
                val promoteFee = doc.getDouble("promoteFee") ?: 0.0
                val clicks = doc.getLong("clicks")?.toInt() ?: 0
                val impressions = doc.getLong("impressions")?.toInt() ?: 0
                Post(id, authorId, authorName, title, content, categories, tags, mediaUri, spotifyTrackId, spotifyTrackName, spotifyTrackArtist, spotifyTrackImageUrl, spotifyTrackPreviewUrl, isDraft, timestamp, commentsDisabled, isPromoted, promoteFee, clicks, impressions)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch all posts", e)
            emptyList()
        }
    }

    suspend fun deletePost(postId: Int) {
        try {
            db.collection(COLL_POSTS)
                .document(postId.toString())
                .delete()
                .awaitResult()
            Log.d(TAG, "Successfully deleted post ID: $postId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete post", e)
        }
    }

    // ==========================================
    // 3. COMMENT METHODS
    // ==========================================

    suspend fun saveComment(comment: Comment) {
        try {
            db.collection(COLL_COMMENTS)
                .document(comment.id.toString())
                .set(commentToMap(comment), SetOptions.merge())
                .awaitResult()
            Log.d(TAG, "Successfully saved comment on post ${comment.postId}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save comment", e)
        }
    }

    suspend fun fetchAllComments(): List<Comment> {
        return try {
            val snapshot = db.collection(COLL_COMMENTS)
                .get()
                .awaitResult()
            snapshot.documents.mapNotNull { doc ->
                val id = doc.getLong("id")?.toInt() ?: doc.id.toIntOrNull() ?: return@mapNotNull null
                val postId = doc.getLong("postId")?.toInt() ?: 0
                val authorId = doc.getLong("authorId")?.toInt() ?: 0
                val authorName = doc.getString("authorName") ?: ""
                val content = doc.getString("content") ?: ""
                val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                val isPinned = doc.getBoolean("pinned") ?: doc.getBoolean("isPinned") ?: false
                Comment(id, postId, authorId, authorName, content, timestamp, isPinned)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch comments", e)
            emptyList()
        }
    }

    suspend fun deleteComment(commentId: Int) {
        try {
            db.collection(COLL_COMMENTS)
                .document(commentId.toString())
                .delete()
                .awaitResult()
            Log.d(TAG, "Successfully deleted comment ID: $commentId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete comment", e)
        }
    }

    // ==========================================
    // 4. LIKES PARTNERSHIP
    // ==========================================

    suspend fun saveLike(like: Like, delete: Boolean = false) {
        try {
            val docId = "${like.userId}_${like.postId}"
            val docRef = db.collection(COLL_LIKES).document(docId)
            if (delete) {
                docRef.delete().awaitResult()
            } else {
                docRef.set(likeToMap(like)).awaitResult()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save/delete like", e)
        }
    }

    suspend fun fetchAllLikes(): List<Like> {
        return try {
            val snapshot = db.collection(COLL_LIKES)
                .get()
                .awaitResult()
            snapshot.documents.mapNotNull { doc ->
                val id = doc.getLong("id")?.toInt() ?: 0
                val userId = doc.getLong("userId")?.toInt() ?: 0
                val postId = doc.getLong("postId")?.toInt() ?: 0
                Like(id, userId, postId)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch likes", e)
            emptyList()
        }
    }

    // ==========================================
    // 5. BOOKMARKS PARTNERSHIP
    // ==========================================

    suspend fun saveBookmark(bookmark: Bookmark, delete: Boolean = false) {
        try {
            val docId = "${bookmark.userId}_${bookmark.postId}"
            val docRef = db.collection(COLL_BOOKMARKS).document(docId)
            if (delete) {
                docRef.delete().awaitResult()
            } else {
                docRef.set(bookmarkToMap(bookmark)).awaitResult()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save/delete bookmark", e)
        }
    }

    suspend fun fetchAllBookmarks(): List<Bookmark> {
        return try {
            val snapshot = db.collection(COLL_BOOKMARKS)
                .get()
                .awaitResult()
            snapshot.documents.mapNotNull { doc ->
                val id = doc.getLong("id")?.toInt() ?: 0
                val userId = doc.getLong("userId")?.toInt() ?: 0
                val postId = doc.getLong("postId")?.toInt() ?: 0
                Bookmark(id, userId, postId)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch bookmarks", e)
            emptyList()
        }
    }

    // ==========================================
    // 6. FOLLOWS PARTNERSHIP
    // ==========================================

    suspend fun saveFollow(follow: Follow, delete: Boolean = false) {
        try {
            val docId = "${follow.followerId}_${follow.followingId}"
            val docRef = db.collection(COLL_FOLLOWS).document(docId)
            if (delete) {
                docRef.delete().awaitResult()
            } else {
                docRef.set(followToMap(follow)).awaitResult()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save/delete follow", e)
        }
    }

    suspend fun fetchAllFollows(): List<Follow> {
        return try {
            val snapshot = db.collection(COLL_FOLLOWS)
                .get()
                .awaitResult()
            snapshot.documents.mapNotNull { doc ->
                val id = doc.getLong("id")?.toInt() ?: 0
                val followerId = doc.getLong("followerId")?.toInt() ?: 0
                val followingId = doc.getLong("followingId")?.toInt() ?: 0
                Follow(id, followerId, followingId)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch follows", e)
            emptyList()
        }
    }

    // ==========================================
    // 7. CHAT MESSAGES
    // ==========================================

    suspend fun saveChatMessage(chatMessage: ChatMessage) {
        try {
            db.collection(COLL_CHAT_MESSAGES)
                .document(chatMessage.id.toString())
                .set(chatMessageToMap(chatMessage), SetOptions.merge())
                .awaitResult()
            Log.d(TAG, "Successfully saved chat message from ID: ${chatMessage.senderId}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save chat message", e)
        }
    }

    suspend fun fetchAllChatMessages(): List<ChatMessage> {
        return try {
            val snapshot = db.collection(COLL_CHAT_MESSAGES)
                .get()
                .awaitResult()
            snapshot.documents.mapNotNull { doc ->
                val id = doc.getLong("id")?.toInt() ?: doc.id.toIntOrNull() ?: return@mapNotNull null
                val senderId = doc.getLong("senderId")?.toInt() ?: 0
                val receiverId = doc.getLong("receiverId")?.toInt() ?: 0
                val text = doc.getString("text") ?: ""
                val type = doc.getString("type") ?: "text"
                val mediaUri = doc.getString("mediaUri")
                val spotifyTrackId = doc.getString("spotifyTrackId")
                val spotifyTrackName = doc.getString("spotifyTrackName")
                val spotifyTrackArtist = doc.getString("spotifyTrackArtist")
                val spotifyTrackImageUrl = doc.getString("spotifyTrackImageUrl")
                val spotifyTrackPreviewUrl = doc.getString("spotifyTrackPreviewUrl")
                val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                val isEdited = doc.getBoolean("isEdited") ?: false
                val isReported = doc.getBoolean("isReported") ?: false
                val replyToId = doc.getLong("replyToId")?.toInt()
                val isRead = doc.getBoolean("isRead") ?: false
                ChatMessage(id, senderId, receiverId, text, type, mediaUri, spotifyTrackId, spotifyTrackName, spotifyTrackArtist, spotifyTrackImageUrl, spotifyTrackPreviewUrl, timestamp, isEdited, isReported, replyToId, isRead)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch chat messages", e)
            emptyList()
        }
    }

    suspend fun deleteChatMessage(messageId: Int) {
        try {
            db.collection(COLL_CHAT_MESSAGES)
                .document(messageId.toString())
                .delete()
                .awaitResult()
            Log.d(TAG, "Successfully deleted chat message: $messageId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete chat message", e)
        }
    }

    // ==========================================
    // 8. TYPING STATUS
    // ==========================================

    suspend fun setTypingStatus(userId: Int, receiverId: Int, isTyping: Boolean) {
        try {
            val docId = "${userId}_to_${receiverId}"
            db.collection(COLL_TYPING_STATUS)
                .document(docId)
                .set(mapOf(
                    "userId" to userId,
                    "receiverId" to receiverId,
                    "isTyping" to isTyping,
                    "timestamp" to System.currentTimeMillis()
                ))
                .awaitResult()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set typing status", e)
        }
    }

    suspend fun saveUserEmailMapping(username: String, email: String) {
        try {
            db.collection(COLL_USER_EMAILS)
                .document(username)
                .set(mapOf("email" to email))
                .awaitResult()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save username-email mapping", e)
        }
    }

    suspend fun getEmailByUsername(username: String): String? {
        return try {
            val doc = db.collection(COLL_USER_EMAILS).document(username).get().awaitResult()
            doc.getString("email")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get email for username $username", e)
            null
        }
    }

    suspend fun getAliasByEmail(email: String): String? {
        return try {
            val querySnapshot = db.collection(COLL_USER_EMAILS)
                .whereEqualTo("email", email)
                .get()
                .awaitResult()
            if (!querySnapshot.isEmpty) {
                querySnapshot.documents.firstOrNull()?.id
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get alias by email $email", e)
            null
        }
    }

    // ==========================================
    // 9. STORY METHODS
    // ==========================================

    suspend fun saveStory(story: Story) {
        try {
            db.collection(COLL_STORIES)
                .document(story.id.toString())
                .set(storyToMap(story), SetOptions.merge())
                .awaitResult()
            Log.d(TAG, "Successfully saved story from: ${story.authorName}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save story", e)
        }
    }

    suspend fun fetchAllStories(): List<Story> {
        return try {
            val snapshot = db.collection(COLL_STORIES)
                .get()
                .awaitResult()
            val now = System.currentTimeMillis()
            snapshot.documents.mapNotNull { doc ->
                val idLong = doc.getLong("id") ?: doc.id.toLongOrNull() ?: return@mapNotNull null
                val id = idLong.toInt()
                val authorId = doc.getLong("authorId")?.toInt() ?: 0
                val authorName = doc.getString("authorName") ?: ""
                val mediaUri = doc.getString("mediaUri")
                val text = doc.getString("text") ?: ""
                val spotifyTrackId = doc.getString("spotifyTrackId")
                val spotifyTrackName = doc.getString("spotifyTrackName")
                val spotifyTrackArtist = doc.getString("spotifyTrackArtist")
                val spotifyTrackImageUrl = doc.getString("spotifyTrackImageUrl")
                val spotifyTrackPreviewUrl = doc.getString("spotifyTrackPreviewUrl")
                val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                val expiresAt = doc.getLong("expiresAt") ?: (timestamp + 86400000L)
                if (expiresAt <= now) {
                    return@mapNotNull null
                }
                Story(id, authorId, authorName, mediaUri, text, spotifyTrackId, spotifyTrackName, spotifyTrackArtist, spotifyTrackImageUrl, spotifyTrackPreviewUrl, timestamp, expiresAt)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch stories", e)
            emptyList()
        }
    }

    suspend fun deleteStory(storyId: Int) {
        try {
            db.collection(COLL_STORIES)
                .document(storyId.toString())
                .delete()
                .awaitResult()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete story", e)
        }
    }
}
