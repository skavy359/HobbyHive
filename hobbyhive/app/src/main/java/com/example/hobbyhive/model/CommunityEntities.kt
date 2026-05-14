package com.example.hobbyhive.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forum_posts")
data class ForumPost(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appwriteId: String? = null,
    val title: String,
    val content: String,
    val authorName: String,
    val createdAt: Long = System.currentTimeMillis(),
    val category: String,
    val upvotes: Int = 0,
    val repliesCount: Int = 0
)

@Entity(tableName = "forum_comments")
data class ForumComment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appwriteId: String? = null,
    val postId: Long,
    val authorName: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "hobby_groups")
data class HobbyGroup(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val category: String,
    val membersCount: Int = 0,
    val isJoined: Boolean = false
)
