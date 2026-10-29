package com.example.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.BlogRepository
import com.example.data.Comment
import com.example.data.Post
import com.example.data.SessionManager
import com.example.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.resume

class BlogViewModel(
    private val repository: BlogRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(sessionManager.getLoggedInUser())
    val currentUser: StateFlow<User?> = _currentUser

    private val _knownUserIds = MutableStateFlow(sessionManager.getKnownUserIds())
    val knownUserIds: StateFlow<Set<String>> = _knownUserIds

    val isDarkTheme = MutableStateFlow(sessionManager.isDarkTheme())

    val pendingCallAnswerFromServiceUser = androidx.compose.runtime.mutableStateOf<Int?>(null)
    val pendingCallAnswerType = androidx.compose.runtime.mutableStateOf<String?>(null)

    fun setDarkTheme(enabled: Boolean) {
        sessionManager.setDarkTheme(enabled)
        isDarkTheme.value = enabled
    }

    private var userCollectJob: kotlinx.coroutines.Job? = null

    private fun observeUser(userId: Int) {
        userCollectJob?.cancel()
        userCollectJob = viewModelScope.launch {
            repository.getUserByIdFlow(userId).collect { dbUser ->
                if (dbUser != null) {
                    _currentUser.value = dbUser
                }
            }
        }
    }

    private val _notifications = MutableStateFlow<List<AlertNotification>>(emptyList())
    val notifications: StateFlow<List<AlertNotification>> = _notifications

    val allStories: StateFlow<List<com.example.data.Story>> = repository.allStories
        .map { stories ->
            val now = System.currentTimeMillis()
            stories.filter { it.expiresAt > now }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createStory(mediaUri: String?, text: String = "", spotifyTrackId: String? = null, spotifyTrackName: String? = null, spotifyTrackArtist: String? = null, spotifyTrackImageUrl: String? = null, spotifyTrackPreviewUrl: String? = null, onComplete: () -> Unit = {}) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.createStory(user.id, user.username, mediaUri, text, spotifyTrackId, spotifyTrackName, spotifyTrackArtist, spotifyTrackImageUrl, spotifyTrackPreviewUrl)
            onComplete()
        }
    }

    fun deleteStory(storyId: Int) {
        viewModelScope.launch {
            repository.deleteStory(storyId)
        }
    }

    fun addNotification(title: String, text: String, type: String, associatedUserId: Int? = null) {
        val current = _notifications.value
        _notifications.value = listOf(
            AlertNotification(title = title, text = text, type = type, associatedUserId = associatedUserId)
        ) + current
    }

    fun markNotificationAsRead(id: String, isRead: Boolean = true) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = isRead) else it
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun removeNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    fun clearNotifications() {
        _notifications.value = emptyList()
    }

    init {
        val loggedIn = sessionManager.getLoggedInUser()
        if (loggedIn != null) {
            observeUser(loggedIn.id)
            
            // Periodically update lastSeen
            viewModelScope.launch {
                while (true) {
                    repository.updateLastSeen(loggedIn.id)
                    kotlinx.coroutines.delay(60_000) // Every minute
                }
            }
        }
        // Start Live Firestore database fetching / synchronization in background
        repository.startLiveFirestoreSync(viewModelScope)

        viewModelScope.launch {
            try {
                if (loggedIn != null) {
                    val existing = repository.getUserByIdFlow(loggedIn.id).first()
                    if (existing == null) {
                        repository.insertUser(loggedIn)
                    }
                }
            } catch (e: Exception) {
                // Ignore initial database verification errors
            }
            try {
                syncPosts()
            } catch (e: Exception) {
                // Safe fall-back
            }
        }
    }

    private val _selectedFilter = MutableStateFlow<String?>(null)
    val selectedFilter: StateFlow<String?> = _selectedFilter

    val hiddenPostIds = MutableStateFlow<Set<Int>>(emptySet())
    val reportedPostIds = MutableStateFlow<Set<Int>>(emptySet())

    fun hidePost(postId: Int) {
        hiddenPostIds.value = hiddenPostIds.value + postId
    }

    fun reportPost(postId: Int) {
        reportedPostIds.value = reportedPostIds.value + postId
        // When reporting, we also default to hiding it to prevent further annoyance
        hiddenPostIds.value = hiddenPostIds.value + postId
    }

    val allPosts: StateFlow<List<Post>> = combine(repository.allPosts, _selectedFilter, hiddenPostIds) { posts, filter, hidden ->
        val visible = posts.filter { it.id !in hidden && !it.mediaUri.isNullOrBlank() }
        val filtered = if (filter.isNullOrBlank()) {
            visible
        } else {
            visible.filter { post ->
                post.title.contains(filter, ignoreCase = true) ||
                post.content.contains(filter, ignoreCase = true) ||
                post.authorName.contains(filter, ignoreCase = true) ||
                post.categories.contains(filter, ignoreCase = true) ||
                post.tags.contains(filter, ignoreCase = true)
            }
        }
        filtered.sortedWith(compareByDescending<Post> { it.isPromoted }.thenByDescending { it.timestamp })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setFilter(filter: String?) {
        _selectedFilter.value = filter
    }

    private fun getFirebaseEmail(email: String): String {
        return if (email.endsWith("@zooz.secure", ignoreCase = true)) {
            "${email.substringBefore("@")}@gmail.com"
        } else if (email.endsWith("@zooz.official", ignoreCase = true)) {
            "${email.substringBefore("@")}@gmail.com"
        } else if (!email.contains("@")) {
            "$email@gmail.com"
        } else {
            email
        }
    }

    suspend fun resetPassword(email: String): Boolean = kotlin.coroutines.suspendCoroutine { continuation ->
        val apiKey = com.example.BuildConfig.FIREBASE_API_KEY
        if (apiKey.isBlank() || apiKey == "YOUR_FIREBASE_API_KEY") {
            // Simulated reset password for local DB
            continuation.resume(true)
            return@suspendCoroutine
        }

        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        val rawEmail = if (!email.contains("@")) {
            var resolved: String? = null
            kotlinx.coroutines.runBlocking {
                resolved = com.example.data.FirestoreService.getEmailByUsername(email)
            }
            resolved ?: "$email@zooz.secure"
        } else {
            email
        }
        val treatedEmail = getFirebaseEmail(rawEmail)
        
        auth.sendPasswordResetEmail(treatedEmail).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(true)
            } else {
                continuation.resume(false)
            }
        }
    }

    fun loginWithSSO(username: String, onComplete: () -> Unit, onError: (String) -> Unit) {
        // For Zooz SSO, we always leverage the Google/Firebase auth provider as the master identity
        // This method signals the UI that it needs to perform a Google Sign-In to authorize this identifier
        viewModelScope.launch {
            val mappedEmail = com.example.data.FirestoreService.getEmailByUsername(username)
            if (mappedEmail != null) {
                // If we have a mapping, we prompt for Google login
                // This is a signal for the UI to call loginWithGoogle
                onComplete()
            } else {
                // Default to creating a new link via Google
                onComplete()
            }
        }
    }

    private fun loginUser(userId: Int, username: String, token: String? = null) {
        sessionManager.loginUser(userId, username, token)
        _knownUserIds.value = sessionManager.getKnownUserIds()
    }

    fun login(emailOrUsername: String, password: String = "secret_password_123", isLoginMode: Boolean = true, onComplete: () -> Unit, onError: (String) -> Unit = {}) {
        val apiKey = com.example.BuildConfig.FIREBASE_API_KEY
        if (apiKey.isBlank() || apiKey == "YOUR_FIREBASE_API_KEY") {
            viewModelScope.launch {
                try {
                    val user = repository.getOrCreateUser(emailOrUsername.substringBefore("@"))
                    loginUser(user.id, user.username)
                    _currentUser.value = user
                    observeUser(user.id)
                    
                    // Start lastSeen update loop
                    viewModelScope.launch {
                        while (true) {
                            repository.updateLastSeen(user.id)
                            kotlinx.coroutines.delay(60_000)
                        }
                    }

                    onComplete()
                    syncPosts()
                } catch (e: Exception) {
                    onError(e.localizedMessage ?: "Login failed")
                }
            }
            return
        }

        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        
        viewModelScope.launch {
            val usernameFromInput = if (emailOrUsername.contains("@")) emailOrUsername.substringBefore("@") else emailOrUsername
            val isCustomZoozDomain = emailOrUsername.endsWith("@zooz.secure", ignoreCase = true) || 
                                     emailOrUsername.endsWith("@zooz.official", ignoreCase = true)
            
            // Identity Resolution
            val mappedEmail = com.example.data.FirestoreService.getEmailByUsername(usernameFromInput)
            val mappedAlias = com.example.data.FirestoreService.getAliasByEmail(emailOrUsername)
            
            val rawEmail = when {
                mappedEmail != null -> mappedEmail
                emailOrUsername.contains("@") -> emailOrUsername
                else -> "$emailOrUsername@zooz.secure"
            }
            
            val finalUsernameForSuccess = when {
                mappedEmail != null -> usernameFromInput
                mappedAlias != null -> mappedAlias
                else -> usernameFromInput
            }
            
            // Translate secure domain emails to Google/Gmail domain for Firebase Auth calls
            val treatedEmail = getFirebaseEmail(rawEmail)

            if (isLoginMode) {
                auth.signInWithEmailAndPassword(treatedEmail, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = task.result?.user
                            val isVerified = firebaseUser?.isEmailVerified == true
                            val isLegacyUser = (firebaseUser?.metadata?.creationTimestamp ?: 0) < System.currentTimeMillis()
                            
                            if (isVerified || isLegacyUser) {
                                handleFirebaseLoginSuccess(firebaseUser, finalUsernameForSuccess, onComplete, onError)
                            } else {
                                onError("Email not verified. Please check your inbox.")
                            }
                        } else {
                            onError(task.exception?.localizedMessage ?: "Sign In failed")
                        }
                    }
            } else {
                auth.createUserWithEmailAndPassword(treatedEmail, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = task.result?.user
                            // Save mapping if it doesn't already exist
                            viewModelScope.launch {
                                com.example.data.FirestoreService.saveUserEmailMapping(finalUsernameForSuccess, rawEmail)
                            }
                            
                            firebaseUser?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                                if (verifyTask.isSuccessful) {
                                    onError("Account created! Please verify your email before logging in.")
                                } else {
                                    onError("Registration successful, but failed to send verification email: ${verifyTask.exception?.message}")
                                }
                            } ?: onError("Account created, but verification failed.")
                        } else {
                            val msg = task.exception?.localizedMessage ?: "Sign Up failed"
                            if (msg.contains("email address is already in use", ignoreCase = true)) {
                                onError("This identity is already linked to an account (e.g. Google). Please sign in with Google or use the Forgot Password flow.")
                            } else {
                                onError(msg)
                            }
                        }
                    }
            }
        }
    }

    private fun handleFirebaseLoginSuccess(firebaseUser: com.google.firebase.auth.FirebaseUser?, originalUsername: String, onComplete: () -> Unit, onError: (String) -> Unit, fallbackToken: String? = null) {
        if (firebaseUser == null) {
            onError("User is null")
            return
        }
        val idTokenTask = firebaseUser.getIdToken(true)
        idTokenTask.addOnCompleteListener { tokenTask ->
            if (tokenTask.isSuccessful) {
                val token = tokenTask.result.token
                viewModelScope.launch {
                    try {
                        val email = firebaseUser.email ?: ""
                        // Priority 1: Check if there is a bound alias for this email
                        val mappedAlias = if (email.isNotBlank()) com.example.data.FirestoreService.getAliasByEmail(email) else null
                        
                        // Priority 2: Use originalUsername (which might be a raw alias from the login form)
                        // Priority 3: Fallback to displayName or email prefix
                        val username = when {
                            mappedAlias != null -> mappedAlias
                            !originalUsername.contains("@") && originalUsername.isNotBlank() -> originalUsername
                            originalUsername.endsWith("@zooz.secure") || originalUsername.endsWith("@zooz.official") -> 
                                originalUsername.substringBefore("@")
                            else -> firebaseUser.displayName?.replace(" ", "")?.ifBlank { null } 
                                     ?: email.substringBefore("@").ifBlank { "User_" + firebaseUser.uid.take(5) }
                        }
                        
                        val user = repository.getOrCreateUser(username)
                        val updatedUser = user.copy(
                            fullName = user.fullName.ifBlank { firebaseUser.displayName ?: "" },
                            profilePicUri = user.profilePicUri ?: firebaseUser.photoUrl?.toString()
                        )
                        repository.updateUser(updatedUser)
                        loginUser(updatedUser.id, updatedUser.username, token)
                        _currentUser.value = updatedUser
                        observeUser(updatedUser.id)
                        
                        // Start lastSeen update loop
                        viewModelScope.launch {
                            while (true) {
                                repository.updateLastSeen(updatedUser.id)
                                kotlinx.coroutines.delay(60_000)
                            }
                        }
                        
                        onComplete()
                        syncPosts()
                    } catch (e: Exception) {
                        onError("Firestore Auth Sync Error: ${e.message}")
                    }
                }
            } else {
                onError("Token error: ${tokenTask.exception?.message}")
            }
        }
    }

    fun loginWithGoogle(idToken: String, pendingAlias: String? = null, onComplete: () -> Unit, onError: (String) -> Unit) {
        val apiKey = com.example.BuildConfig.FIREBASE_API_KEY
        if (apiKey.isBlank() || apiKey == "YOUR_FIREBASE_API_KEY") {
            viewModelScope.launch {
                val user = repository.getOrCreateUser(pendingAlias ?: ("GoogleUser_" + idToken.take(8)))
                loginUser(user.id, user.username)
                _currentUser.value = user
                observeUser(user.id)
                onComplete()
                syncPosts()
            }
            return
        }
        
        try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        val emailRoot = user?.email?.substringBefore("@") ?: "GoogleUser"
                        val finalUsername = pendingAlias ?: emailRoot
                        handleFirebaseLoginSuccess(user, finalUsername, onComplete, onError, fallbackToken = idToken)
                    } else {
                        onError(task.exception?.message ?: "Google Login failed")
                    }
                }
        } catch (e: Exception) {
            viewModelScope.launch {
                val user = repository.getOrCreateUser(pendingAlias ?: ("GoogleUser_" + idToken.take(8)))
                loginUser(user.id, user.username)
                _currentUser.value = user
                observeUser(user.id)
                onComplete()
                syncPosts()
            }
        }
    }
    
    fun syncPosts() {
        viewModelScope.launch {
            try {
                repository.syncAllFromFirestore()
            } catch (e: Exception) {
                // Ignore network errors on sync
            }
        }
    }

    fun deleteAccount(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val currentUserId = currentUser.value?.id ?: return onError("User not logged in.")
        viewModelScope.launch {
            try {
                // Delete user's data from DB
                repository.deleteUserAndData(currentUserId)
                
                // Clear state
                logout()
                onSuccess()
            } catch (e: Exception) {
                onError("Failed to delete account: ${e.localizedMessage}")
            }
        }
    }

    fun logout() {
        sessionManager.logout()
        userCollectJob?.cancel()
        _currentUser.value = null
    }

    fun updateUserProfile(
        username: String,
        fullName: String,
        bio: String,
        profilePicUri: String?,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val user = _currentUser.value ?: return
        val trimmedUsername = username.trim()
        if (trimmedUsername.isBlank()) {
            onError("Username cannot be empty")
            return
        }

        val usernameChanged = !trimmedUsername.equals(user.username, ignoreCase = true)

        viewModelScope.launch {
            var updatedLastChanged = user.usernameLastChangedAt
            if (usernameChanged) {
                // Ensure 7 days limit is respected
                val sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000L
                val timePassed = System.currentTimeMillis() - user.usernameLastChangedAt
                if (user.usernameLastChangedAt > 0L && timePassed < sevenDaysInMillis) {
                    val daysRemaining = Math.ceil((sevenDaysInMillis - timePassed).toDouble() / (1000 * 60 * 60 * 24)).toInt()
                    onError("You can only change your username once every 7 days. Please wait $daysRemaining days.")
                    return@launch
                }

                // Ensure username uniqueness in local DB
                val isTaken = repository.getUserByUsername(trimmedUsername)
                if (isTaken != null && isTaken.id != user.id) {
                    onError("Username is already taken!")
                    return@launch
                }

                updatedLastChanged = System.currentTimeMillis()
            }

            val updatedUser = user.copy(
                username = trimmedUsername,
                fullName = fullName,
                bio = bio,
                profilePicUri = profilePicUri,
                usernameLastChangedAt = updatedLastChanged
            )

            try {
                repository.updateUser(updatedUser)
                _currentUser.value = updatedUser
                loginUser(updatedUser.id, updatedUser.username)
                onSuccess()
            } catch (e: Exception) {
                onError("Database error: ${e.message}")
            }
        }
    }
    
    fun getUserById(userId: Int): StateFlow<User?> {
        return repository.getUserByIdFlow(userId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }
    
    fun getFollowersList(userId: Int): StateFlow<List<User>> {
        return repository.getFollowers(userId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun getFollowingList(userId: Int): StateFlow<List<User>> {
        return repository.getFollowing(userId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun getDrafts(): StateFlow<List<Post>> {
        val user = _currentUser.value ?: return MutableStateFlow(emptyList())
        return repository.getDrafts(user.id)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun getLikedPosts(userId: Int? = null): StateFlow<List<Post>> {
        val targetId = userId ?: _currentUser.value?.id ?: return MutableStateFlow(emptyList())
        return repository.getLikedPosts(targetId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun getBookmarkedPosts(): StateFlow<List<Post>> {
        val user = _currentUser.value ?: return MutableStateFlow(emptyList())
        return repository.getBookmarkedPosts(user.id)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun isBookmarked(postId: Int): StateFlow<Boolean> {
        val user = _currentUser.value ?: return MutableStateFlow(false)
        return repository.isBookmarked(user.id, postId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )
    }

    fun toggleBookmark(postId: Int, currentBookmarked: Boolean) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.toggleBookmark(user.id, postId, currentBookmarked)
        }
    }

    fun getLikeCount(postId: Int): StateFlow<Int> {
        return repository.getLikeCount(postId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )
    }

    fun isLiked(postId: Int): StateFlow<Boolean> {
        val user = _currentUser.value ?: return MutableStateFlow(false)
        return repository.isLiked(user.id, postId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )
    }

    fun toggleLike(postId: Int, currentLiked: Boolean) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.toggleLike(user.id, postId, currentLiked)
            
            if (!currentLiked) {
                // Find post detail to show title
                val post = repository.allPosts.first().find { it.id == postId }
                val postTitle = post?.title ?: "Post"
                val authorName = post?.authorName ?: "Creator"
                addNotification(
                    title = "You Liked a Post ❤️",
                    text = "You liked $authorName's post: \"$postTitle\"",
                    type = "my_like"
                )
            }
        }
    }

    private val followerCountFlows = java.util.concurrent.ConcurrentHashMap<Int, StateFlow<Int>>()

    fun getFollowerCount(userId: Int): StateFlow<Int> {
        return followerCountFlows.getOrPut(userId) {
            repository.getFollowerCount(userId)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = 0
                )
        }
    }

    fun getFollowingCount(userId: Int): StateFlow<Int> {
        return repository.getFollowingCount(userId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )
    }

    fun isFollowing(followingId: Int): StateFlow<Boolean> {
        val user = _currentUser.value ?: return MutableStateFlow(false)
        if (user.id == followingId) return MutableStateFlow(false)
        return repository.isFollowing(user.id, followingId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )
    }

    fun toggleFollow(followingId: Int, currentFollowing: Boolean) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.toggleFollow(user.id, followingId, currentFollowing)
            
            if (!currentFollowing) {
                val followingUser = repository.getUserByIdFlow(followingId).first()
                val targetName = followingUser?.fullName?.ifBlank { followingUser?.username } ?: "Creator"
                addNotification(
                    title = "Started Following 👤",
                    text = "You started following $targetName (@${followingUser?.username})",
                    type = "my_follow"
                )
            }
        }
    }

    fun createPost(title: String, content: String, categories: String, tags: String, mediaUri: String?, isDraft: Boolean, commentsDisabled: Boolean = false, spotifyTrackId: String? = null, spotifyTrackName: String? = null, spotifyTrackArtist: String? = null, spotifyTrackImageUrl: String? = null, spotifyTrackPreviewUrl: String? = null, onComplete: () -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.createPost(user.id, user.username, title, content, categories, tags, mediaUri, isDraft, commentsDisabled, spotifyTrackId, spotifyTrackName, spotifyTrackArtist, spotifyTrackImageUrl, spotifyTrackPreviewUrl)
            
            if (!isDraft) {
                addNotification(
                    title = "Published New Post \uD83D\uDCDD",
                    text = "You published: \"$title\"",
                    type = "my_publishing"
                )
            }
            onComplete()
        }
    }

    fun getPostById(postId: Int): StateFlow<Post?> {
        return repository.getPostById(postId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }

    fun getCommentsForPost(postId: Int): StateFlow<List<Comment>> {
        return repository.getCommentsForPost(postId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun addComment(postId: Int, content: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.createComment(postId, user.id, user.username, content)
            
            // Add to My Activities
            val post = repository.allPosts.first().find { it.id == postId }
            val postTitle = post?.title ?: "Post"
            val authorName = post?.authorName ?: "Creator"
            addNotification(
                title = "You Commented 💬",
                text = "You commented on $authorName's post: \"$content\"",
                type = "my_comment"
            )
        }
    }

    fun deletePost(postId: Int, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val post = repository.allPosts.first().find { it.id == postId }
            repository.deletePost(postId)
            if (post != null) {
                addNotification(
                    title = "Deleted Post 🗑️",
                    text = "You deleted your post: \"${post.title}\"",
                    type = "my_publishing"
                )
            }
            onComplete()
        }
    }

    fun updatePost(post: Post, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updatePost(post)
            addNotification(
                title = "Updated Post 📝",
                text = "You edited your post: \"${post.title}\"",
                type = "my_publishing"
            )
            onComplete()
        }
    }

    fun toggleCommentsDisabled(postId: Int, disabled: Boolean) {
        viewModelScope.launch {
            val post = repository.allPosts.first().find { it.id == postId }
            if (post != null) {
                val updatedPost = post.copy(commentsDisabled = disabled)
                repository.updatePost(updatedPost)
                addNotification(
                    title = if (disabled) "Comments Disabled 🔕" else "Comments Enabled 🔔",
                    text = "You ${if (disabled) "closed" else "opened"} the comment section for: \"${post.title}\"",
                    type = "my_publishing"
                )
            }
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            repository.deleteComment(commentId)
            addNotification(
                title = "Comment Deleted 🗑️",
                text = "A comment was successfully deleted.",
                type = "my_publishing"
            )
        }
    }

    fun updateComment(comment: Comment) {
        viewModelScope.launch {
            repository.updateComment(comment)
            addNotification(
                title = "Comment Updated 💬",
                text = "A comment was edited successfully.",
                type = "my_comment"
            )
        }
    }

    fun toggleCommentPin(comment: Comment, pinned: Boolean) {
        viewModelScope.launch {
            val updatedComment = comment.copy(isPinned = pinned)
            repository.updateComment(updatedComment)
            addNotification(
                title = if (pinned) "Comment Pinned 📌" else "Comment Unpinned 📍",
                text = "You ${if (pinned) "pinned" else "unpinned"} a comment on your post.",
                type = "my_publishing"
            )
        }
    }

    val allUsers: StateFlow<List<User>> = repository.getAllUsersFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val knownUsers: StateFlow<List<User>> = combine(allUsers, knownUserIds) { users, knownIds ->
        users.filter { it.id.toString() in knownIds }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allChatMessages: StateFlow<List<com.example.data.ChatMessage>> = repository.getAllMessagesFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val typingUsers: StateFlow<Map<Int, Boolean>> = repository.typingUsers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    fun getMessagesBetween(otherUserId: Int): StateFlow<List<com.example.data.ChatMessage>> {
        val currentUserId = currentUser.value?.id ?: 0
        return repository.getMessagesBetweenUsers(currentUserId, otherUserId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun sendChatMessage(receiverId: Int, text: String, type: String = "text", mediaUri: String? = null, replyToId: Int? = null, spotifyTrackId: String? = null, spotifyTrackName: String? = null, spotifyTrackArtist: String? = null, spotifyTrackImageUrl: String? = null, spotifyTrackPreviewUrl: String? = null) {
        val senderId = currentUser.value?.id ?: return
        viewModelScope.launch {
            repository.sendChatMessage(senderId, receiverId, text, type, mediaUri, replyToId, spotifyTrackId, spotifyTrackName, spotifyTrackArtist, spotifyTrackImageUrl, spotifyTrackPreviewUrl)
        }
    }

    fun markMessagesAsRead(senderId: Int) {
        val receiverId = currentUser.value?.id ?: return
        viewModelScope.launch {
            repository.markMessagesAsRead(senderId, receiverId)
        }
    }

    fun sendChatMediaMessage(context: Context, receiverId: Int, uri: Uri, type: String, replyToId: Int? = null) {
        val senderId = currentUser.value?.id ?: return
        viewModelScope.launch {
            val uploadedUrl = com.example.data.R2Uploader.uploadFile(context, uri)
            if (uploadedUrl != null) {
                repository.sendChatMessage(senderId, receiverId, "", type, uploadedUrl, replyToId)
            } else {
                // Fallback to local URI if upload fails (though R2Uploader returns null on failure)
                // For a real production app, we might want to handle this better (retry or show error)
            }
        }
    }

    fun setTypingStatus(receiverId: Int, isTyping: Boolean) {
        val senderId = currentUser.value?.id ?: return
        viewModelScope.launch {
            repository.setTypingStatus(senderId, receiverId, isTyping)
        }
    }

    fun deleteChatMessage(messageId: Int) {
        viewModelScope.launch {
            repository.deleteChatMessage(messageId)
        }
    }

    fun updateChatMessage(messageId: Int, newText: String) {
        viewModelScope.launch {
            val message = allChatMessages.value.find { it.id == messageId } ?: return@launch
            repository.updateChatMessage(message.copy(text = newText, isEdited = true))
        }
    }

    fun reportChatMessage(messageId: Int) {
        viewModelScope.launch {
            repository.reportChatMessage(messageId)
        }
    }

    fun getServerUrl(): String = sessionManager.getServerUrl()
    fun setServerUrl(url: String) {
        sessionManager.setServerUrl(url)
    }

    fun getAgoraAppId(): String? = sessionManager.getAgoraAppId()
    fun setAgoraAppId(appId: String?) {
        sessionManager.setAgoraAppId(appId)
    }

    fun boostPost(postId: Int, fee: Double) {
        viewModelScope.launch {
            repository.boostPost(postId, fee)
        }
    }

    fun recordPostClick(postId: Int) {
        viewModelScope.launch {
            repository.incrementClicks(postId)
        }
    }

    fun recordPostImpression(postId: Int) {
        viewModelScope.launch {
            repository.incrementImpressions(postId)
        }
    }

    suspend fun getCurrentlyBoundAlias(email: String): String? {
        return com.example.data.FirestoreService.getAliasByEmail(email)
    }

    fun bindCustomZoozAlias(alias: String, onComplete: () -> Unit, onError: (String) -> Unit) {
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        if (firebaseUser == null || firebaseUser.email.isNullOrBlank()) {
            onError("You must be signed in with a Google account to bind a Zooz alias")
            return
        }
        val targetAlias = alias.trim().lowercase()
        if (targetAlias.isBlank()) {
            onError("Alias username cannot be empty")
            return
        }
        if (targetAlias.contains("@")) {
            onError("Please enter raw username without the domain suffix (e.g. alice)")
            return
        }
        
        viewModelScope.launch {
            try {
                // Check if this username prefix is already map-bound in Firestore
                val existingMapping = com.example.data.FirestoreService.getEmailByUsername(targetAlias)
                if (existingMapping != null) {
                    if (existingMapping.equals(firebaseUser.email, ignoreCase = true)) {
                        onComplete() // Already bound to this exact Google account!
                        return@launch
                    } else {
                        onError("This alias '$targetAlias' is already bound to another Google secure account")
                        return@launch
                    }
                }
                
                // Save the binding mapping: targetAlias -> Google email in Firestore
                com.example.data.FirestoreService.saveUserEmailMapping(targetAlias, firebaseUser.email!!)
                onComplete()
            } catch (e: Exception) {
                onError("Failed to bind alias: ${e.message}")
            }
        }
    }

    fun enableCredentialsLogin(password: String, onComplete: () -> Unit, onError: (String) -> Unit) {
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user == null || user.email == null) {
            onError("You must be logged in with Google first")
            return
        }
        
        val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(user.email!!, password)
        user.linkWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete()
                } else {
                    onError(task.exception?.localizedMessage ?: "Failed to enable credentials login. Account might already have a password set.")
                }
            }
    }

    class Factory(
        private val repository: BlogRepository,
        private val sessionManager: SessionManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BlogViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BlogViewModel(repository, sessionManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class AlertNotification(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val text: String,
    val type: String, // "direct_message", "growth_boost", "promotion_gate", "system"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val associatedUserId: Int? = null
)
