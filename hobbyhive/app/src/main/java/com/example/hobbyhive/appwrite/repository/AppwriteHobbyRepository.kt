package com.example.hobbyhive.appwrite.repository

import com.example.hobbyhive.appwrite.AppwriteClient
import com.example.hobbyhive.appwrite.AppwriteConfig
import com.example.hobbyhive.appwrite.model.AppwriteHobby
import com.example.hobbyhive.appwrite.model.toRoomHobby
import com.example.hobbyhive.data.HobbyDao
import com.example.hobbyhive.data.HobbyRepository
import com.example.hobbyhive.model.Hobby
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Query
import io.appwrite.Role
import io.appwrite.exceptions.AppwriteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

// ═══════════════════════════════════════════════════════════════════════════
// AppwriteHobbyRepository.kt
//
// Offline-first hobby repository:
//   • All reads serve from Room (fast, offline-capable)
//   • All writes go to Appwrite first, then mirror to Room
//   • On app start, fetchAndSync() pulls Appwrite data into Room
//
// The "hobbyDocumentId" (Appwrite string ID) is stored in the Hobby.imageUri
// field temporarily; for a production app you'd add a dedicated column.
// A cleaner approach is to add `appwriteId: String?` to the Room Hobby entity.
// ═══════════════════════════════════════════════════════════════════════════

class AppwriteHobbyRepository(
    hobbyDao: HobbyDao,
    private val userPreferencesRepository: com.example.hobbyhive.data.UserPreferencesRepository
) : HobbyRepository(hobbyDao) {

    private val databases get() = AppwriteClient.databases

    private suspend fun getCurrentUserId(): String {
        return userPreferencesRepository.appwriteUserId.first() ?: ""
    }

    override suspend fun insertHobby(hobby: Hobby): Long {
        return createHobby(getCurrentUserId(), hobby).getOrNull()?.id ?: 0L
    }

    override suspend fun updateHobby(hobby: Hobby) {
        updateHobby(hobby.appwriteId ?: "", hobby)
    }

    override suspend fun deleteHobby(hobby: Hobby) {
        deleteHobby(hobby.appwriteId ?: "", hobby)
    }

    // ─── Read (from Room — always fast, works offline) ───────────────────

    /** Observe all hobbies as a Flow from Room. Used by HobbyViewModel. */
    override fun getAllHobbies(): Flow<List<Hobby>> = hobbyDao.getAllHobbies()

    override fun getHobbyById(id: Long): Flow<Hobby?> = hobbyDao.getHobbyById(id)

    override fun searchHobbies(query: String): Flow<List<Hobby>> = hobbyDao.searchHobbies(query)

    // ─── Sync: Appwrite → Room ───────────────────────────────────────────

    /**
     * Fetches all hobbies for [userId] from Appwrite and upserts them into Room.
     * Call once on login or app resume to keep the cache fresh.
     */
    suspend fun fetchAndSync(userId: String): Result<Unit> {
        return try {
            val response = databases.listDocuments(
                databaseId   = AppwriteConfig.DATABASE_ID,
                collectionId = AppwriteConfig.COLLECTION_HOBBIES,
                queries      = listOf(Query.equal("userId", userId))
            )
            response.documents.forEach { doc ->
                val appwriteHobby = AppwriteHobby.fromDocument(doc)
                hobbyDao.insert(appwriteHobby.toRoomHobby())
            }
            Result.success(Unit)
        } catch (e: AppwriteException) {
            Result.failure(Exception(e.message ?: "Sync failed"))
        }
    }

    // ─── Write (Appwrite first, then Room) ───────────────────────────────

    /**
     * Create a new hobby:
     *  1. Insert into Appwrite (cloud — source of truth)
     *  2. Insert the returned document into Room (local cache)
     */
    suspend fun createHobby(userId: String, hobby: Hobby): Result<Hobby> {
        return try {
            val data = mapOf(
                "userId" to userId, "name" to hobby.name,
                "description" to hobby.description, "category" to hobby.category.name,
                "rating" to hobby.rating.toDouble(), "progress" to hobby.progress,
                "status" to hobby.status.name, "notes" to hobby.notes,
                "imageFileId" to hobby.imageUri, "reminderEnabled" to hobby.reminderEnabled,
                "reminderTime" to hobby.reminderTime, "targetDate" to hobby.targetDate,
                "createdAt" to hobby.createdAt, "updatedAt" to hobby.updatedAt
            )
            val doc = databases.createDocument(
                databaseId   = AppwriteConfig.DATABASE_ID,
                collectionId = AppwriteConfig.COLLECTION_HOBBIES,
                documentId   = ID.unique(),
                data         = data,
                permissions  = listOf(
                    Permission.read(Role.user(userId)),
                    Permission.write(Role.user(userId))
                )
            )
            val appwriteHobby = AppwriteHobby.fromDocument(doc)
            val roomId = hobbyDao.insert(appwriteHobby.toRoomHobby())
            Result.success(appwriteHobby.toRoomHobby(roomId))
        } catch (e: AppwriteException) {
            // Log the real error — do NOT silently swallow it
            android.util.Log.e("AppwriteHobby", "createDocument FAILED: code=${e.code} msg=${e.message}")
            // Fallback: save to Room only so the user doesn't lose data offline
            val roomId = hobbyDao.insert(hobby)
            Result.success(hobby.copy(id = roomId))
        }
    }

    /**
     * Update an existing hobby by its Appwrite document ID.
     * If [documentId] is empty the update only writes to Room (offline mode).
     */
    suspend fun updateHobby(documentId: String, hobby: Hobby): Result<Unit> {
        return try {
            if (documentId.isNotEmpty()) {
                val data = mapOf(
                    "name" to hobby.name, "description" to hobby.description,
                    "category" to hobby.category.name, "rating" to hobby.rating.toDouble(),
                    "progress" to hobby.progress, "status" to hobby.status.name,
                    "notes" to hobby.notes, "imageFileId" to hobby.imageUri,
                    "reminderEnabled" to hobby.reminderEnabled,
                    "updatedAt" to System.currentTimeMillis()
                )
                databases.updateDocument(
                    databaseId   = AppwriteConfig.DATABASE_ID,
                    collectionId = AppwriteConfig.COLLECTION_HOBBIES,
                    documentId   = documentId,
                    data         = data
                )
            }
            hobbyDao.update(hobby.copy(updatedAt = System.currentTimeMillis()))
            Result.success(Unit)
        } catch (e: AppwriteException) {
            hobbyDao.update(hobby)
            Result.success(Unit)   // graceful offline fallback
        }
    }

    /**
     * Delete a hobby by its Appwrite document ID and Room local ID.
     */
    suspend fun deleteHobby(documentId: String, hobby: Hobby): Result<Unit> {
        return try {
            if (documentId.isNotEmpty()) {
                databases.deleteDocument(
                    databaseId   = AppwriteConfig.DATABASE_ID,
                    collectionId = AppwriteConfig.COLLECTION_HOBBIES,
                    documentId   = documentId
                )
            }
            hobbyDao.delete(hobby)
            Result.success(Unit)
        } catch (e: AppwriteException) {
            hobbyDao.delete(hobby)
            Result.success(Unit)
        }
    }
}
