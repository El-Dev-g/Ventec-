package com.example.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BlogRepository(
    private val userDao: UserDao,
    private val postDao: PostDao,
    private val commentDao: CommentDao,
    private val bookmarkDao: BookmarkDao,
    private val likeDao: LikeDao,
    private val followDao: FollowDao,
    private val chatMessageDao: ChatMessageDao,
    private val storyDao: StoryDao
) {
    private val _allPosts = MutableStateFlow<List<Post>>(emptyList())
    val allPosts: Flow<List<Post>> = _allPosts.map { list -> list.filter { !it.mediaUri.isNullOrBlank() || !it.spotifyTrackId.isNullOrBlank() } }

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: Flow<List<User>> = _allUsers

    private val _allStories = MutableStateFlow<List<Story>>(emptyList())
    val allStories: Flow<List<Story>> = _allStories

    private val _allComments = MutableStateFlow<List<Comment>>(emptyList())
    val allComments: Flow<List<Comment>> = _allComments

    private val _allLikes = MutableStateFlow<List<Like>>(emptyList())
    val allLikes: Flow<List<Like>> = _allLikes

    private val _allBookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val allBookmarks: Flow<List<Bookmark>> = _allBookmarks

    private val _allFollows = MutableStateFlow<List<Follow>>(emptyList())
    val allFollows: Flow<List<Follow>> = _allFollows

    private val _allChatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val allChatMessages: Flow<List<ChatMessage>> = _allChatMessages

    private val _typingUsers = MutableStateFlow<Map<Int, Boolean>>(emptyMap()) // receiverId -> isTyping
    val typingUsers: Flow<Map<Int, Boolean>> = _typingUsers
    
    fun getAllUsersFlow(): Flow<List<User>> = _allUsers

    fun getMessagesBetweenUsers(userId1: Int, userId2: Int): Flow<List<ChatMessage>> =
        _allChatMessages.map { list ->
            list.filter { (it.senderId == userId1 && it.receiverId == userId2) || (it.senderId == userId2 && it.receiverId == userId1) }
        }

    fun getAllMessagesFlow(): Flow<List<ChatMessage>> = _allChatMessages

    suspend fun sendChatMessage(senderId: Int, receiverId: Int, text: String, type: String = "text", mediaUri: String? = null, replyToId: Int? = null, spotifyTrackId: String? = null, spotifyTrackName: String? = null, spotifyTrackArtist: String? = null, spotifyTrackImageUrl: String? = null, spotifyTrackPreviewUrl: String? = null) {
        val generatedId = (System.currentTimeMillis() % 100000000).toInt() + kotlin.random.Random.nextInt(5000)
        val chatMessage = ChatMessage(
            id = generatedId,
            senderId = senderId,
            receiverId = receiverId,
            text = text,
            type = type,
            mediaUri = mediaUri,
            spotifyTrackId = spotifyTrackId,
            spotifyTrackName = spotifyTrackName,
            spotifyTrackArtist = spotifyTrackArtist,
            spotifyTrackImageUrl = spotifyTrackImageUrl,
            spotifyTrackPreviewUrl = spotifyTrackPreviewUrl,
            timestamp = System.currentTimeMillis(),
            replyToId = replyToId
        )
        try {
            FirestoreService.saveChatMessage(chatMessage)
        } catch (e: Exception) {
            Log.e("BlogRepository", "Failed to send chat message to Firestore: ${e.message}")
        }
    }

    suspend fun setTypingStatus(userId: Int, receiverId: Int, isTyping: Boolean) {
        try {
            FirestoreService.setTypingStatus(userId, receiverId, isTyping)
        } catch (e: Exception) {
            Log.e("BlogRepository", "Failed to set typing status", e)
        }
    }

    suspend fun updateChatMessage(message: ChatMessage) {
        try {
            FirestoreService.saveChatMessage(message)
        } catch (e: Exception) {
            Log.e("BlogRepository", "Failed to update chat message: ${e.message}")
        }
    }

    suspend fun deleteChatMessage(messageId: Int) {
        try {
            FirestoreService.deleteChatMessage(messageId)
        } catch (e: Exception) {
            Log.e("BlogRepository", "Failed to delete chat message: ${e.message}")
        }
    }

    suspend fun markMessagesAsRead(senderId: Int, receiverId: Int) {
        val messagesToUpdate = _allChatMessages.value.filter {
            it.senderId == senderId && it.receiverId == receiverId && !it.isRead
        }
        for (message in messagesToUpdate) {
            try {
                FirestoreService.saveChatMessage(message.copy(isRead = true))
            } catch (e: Exception) {
                Log.e("BlogRepository", "Failed to mark message as read: ${e.message}")
            }
        }
    }

    suspend fun reportChatMessage(messageId: Int) {
        try {
            val message = _allChatMessages.value.find { it.id == messageId } ?: return
            FirestoreService.saveChatMessage(message.copy(isReported = true))
        } catch (e: Exception) {
            Log.e("BlogRepository", "Failed to report chat message: ${e.message}")
        }
    }

    fun getLikeCount(postId: Int): Flow<Int> = _allLikes.map { list -> list.count { it.postId == postId } }
    fun isLiked(userId: Int, postId: Int): Flow<Boolean> = _allLikes.map { list -> list.any { it.userId == userId && it.postId == postId } }
    
    suspend fun toggleLike(userId: Int, postId: Int, isLiked: Boolean) {
        val likeItem = Like(
            id = (System.currentTimeMillis() % 100000000).toInt() + kotlin.random.Random.nextInt(5000),
            userId = userId,
            postId = postId
        )
        try {
            FirestoreService.saveLike(likeItem, delete = isLiked)
        } catch (e: Exception) {
            Log.e("BlogRepository", "Failed to toggleLike on Firestore: ${e.message}")
        }
    }

    fun getFollowerCount(userId: Int): Flow<Int> = _allFollows.map { list -> list.count { it.followingId == userId } }
    fun getFollowingCount(userId: Int): Flow<Int> = _allFollows.map { list -> list.count { it.followerId == userId } }
    fun isFollowing(followerId: Int, followingId: Int): Flow<Boolean> = _allFollows.map { list -> list.any { it.followerId == followerId && it.followingId == followingId } }

    suspend fun toggleFollow(followerId: Int, followingId: Int, isFollowing: Boolean) {
        val followItem = Follow(
            id = (System.currentTimeMillis() % 100000000).toInt() + kotlin.random.Random.nextInt(5000),
            followerId = followerId,
            followingId = followingId
        )
        try {
            FirestoreService.saveFollow(followItem, delete = isFollowing)
        } catch (e: Exception) {
            Log.e("BlogRepository", "Failed to toggleFollow on Firestore: ${e.message}")
        }
    }

    fun getDrafts(userId: Int): Flow<List<Post>> = _allPosts.map { list -> list.filter { it.authorId == userId && it.isDraft } }
    fun getBookmarkedPosts(userId: Int): Flow<List<Post>> = combine(_allPosts, _allBookmarks) { posts, bookmarks ->
        val bookmarkedIds = bookmarks.filter { it.userId == userId }.map { it.postId }
        posts.filter { it.id in bookmarkedIds }
    }
    fun getLikedPosts(userId: Int): Flow<List<Post>> = combine(_allPosts, _allLikes) { posts, likes ->
        val likedIds = likes.filter { it.userId == userId }.map { it.postId }
        posts.filter { it.id in likedIds }
    }
    fun isBookmarked(userId: Int, postId: Int): Flow<Boolean> = _allBookmarks.map { list -> list.any { it.userId == userId && it.postId == postId } }

    suspend fun toggleBookmark(userId: Int, postId: Int, isBookmarked: Boolean) {
        val bookmarkItem = Bookmark(
            id = (System.currentTimeMillis() % 100000000).toInt() + kotlin.random.Random.nextInt(5000),
            userId = userId,
            postId = postId
        )
        try {
            FirestoreService.saveBookmark(bookmarkItem, delete = isBookmarked)
        } catch (e: Exception) {
            Log.e("BlogRepository", "Failed to toggleBookmark on Firestore: ${e.message}")
        }
    }

    fun getPostById(postId: Int): Flow<Post?> = _allPosts.map { list -> list.find { it.id == postId } }
    fun getCommentsForPost(postId: Int): Flow<List<Comment>> = _allComments.map { list -> list.filter { it.postId == postId }.sortedBy { it.timestamp } }

    fun getUserByIdFlow(userId: Int): Flow<User?> = _allUsers.map { list -> list.find { it.id == userId } }
    fun getFollowers(userId: Int): Flow<List<User>> = combine(_allUsers, _allFollows) { users, follows ->
        val followerIds = follows.filter { it.followingId == userId }.map { it.followerId }
        users.filter { it.id in followerIds }
    }
    fun getFollowing(userId: Int): Flow<List<User>> = combine(_allUsers, _allFollows) { users, follows ->
        val followingIds = follows.filter { it.followerId == userId }.map { it.followingId }
        users.filter { it.id in followingIds }
    }
    
    suspend fun getUserByUsername(username: String): User? {
        return try {
            FirestoreService.fetchAllUsers().find { it.username.equals(username, ignoreCase = true) }
        } catch (e: Exception) {
            _allUsers.value.find { it.username.equals(username, ignoreCase = true) }
        }
    }

    suspend fun updateUser(user: User) {
        try {
            FirestoreService.saveUser(user)
        } catch (e: Exception) {
            Log.e("BlogRepository", "Firestore saveUser failed: ${e.message}")
        }
    }

    suspend fun insertUser(user: User) {
        try {
            FirestoreService.saveUser(user)
        } catch (e: Exception) {
            Log.e("BlogRepository", "Firestore saveUser failed on insert: ${e.message}")
        }
    }

    suspend fun getOrCreateUser(username: String): User {
        try {
            val firestoreUsers = FirestoreService.fetchAllUsers()
            val match = firestoreUsers.find { it.username.equals(username, ignoreCase = true) }
            if (match != null) {
                return match
            }
        } catch (e: Exception) {
            Log.e("BlogRepository", "Failed to check users on firestore", e)
        }

        val generatedId = (System.currentTimeMillis() % 100000000).toInt() + kotlin.random.Random.nextInt(5000)
        val newUser = User(id = generatedId, username = username)
        try {
            FirestoreService.saveUser(newUser)
        } catch (e: Exception) {
            Log.e("BlogRepository", "Firestore saveUser failed on new user: ${e.message}")
        }
        return newUser
    }

    suspend fun deleteUserAndData(userId: Int) {
        try {
            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            auth.currentUser?.delete()
        } catch (e: Exception) {
            Log.e("BlogRepository", "Failed to delete account from Firebase Auth", e)
        }
    }

    suspend fun updateLastSeen(userId: Int) {
        try {
            FirestoreService.updateLastSeen(userId)
        } catch (e: Exception) {
            Log.e("BlogRepository", "Failed to update lastSeen", e)
        }
    }

    suspend fun createPost(authorId: Int, authorName: String, title: String, content: String, categories: String, tags: String, mediaUri: String?, isDraft: Boolean, commentsDisabled: Boolean = false, spotifyTrackId: String? = null, spotifyTrackName: String? = null, spotifyTrackArtist: String? = null, spotifyTrackImageUrl: String? = null, spotifyTrackPreviewUrl: String? = null) {
        if (mediaUri.isNullOrBlank() && spotifyTrackId.isNullOrBlank()) {
            Log.w("BlogRepository", "Cannot create text-only post without media or Spotify track")
            return
        }
        val generatedId = (System.currentTimeMillis() % 100000000).toInt() + kotlin.random.Random.nextInt(5000)
        val post = Post(
            id = generatedId,
            authorId = authorId,
            authorName = authorName,
            title = title,
            content = content,
            categories = categories,
            tags = tags,
            mediaUri = mediaUri,
            spotifyTrackId = spotifyTrackId,
            spotifyTrackName = spotifyTrackName,
            spotifyTrackArtist = spotifyTrackArtist,
            spotifyTrackImageUrl = spotifyTrackImageUrl,
            spotifyTrackPreviewUrl = spotifyTrackPreviewUrl,
            isDraft = isDraft,
            commentsDisabled = commentsDisabled
        )
        try {
            FirestoreService.savePost(post)
        } catch (e: Exception) {
            Log.e("BlogRepository", "Firestore savePost failed: ${e.message}")
        }
    }

    suspend fun updatePost(post: Post) {
        if (post.mediaUri.isNullOrBlank() && post.spotifyTrackId.isNullOrBlank()) {
            Log.w("BlogRepository", "Cannot update post to be text-only without media or Spotify track")
            return
        }
        try {
            FirestoreService.savePost(post)
        } catch (e: Exception) {
            Log.e("BlogRepository", "Firestore savePost failed on update: ${e.message}")
        }
    }

    suspend fun deletePost(postId: Int) {
        try {
            FirestoreService.deletePost(postId)
        } catch (e: Exception) {
            Log.e("BlogRepository", "Firestore deletePost failed: ${e.message}")
        }
    }

    suspend fun deleteComment(commentId: Int) {
        try {
            FirestoreService.deleteComment(commentId)
        } catch (e: Exception) {
            Log.e("BlogRepository", "Firestore deleteComment failed: ${e.message}")
        }
    }

    suspend fun updateComment(comment: Comment) {
        try {
            FirestoreService.saveComment(comment)
        } catch (e: Exception) {
            Log.e("BlogRepository", "Firestore saveComment failed: ${e.message}")
        }
    }

    suspend fun createComment(postId: Int, authorId: Int, authorName: String, content: String) {
        val generatedId = (System.currentTimeMillis() % 100000000).toInt() + kotlin.random.Random.nextInt(5000)
        val comment = Comment(
            id = generatedId,
            postId = postId,
            authorId = authorId,
            authorName = authorName,
            content = content
        )
        try {
            FirestoreService.saveComment(comment)
        } catch (e: Exception) {
            Log.e("BlogRepository", "Firestore saveComment failed on create: ${e.message}")
        }
    }

    suspend fun createStory(authorId: Int, authorName: String, mediaUri: String?, text: String = "", spotifyTrackId: String? = null, spotifyTrackName: String? = null, spotifyTrackArtist: String? = null, spotifyTrackImageUrl: String? = null, spotifyTrackPreviewUrl: String? = null) {
        val generatedId = (System.currentTimeMillis() % 100000000).toInt() + kotlin.random.Random.nextInt(5000)
        val now = System.currentTimeMillis()
        val story = Story(
            id = generatedId,
            authorId = authorId,
            authorName = authorName,
            mediaUri = mediaUri,
            text = text,
            spotifyTrackId = spotifyTrackId,
            spotifyTrackName = spotifyTrackName,
            spotifyTrackArtist = spotifyTrackArtist,
            spotifyTrackImageUrl = spotifyTrackImageUrl,
            spotifyTrackPreviewUrl = spotifyTrackPreviewUrl,
            timestamp = now,
            expiresAt = now + 86400000L // 24 hours
        )
        try {
            FirestoreService.saveStory(story)
            val current = _allStories.value.filter { it.expiresAt > System.currentTimeMillis() }
            _allStories.value = (listOf(story) + current).sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            Log.e("BlogRepository", "Firestore saveStory failed: ${e.message}")
        }
    }

    suspend fun deleteStory(storyId: Int) {
        try {
            FirestoreService.deleteStory(storyId)
        } catch (e: Exception) {
            Log.e("BlogRepository", "Firestore deleteStory failed: ${e.message}")
        }
    }

    suspend fun syncAllFromFirestore() {
        try {
            _allPosts.value = FirestoreService.fetchAllPosts().sortedByDescending { it.timestamp }
            _allUsers.value = FirestoreService.fetchAllUsers()
            val now = System.currentTimeMillis()
            _allStories.value = FirestoreService.fetchAllStories().filter { it.expiresAt > now }.sortedByDescending { it.timestamp }
            _allComments.value = FirestoreService.fetchAllComments().sortedBy { it.timestamp }
            _allLikes.value = FirestoreService.fetchAllLikes()
            _allBookmarks.value = FirestoreService.fetchAllBookmarks()
            _allFollows.value = FirestoreService.fetchAllFollows()
            _allChatMessages.value = FirestoreService.fetchAllChatMessages()
            Log.d("BlogRepository", "Initial fetch from Firestore finished successfully!")
        } catch (e: Exception) {
            Log.e("BlogRepository", "Failed to initially fetch all entries from Firestore", e)
        }
    }

    fun startLiveFirestoreSync(scope: kotlinx.coroutines.CoroutineScope) {
        val firestore = try {
            FirestoreService.db
        } catch (e: Exception) {
            Log.e("BlogRepository", "Firebase not initialized/configured: ${e.message}")
            return
        }

        // 1. Live Posts Listen
        try {
            firestore.collection("posts")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("BlogRepository", "Live posts listen failed: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        scope.launch {
                            try {
                                val newPosts = snapshot.documents.mapNotNull { doc ->
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
                                _allPosts.value = newPosts.sortedByDescending { it.timestamp }
                            } catch (e: Exception) {
                                Log.e("BlogRepository", "Error saving live posts: ${e.message}")
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("BlogRepository", "Posts snapshot init crash: ${e.message}")
        }

        // 2. Live Comments Listen
        try {
            firestore.collection("comments")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("BlogRepository", "Live comments listen failed: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        scope.launch {
                            try {
                                val parsedComments = snapshot.documents.mapNotNull { doc ->
                                    val id = doc.getLong("id")?.toInt() ?: doc.id.toIntOrNull() ?: return@mapNotNull null
                                    val postId = doc.getLong("postId")?.toInt() ?: 0
                                    val authorId = doc.getLong("authorId")?.toInt() ?: 0
                                    val authorName = doc.getString("authorName") ?: ""
                                    val content = doc.getString("content") ?: ""
                                    val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                                    val isPinned = doc.getBoolean("pinned") ?: doc.getBoolean("isPinned") ?: false
                                    
                                    Comment(id, postId, authorId, authorName, content, timestamp, isPinned)
                                }
                                _allComments.value = parsedComments.sortedBy { it.timestamp }
                            } catch (e: Exception) {
                                Log.e("BlogRepository", "Error saving live comments: ${e.message}")
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("BlogRepository", "Comments snapshot init: ${e.message}")
        }

        // 3. Live Chat Messages Listen
        try {
            firestore.collection("chat_messages")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("BlogRepository", "Live chats listen failed: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        scope.launch {
                            try {
                                val parsedMsgs = snapshot.documents.mapNotNull { doc ->
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
                                _allChatMessages.value = parsedMsgs.sortedBy { it.timestamp }
                            } catch (e: Exception) {
                                Log.e("BlogRepository", "Error saving live chats: ${e.message}")
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("BlogRepository", "Chats snapshot init: ${e.message}")
        }

        // 4. Live Users Listen
        try {
            firestore.collection("users")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        scope.launch {
                            try {
                                val parsedUsers = snapshot.documents.mapNotNull { doc ->
                                    val idLong = doc.getLong("id") ?: doc.id.toLongOrNull() ?: return@mapNotNull null
                                    val id = idLong.toInt()
                                    val username = doc.getString("username") ?: ""
                                    val fullName = doc.getString("fullName") ?: ""
                                    val bio = doc.getString("bio") ?: ""
                                    val profilePicUri = doc.getString("profilePicUri")
                                    val usernameLastChangedAt = doc.getLong("usernameLastChangedAt") ?: 0L
                                    val lastSeen = doc.getLong("lastSeen") ?: System.currentTimeMillis()
                                    
                                    User(id, username, fullName, bio, profilePicUri, usernameLastChangedAt, lastSeen)
                                }
                                _allUsers.value = parsedUsers
                            } catch (e: Exception) {
                                // Ignore parsing errors
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            // Safe ignore
        }

        // 5. Live Likes Listen
        try {
            firestore.collection("likes")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        scope.launch {
                            try {
                                val parsedLikes = snapshot.documents.mapNotNull { doc ->
                                    val id = doc.getLong("id")?.toInt() ?: 0
                                    val userId = doc.getLong("userId")?.toInt() ?: 0
                                    val postId = doc.getLong("postId")?.toInt() ?: 0
                                    Like(id, userId, postId)
                                }
                                _allLikes.value = parsedLikes
                            } catch (e: Exception) {
                                // FKEY safe catch
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            // Ignore
        }

        // 6. Live Bookmarks Listen
        try {
            firestore.collection("bookmarks")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        scope.launch {
                            try {
                                val parsedBk = snapshot.documents.mapNotNull { doc ->
                                    val id = doc.getLong("id")?.toInt() ?: 0
                                    val userId = doc.getLong("userId")?.toInt() ?: 0
                                    val postId = doc.getLong("postId")?.toInt() ?: 0
                                    Bookmark(id, userId, postId)
                                }
                                _allBookmarks.value = parsedBk
                            } catch (e: Exception) {
                                // Catch
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            // Ignore
        }

        // 7. Live Follows Listen
        try {
            firestore.collection("follows")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        scope.launch {
                            try {
                                val parsedFollows = snapshot.documents.mapNotNull { doc ->
                                    val id = doc.getLong("id")?.toInt() ?: 0
                                    val followerId = doc.getLong("followerId")?.toInt() ?: 0
                                    val followingId = doc.getLong("followingId")?.toInt() ?: 0
                                    Follow(id, followerId, followingId)
                                }
                                _allFollows.value = parsedFollows
                            } catch (e: Exception) {
                                // Catch
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            // Ignore
        }

        // 8. Live Typing Status Listen
        try {
            firestore.collection("typing_status")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        val typingMap = mutableMapOf<Int, Boolean>()
                        snapshot.documents.forEach { doc ->
                            val userId = doc.getLong("userId")?.toInt() ?: return@forEach
                            val isTyping = doc.getBoolean("isTyping") ?: false
                            val timestamp = doc.getLong("timestamp") ?: 0L
                            // Only consider status valid if it was updated in the last 10 seconds
                            if (System.currentTimeMillis() - timestamp < 10000) {
                                typingMap[userId] = isTyping
                            }
                        }
                        _typingUsers.value = typingMap
                    }
                }
        } catch (e: Exception) {
            // Safe
        }

        // 9. Live Stories Listen
        try {
            firestore.collection("stories")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    if (snapshot != null) {
                        scope.launch {
                            try {
                                val now = System.currentTimeMillis()
                                val parsedStories = snapshot.documents.mapNotNull { doc ->
                                    val id = doc.getLong("id")?.toInt() ?: doc.id.toIntOrNull() ?: return@mapNotNull null
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
                                _allStories.value = parsedStories.sortedByDescending { it.timestamp }
                            } catch (e: Exception) {
                                // Catch
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            // Ignore
        }
    }

    suspend fun boostPost(postId: Int, fee: Double) {
        try {
            val db = FirestoreService.db
            db.collection("posts")
                .document(postId.toString())
                .update(mapOf(
                    "isPromoted" to true,
                    "promoteFee" to fee
                ))
        } catch (e: Exception) {
            Log.e("BlogRepository", "Failed to live-boost post: ${e.message}")
        }
    }

    suspend fun incrementImpressions(postId: Int) {
        try {
            val db = FirestoreService.db
            db.collection("posts")
                .document(postId.toString())
                .update("impressions", com.google.firebase.firestore.FieldValue.increment(1))
        } catch (e: Exception) {
            Log.e("BlogRepository", "Failed to increment impressions: ${e.message}")
        }
    }

    suspend fun incrementClicks(postId: Int) {
        val currentList = _allPosts.value
        val updatedList = currentList.map {
            if (it.id == postId) {
                it.copy(clicks = it.clicks + 1)
            } else {
                it
            }
        }
        _allPosts.value = updatedList

        try {
            val db = FirestoreService.db
            db.collection("posts")
                .document(postId.toString())
                .update("clicks", com.google.firebase.firestore.FieldValue.increment(1))
        } catch (e: Exception) {
            Log.e("BlogRepository", "Failed to increment clicks: ${e.message}")
        }
    }
}
