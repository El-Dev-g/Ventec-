package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "users", indices = [Index(value = ["username"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val fullName: String = "",
    val bio: String = "",
    val profilePicUri: String? = null,
    val usernameLastChangedAt: Long = 0L,
    val lastSeen: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "posts",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("authorId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["authorId"])]
)
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorId: Int,
    val authorName: String,
    val title: String,
    val content: String,
    val categories: String = "",
    val tags: String = "",
    val mediaUri: String? = null,
    val spotifyTrackId: String? = null,
    val spotifyTrackName: String? = null,
    val spotifyTrackArtist: String? = null,
    val spotifyTrackImageUrl: String? = null,
    val spotifyTrackPreviewUrl: String? = null,
    val isDraft: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val commentsDisabled: Boolean = false,
    val isPromoted: Boolean = false,
    val promoteFee: Double = 0.0,
    val clicks: Int = 0,
    val impressions: Int = 0,
    val isReported: Boolean = false
)

@Entity(
    tableName = "bookmarks",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("userId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Post::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("postId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId", "postId"], unique = true), Index(value = ["postId"])]
)
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val postId: Int
)

@Entity(
    tableName = "likes",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("userId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Post::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("postId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId", "postId"], unique = true), Index(value = ["postId"])]
)
data class Like(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val postId: Int
)

@Entity(
    tableName = "follows",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("followerId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("followingId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["followerId", "followingId"], unique = true), Index(value = ["followingId"])]
)
data class Follow(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val followerId: Int,
    val followingId: Int
)

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = Post::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("postId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("authorId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["postId"]), Index(value = ["authorId"])]
)
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val postId: Int,
    val authorId: Int,
    val authorName: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("senderId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("receiverId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["senderId"]), Index(value = ["receiverId"])]
)
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderId: Int,
    val receiverId: Int,
    val text: String,
    val type: String = "text", // "text", "voice", "image", "video", "file", "music"
    val mediaUri: String? = null,
    val spotifyTrackId: String? = null,
    val spotifyTrackName: String? = null,
    val spotifyTrackArtist: String? = null,
    val spotifyTrackImageUrl: String? = null,
    val spotifyTrackPreviewUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isEdited: Boolean = false,
    val isReported: Boolean = false,
    val replyToId: Int? = null,
    val isRead: Boolean = false
)

@Entity(
    tableName = "stories",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("authorId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["authorId"])]
)
data class Story(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorId: Int,
    val authorName: String,
    val mediaUri: String? = null,
    val text: String = "",
    val spotifyTrackId: String? = null,
    val spotifyTrackName: String? = null,
    val spotifyTrackArtist: String? = null,
    val spotifyTrackImageUrl: String? = null,
    val spotifyTrackPreviewUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 86400000L // 24 hours (86,400,000 ms)
)
