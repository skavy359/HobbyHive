package com.example.hobbyhive.appwrite.model

import com.example.hobbyhive.model.ForumComment
import com.example.hobbyhive.model.ForumPost
import com.example.hobbyhive.model.Goal
import com.example.hobbyhive.model.GoalStatus
import com.example.hobbyhive.model.Hobby
import com.example.hobbyhive.model.HobbyCategory
import com.example.hobbyhive.model.HobbyStatus
import com.example.hobbyhive.model.Session
import io.appwrite.models.Document

// ═══════════════════════════════════════════════════════════════════════════
// AppwriteModels.kt — Data Transfer Objects for each Appwrite collection
//
// Each DTO has:
//   • fromDocument() — build from raw Appwrite Document
//   • toMap()        — serialize to Map for createDocument / updateDocument
//
// Mapper extension functions at the bottom convert DTOs ↔ Room entities.
// ═══════════════════════════════════════════════════════════════════════════

data class AppwriteHobby(
    val documentId: String,
    val userId: String,
    val name: String,
    val description: String,
    val category: String,
    val rating: Double,
    val progress: Int,
    val status: String,
    val notes: String,
    val imageFileId: String?,
    val reminderEnabled: Boolean,
    val reminderTime: Long?,
    val targetDate: Long?,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        fun fromDocument(doc: Document<Map<String, Any>>): AppwriteHobby {
            val d = doc.data
            return AppwriteHobby(
                documentId      = doc.id,
                userId          = d["userId"] as? String ?: "",
                name            = d["name"] as? String ?: "",
                description     = d["description"] as? String ?: "",
                category        = d["category"] as? String ?: "OTHER",
                rating          = (d["rating"] as? Number)?.toDouble() ?: 0.0,
                progress        = (d["progress"] as? Number)?.toInt() ?: 0,
                status          = d["status"] as? String ?: "ACTIVE",
                notes           = d["notes"] as? String ?: "",
                imageFileId     = d["imageFileId"] as? String,
                reminderEnabled = d["reminderEnabled"] as? Boolean ?: false,
                reminderTime    = (d["reminderTime"] as? Number)?.toLong(),
                targetDate      = (d["targetDate"] as? Number)?.toLong(),
                createdAt       = (d["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                updatedAt       = (d["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId, "name" to name, "description" to description,
        "category" to category, "rating" to rating, "progress" to progress,
        "status" to status, "notes" to notes, "imageFileId" to imageFileId,
        "reminderEnabled" to reminderEnabled, "reminderTime" to reminderTime,
        "targetDate" to targetDate, "createdAt" to createdAt, "updatedAt" to updatedAt
    )
}

data class AppwriteSession(
    val documentId: String,
    val userId: String,
    val hobbyDocumentId: String,
    val durationMinutes: Int,
    val sessionDate: Long,
    val notes: String,
    val createdAt: Long
) {
    companion object {
        fun fromDocument(doc: Document<Map<String, Any>>): AppwriteSession {
            val d = doc.data
            return AppwriteSession(
                documentId      = doc.id,
                userId          = d["userId"] as? String ?: "",
                hobbyDocumentId = d["hobbyId"] as? String ?: "",
                durationMinutes = (d["durationMinutes"] as? Number)?.toInt() ?: 0,
                sessionDate     = (d["sessionDate"] as? Number)?.toLong() ?: 0L,
                notes           = d["notes"] as? String ?: "",
                createdAt       = (d["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId, "hobbyId" to hobbyDocumentId,
        "durationMinutes" to durationMinutes, "sessionDate" to sessionDate,
        "notes" to notes, "createdAt" to createdAt
    )
}

data class AppwriteGoal(
    val documentId: String,
    val userId: String,
    val hobbyDocumentId: String,
    val title: String,
    val description: String,
    val targetValue: Int,
    val currentValue: Int,
    val unit: String,
    val deadline: Long?,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        fun fromDocument(doc: Document<Map<String, Any>>): AppwriteGoal {
            val d = doc.data
            return AppwriteGoal(
                documentId      = doc.id,
                userId          = d["userId"] as? String ?: "",
                hobbyDocumentId = d["hobbyId"] as? String ?: "",
                title           = d["title"] as? String ?: "",
                description     = d["description"] as? String ?: "",
                targetValue     = (d["targetValue"] as? Number)?.toInt() ?: 100,
                currentValue    = (d["currentValue"] as? Number)?.toInt() ?: 0,
                unit            = d["unit"] as? String ?: "hours",
                deadline        = (d["deadline"] as? Number)?.toLong(),
                status          = d["status"] as? String ?: "IN_PROGRESS",
                createdAt       = (d["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                updatedAt       = (d["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId, "hobbyId" to hobbyDocumentId, "title" to title,
        "description" to description, "targetValue" to targetValue,
        "currentValue" to currentValue, "unit" to unit, "deadline" to deadline,
        "status" to status, "createdAt" to createdAt, "updatedAt" to updatedAt
    )
}

data class AppwriteForumPost(
    val documentId: String,
    val userId: String,
    val authorName: String,
    val title: String,
    val content: String,
    val category: String,
    val upvotes: Int,
    val repliesCount: Int,
    val createdAt: Long
) {
    companion object {
        fun fromDocument(doc: Document<Map<String, Any>>): AppwriteForumPost {
            val d = doc.data
            return AppwriteForumPost(
                documentId   = doc.id,
                userId       = d["userId"] as? String ?: "",
                authorName   = d["authorName"] as? String ?: "Anonymous",
                title        = d["title"] as? String ?: "",
                content      = d["content"] as? String ?: "",
                category     = d["category"] as? String ?: "General",
                upvotes      = (d["upvotes"] as? Number)?.toInt() ?: 0,
                repliesCount = (d["repliesCount"] as? Number)?.toInt() ?: 0,
                createdAt    = (d["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId, "authorName" to authorName, "title" to title,
        "content" to content, "category" to category, "upvotes" to upvotes,
        "repliesCount" to repliesCount, "createdAt" to createdAt
    )
}

data class AppwriteForumComment(
    val documentId: String,
    val postId: String,
    val userId: String,
    val authorName: String,
    val content: String,
    val createdAt: Long
) {
    companion object {
        fun fromDocument(doc: Document<Map<String, Any>>): AppwriteForumComment {
            val d = doc.data
            return AppwriteForumComment(
                documentId = doc.id,
                postId     = d["postId"] as? String ?: "",
                userId     = d["userId"] as? String ?: "",
                authorName = d["authorName"] as? String ?: "Anonymous",
                content    = d["content"] as? String ?: "",
                createdAt  = (d["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "postId" to postId, "userId" to userId, "authorName" to authorName,
        "content" to content, "createdAt" to createdAt
    )
}

data class AppwriteProfile(
    val documentId: String,
    val userId: String,
    val fullName: String,
    val about: String,
    val avatarFileId: String?
) {
    companion object {
        fun fromDocument(doc: Document<Map<String, Any>>): AppwriteProfile {
            val d = doc.data
            return AppwriteProfile(
                documentId   = doc.id,
                userId       = d["userId"] as? String ?: "",
                fullName     = d["fullName"] as? String ?: "",
                about        = d["about"] as? String ?: "",
                avatarFileId = d["avatarFileId"] as? String
            )
        }
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId, "fullName" to fullName,
        "about" to about, "avatarFileId" to avatarFileId
    )
}

// ─── Mapper extensions: DTO ↔ Room entity ───────────────────────────────────

fun AppwriteHobby.toRoomHobby(roomId: Long = 0L): Hobby = Hobby(
    id              = roomId,
    appwriteId      = documentId,
    name            = name,
    description     = description,
    category        = runCatching { HobbyCategory.valueOf(category) }.getOrDefault(HobbyCategory.OTHER),
    rating          = rating.toFloat(),
    progress        = progress,
    status          = runCatching { HobbyStatus.valueOf(status) }.getOrDefault(HobbyStatus.ACTIVE),
    notes           = notes,
    imageUri        = imageFileId,
    reminderEnabled = reminderEnabled,
    reminderTime    = reminderTime,
    targetDate      = targetDate,
    createdAt       = createdAt,
    updatedAt       = updatedAt
)

fun Hobby.toAppwriteHobby(userId: String): AppwriteHobby = AppwriteHobby(
    documentId      = appwriteId ?: "",
    userId          = userId,
    name            = name,
    description     = description,
    category        = category.name,
    rating          = rating.toDouble(),
    progress        = progress,
    status          = status.name,
    notes           = notes,
    imageFileId     = imageUri,
    reminderEnabled = reminderEnabled,
    reminderTime    = reminderTime,
    targetDate      = targetDate,
    createdAt       = createdAt,
    updatedAt       = updatedAt
)

fun AppwriteForumPost.toRoomPost(roomId: Long = 0L): ForumPost = ForumPost(
    id = roomId, appwriteId = documentId, title = title, content = content, authorName = authorName,
    createdAt = createdAt, category = category, upvotes = upvotes, repliesCount = repliesCount
)

fun AppwriteForumComment.toRoomComment(roomId: Long = 0L, roomPostId: Long = 0L): ForumComment =
    ForumComment(id = roomId, appwriteId = documentId, postId = roomPostId, authorName = authorName, content = content, createdAt = createdAt)

fun AppwriteGoal.toRoomGoal(roomId: Long = 0L, roomHobbyId: Long = 0L): Goal = Goal(
    id = roomId, appwriteId = documentId, hobbyId = roomHobbyId, title = title, description = description,
    targetValue = targetValue, currentValue = currentValue, unit = unit, deadline = deadline,
    status = runCatching { GoalStatus.valueOf(status) }.getOrDefault(GoalStatus.IN_PROGRESS),
    createdAt = createdAt, updatedAt = updatedAt
)

fun AppwriteSession.toRoomSession(roomId: Long = 0L, roomHobbyId: Long = 0L): Session = Session(
    id = roomId, appwriteId = documentId, hobbyId = roomHobbyId, durationMinutes = durationMinutes,
    sessionDate = sessionDate, notes = notes, createdAt = createdAt
)
