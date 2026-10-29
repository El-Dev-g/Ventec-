package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserByIdFlow(userId: Int): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @androidx.room.Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE id IN (SELECT followerId FROM follows WHERE followingId = :userId)")
    fun getFollowers(userId: Int): Flow<List<User>>
    
    @Query("SELECT * FROM users WHERE id IN (SELECT followingId FROM follows WHERE followerId = :userId)")
    fun getFollowing(userId: Int): Flow<List<User>>

    @Query("SELECT * FROM users ORDER BY username ASC")
    fun getAllUsersFlow(): Flow<List<User>>

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Int)
}

@Dao
interface PostDao {
    @Query("SELECT * FROM posts WHERE isDraft = 0 ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE authorId = :userId AND isDraft = 1 ORDER BY timestamp DESC")
    fun getDrafts(userId: Int): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE id = :postId LIMIT 1")
    fun getPostById(postId: Int): Flow<Post?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post): Long
    
    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: Int)

    @Query("DELETE FROM posts WHERE authorId = :userId")
    suspend fun deletePostsByAuthor(userId: Int)
}

@Dao
interface BookmarkDao {
    @Query("SELECT posts.* FROM posts INNER JOIN bookmarks ON posts.id = bookmarks.postId WHERE bookmarks.userId = :userId ORDER BY posts.timestamp DESC")
    fun getBookmarkedPosts(userId: Int): Flow<List<Post>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookmark(bookmark: Bookmark): Long

    @Query("DELETE FROM bookmarks WHERE userId = :userId AND postId = :postId")
    suspend fun removeBookmark(userId: Int, postId: Int)

    @Query("SELECT COUNT(*) > 0 FROM bookmarks WHERE userId = :userId AND postId = :postId")
    fun isBookmarked(userId: Int, postId: Int): Flow<Boolean>
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Int): Flow<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment): Long

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: Int)
}

@Dao
interface LikeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLike(like: Like): Long

    @Query("DELETE FROM likes WHERE userId = :userId AND postId = :postId")
    suspend fun removeLike(userId: Int, postId: Int)

    @Query("SELECT COUNT(*) FROM likes WHERE postId = :postId")
    fun getLikeCount(postId: Int): Flow<Int>

    @Query("SELECT COUNT(*) > 0 FROM likes WHERE userId = :userId AND postId = :postId")
    fun isLiked(userId: Int, postId: Int): Flow<Boolean>
}

@Dao
interface FollowDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFollow(follow: Follow): Long

    @Query("DELETE FROM follows WHERE followerId = :followerId AND followingId = :followingId")
    suspend fun removeFollow(followerId: Int, followingId: Int)

    @Query("SELECT COUNT(*) FROM follows WHERE followingId = :userId")
    fun getFollowerCount(userId: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM follows WHERE followerId = :userId")
    fun getFollowingCount(userId: Int): Flow<Int>

    @Query("SELECT COUNT(*) > 0 FROM follows WHERE followerId = :followerId AND followingId = :followingId")
    fun isFollowing(followerId: Int, followingId: Int): Flow<Boolean>
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE (senderId = :userId1 AND receiverId = :userId2) OR (senderId = :userId2 AND receiverId = :userId1) ORDER BY timestamp ASC")
    fun getMessagesBetweenUsers(userId1: Int, userId2: Int): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessagesFlow(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @androidx.room.Update
    suspend fun updateMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: Int)

    @Query("UPDATE chat_messages SET isRead = 1 WHERE senderId = :senderId AND receiverId = :receiverId")
    suspend fun markMessagesAsRead(senderId: Int, receiverId: Int)
}

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories WHERE expiresAt > :currentTime ORDER BY timestamp DESC")
    fun getActiveStories(currentTime: Long = System.currentTimeMillis()): Flow<List<Story>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: Story): Long

    @Query("DELETE FROM stories WHERE id = :storyId")
    suspend fun deleteStory(storyId: Int)

    @Query("DELETE FROM stories WHERE expiresAt <= :currentTime")
    suspend fun deleteExpiredStories(currentTime: Long = System.currentTimeMillis())
}
