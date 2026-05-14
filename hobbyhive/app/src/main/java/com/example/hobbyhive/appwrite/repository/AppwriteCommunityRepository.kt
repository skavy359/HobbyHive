package com.example.hobbyhive.appwrite.repository

import com.example.hobbyhive.appwrite.AppwriteClient
import com.example.hobbyhive.appwrite.AppwriteConfig
import com.example.hobbyhive.appwrite.model.AppwriteForumPost
import com.example.hobbyhive.appwrite.model.toRoomPost
import com.example.hobbyhive.data.CommunityDao
import com.example.hobbyhive.data.CommunityRepository
import com.example.hobbyhive.model.ForumComment
import com.example.hobbyhive.model.ForumPost
import com.example.hobbyhive.model.HobbyGroup
import io.appwrite.ID
import io.appwrite.Query
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.Document
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

// ═══════════════════════════════════════════════════════════════════════════
// AppwriteCommunityRepository.kt
//
// Manages forum posts, comments, and hobby groups using Appwrite Database.
//
// Community content is always attempted from Appwrite (network-first):
//   • Posts and comments are social — freshness matters more than offline speed
//   • Room is used as cache when Appwrite is unreachable
//
// Realtime subscriptions for posts are handled separately in
// AppwriteRealtimeRepository.
// ═══════════════════════════════════════════════════════════════════════════

class AppwriteCommunityRepository(
    private val communityDao: CommunityDao,
    private val userPreferencesRepository: com.example.hobbyhive.data.UserPreferencesRepository
) : CommunityRepository(communityDao) {

    private val databases get() = AppwriteClient.databases

    private suspend fun getAppwriteUserId(): String = userPreferencesRepository.appwriteUserId.first() ?: ""

    // ─── Forum Posts — Read ──────────────────────────────────────────────

    /** Observe posts from local Room cache (updated by realtime + sync). */
    override fun getAllPosts(): Flow<List<ForumPost>> = communityDao.getAllPosts()

    override fun getPostById(postId: Long): Flow<ForumPost?> = communityDao.getPostById(postId)

    /** Fetch latest posts from Appwrite and refresh the Room cache. */
    suspend fun fetchAndSyncPosts(): Result<Unit> {
        return try {
            val response = databases.listDocuments(
                databaseId   = AppwriteConfig.DATABASE_ID,
                collectionId = AppwriteConfig.COLLECTION_FORUM_POSTS,
                queries      = listOf(Query.orderDesc("\$createdAt"), Query.limit(50))
            )
            response.documents.forEach { doc ->
                communityDao.insertPost(AppwriteForumPost.fromDocument(doc).toRoomPost())
            }
            Result.success(Unit)
        } catch (e: AppwriteException) {
            Result.failure(Exception(e.message ?: "Post sync failed"))
        }
    }

    // ─── Forum Posts — Write ─────────────────────────────────────────────

    override suspend fun insertPost(post: ForumPost) {
        // Find or provide author name
        createPost(
            userId = getAppwriteUserId(),
            authorName = post.authorName,
            title = post.title,
            content = post.content,
            category = post.category
        )
    }

    /**
     * Create a new forum post on Appwrite, then cache it in Room.
     *
     * @param userId     The Appwrite user ID of the author
     * @param authorName Display name shown on the post
     */
    suspend fun createPost(
        userId: String,
        authorName: String,
        title: String,
        content: String,
        category: String
    ): Result<ForumPost> {
        return try {
            val now = System.currentTimeMillis()
            val data = mapOf(
                "userId"       to userId,
                "authorName"   to authorName,
                "title"        to title,
                "content"      to content,
                "category"     to category,
                "upvotes"      to 0,
                "repliesCount" to 0,
                "createdAt"    to now
            )
            val doc = databases.createDocument(
                databaseId   = AppwriteConfig.DATABASE_ID,
                collectionId = AppwriteConfig.COLLECTION_FORUM_POSTS,
                documentId   = ID.unique(),
                data         = data
            )
            val post = AppwriteForumPost.fromDocument(doc).toRoomPost()
            communityDao.insertPost(post)
            Result.success(post)
        } catch (e: AppwriteException) {
            // Fallback: save locally so the user sees their post immediately
            val post = ForumPost(
                title = title, content = content, authorName = authorName,
                category = category, createdAt = System.currentTimeMillis()
            )
            communityDao.insertPost(post)
            Result.success(post)
        }
    }

    /** Upvote a post — increments counter on Appwrite and Room. */
    override suspend fun upvotePost(post: ForumPost) {
        upvotePost(post.appwriteId ?: "", post)
    }

    suspend fun upvotePost(documentId: String, post: ForumPost): Result<Unit> {
        return try {
            if (documentId.isNotEmpty()) {
                databases.updateDocument(
                    databaseId   = AppwriteConfig.DATABASE_ID,
                    collectionId = AppwriteConfig.COLLECTION_FORUM_POSTS,
                    documentId   = documentId,
                    data         = mapOf("upvotes" to (post.upvotes + 1))
                )
            }
            communityDao.updatePost(post.copy(upvotes = post.upvotes + 1))
            Result.success(Unit)
        } catch (e: AppwriteException) {
            communityDao.updatePost(post.copy(upvotes = post.upvotes + 1))
            Result.success(Unit)
        }
    }

    // ─── Comments ────────────────────────────────────────────────────────

    override fun getCommentsForPost(postId: Long): Flow<List<ForumComment>> =
        communityDao.getCommentsForPost(postId)

    override suspend fun addComment(comment: ForumComment) {
        addComment(
            userId = getAppwriteUserId(),
            authorName = comment.authorName,
            postDocumentId = "", // Ideally we should have the parent post's appwriteId
            postRoomId = comment.postId,
            content = comment.content
        )
    }

    /** Add a comment on Appwrite + Room. */
    suspend fun addComment(
        userId: String,
        authorName: String,
        postDocumentId: String,
        postRoomId: Long,
        content: String
    ): Result<ForumComment> {
        return try {
            val now = System.currentTimeMillis()
            val data = mapOf(
                "postId"     to postDocumentId,
                "userId"     to userId,
                "authorName" to authorName,
                "content"    to content,
                "createdAt"  to now
            )
            databases.createDocument(
                databaseId   = AppwriteConfig.DATABASE_ID,
                collectionId = AppwriteConfig.COLLECTION_FORUM_COMMENTS,
                documentId   = ID.unique(),
                data         = data
            )
            val comment = ForumComment(
                postId = postRoomId, authorName = authorName,
                content = content, createdAt = now
            )
            communityDao.insertComment(comment)
            Result.success(comment)
        } catch (e: AppwriteException) {
            val comment = ForumComment(
                postId = postRoomId, authorName = authorName,
                content = content, createdAt = System.currentTimeMillis()
            )
            communityDao.insertComment(comment)
            Result.success(comment)
        }
    }

    // ─── Hobby Groups ────────────────────────────────────────────────────

    override fun getAllGroups(): Flow<List<HobbyGroup>> = communityDao.getAllGroups()

    override suspend fun toggleGroupJoinStatus(group: HobbyGroup) {
        communityDao.updateGroup(group.copy(isJoined = !group.isJoined))
    }

    /** Fetch groups from Appwrite and populate Room. */
    suspend fun fetchAndSyncGroups(): Result<Unit> {
        return try {
            val response = databases.listDocuments(
                databaseId   = AppwriteConfig.DATABASE_ID,
                collectionId = AppwriteConfig.COLLECTION_HOBBY_GROUPS
            )
            response.documents.forEach { doc: Document<Map<String, Any>> ->
                val d = doc.data
                communityDao.insertGroup(
                    HobbyGroup(
                        name         = d["name"] as? String ?: "",
                        description  = d["description"] as? String ?: "",
                        category     = d["category"] as? String ?: "",
                        membersCount = (d["membersCount"] as? Number)?.toInt() ?: 0
                    )
                )
            }
            Result.success(Unit)
        } catch (e: AppwriteException) {
            Result.failure(Exception(e.message ?: "Group sync failed"))
        }
    }
}
