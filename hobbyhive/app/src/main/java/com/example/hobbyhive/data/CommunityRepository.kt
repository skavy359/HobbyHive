package com.example.hobbyhive.data

import com.example.hobbyhive.model.ForumComment
import com.example.hobbyhive.model.ForumPost
import com.example.hobbyhive.model.HobbyGroup
import kotlinx.coroutines.flow.Flow

class CommunityRepository(private val dao: CommunityDao) {

    fun getAllPosts(): Flow<List<ForumPost>> = dao.getAllPosts()

    fun getPostById(postId: Long): Flow<ForumPost?> = dao.getPostById(postId)

    suspend fun insertPost(post: ForumPost) {
        dao.insertPost(post)
    }

    suspend fun upvotePost(post: ForumPost) {
        dao.updatePost(post.copy(upvotes = post.upvotes + 1))
    }

    fun getCommentsForPost(postId: Long): Flow<List<ForumComment>> = dao.getCommentsForPost(postId)

    suspend fun addComment(comment: ForumComment) {
        dao.insertComment(comment)
        dao.incrementRepliesCount(comment.postId)
    }

    fun getAllGroups(): Flow<List<HobbyGroup>> = dao.getAllGroups()

    suspend fun toggleGroupJoinStatus(group: HobbyGroup) {
        val newCount = if (group.isJoined) group.membersCount - 1 else group.membersCount + 1
        dao.updateGroup(group.copy(isJoined = !group.isJoined, membersCount = newCount))
    }
    
    suspend fun seedInitialGroups() {
        // Seed some groups if none exist
        dao.insertGroup(HobbyGroup(name = "Morning Runners", description = "For those who hit the pavement before the sun comes up.", category = "Fitness", membersCount = 142))
        dao.insertGroup(HobbyGroup(name = "Kotlin Enthusiasts", description = "Discussing best practices in Android & Kotlin.", category = "Programming", membersCount = 58))
        dao.insertGroup(HobbyGroup(name = "Digital Art Basics", description = "Sharing tips for Procreate and Photoshop.", category = "Art", membersCount = 310))
        dao.insertGroup(HobbyGroup(name = "Classical Guitar", description = "Sheet music, technique, and jam sessions.", category = "Music", membersCount = 12))
    }
}
