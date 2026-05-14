package com.example.hobbyhive.appwrite.repository

import com.example.hobbyhive.appwrite.AppwriteClient
import com.example.hobbyhive.appwrite.AppwriteConfig
import com.example.hobbyhive.appwrite.model.AppwriteSession
import com.example.hobbyhive.appwrite.model.toRoomSession
import com.example.hobbyhive.data.SessionDao
import com.example.hobbyhive.data.SessionRepository
import com.example.hobbyhive.model.Session
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Query
import io.appwrite.Role
import io.appwrite.exceptions.AppwriteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

// ═══════════════════════════════════════════════════════════════════════════
// AppwriteSessionRepository.kt
//
// Offline-first session repository.
// Practice sessions are short-lived writes (user logs a session, moves on)
// so we write to Appwrite + Room together and read from Room.
// ═══════════════════════════════════════════════════════════════════════════

class AppwriteSessionRepository(
    sessionDao: SessionDao,
    private val userPreferencesRepository: com.example.hobbyhive.data.UserPreferencesRepository
) : SessionRepository(sessionDao) {

    private val databases get() = AppwriteClient.databases

    private suspend fun getCurrentUserId(): String = userPreferencesRepository.appwriteUserId.first() ?: ""

    // ─── Read (Room) ─────────────────────────────────────────────────────

    override fun getSessionsForHobby(hobbyId: Long): Flow<List<Session>> =
        sessionDao.getSessionsForHobby(hobbyId)

    override fun getAllSessions(): Flow<List<Session>> = sessionDao.getAllSessions()

    override fun getTotalMinutesForHobby(hobbyId: Long): Flow<Int?> =
        sessionDao.getTotalMinutesForHobby(hobbyId)

    override suspend fun insertSession(session: Session): Long {
        return logSession(getCurrentUserId(), "", session).getOrNull()?.id ?: 0L
    }

    override suspend fun deleteSession(session: Session) {
        deleteSession("", session)
    }

    // ─── Sync: Appwrite → Room ───────────────────────────────────────────

    /**
     * Pull all sessions for [userId] from Appwrite and upsert into Room.
     */
    suspend fun fetchAndSync(userId: String): Result<Unit> {
        return try {
            val response = databases.listDocuments(
                databaseId   = AppwriteConfig.DATABASE_ID,
                collectionId = AppwriteConfig.COLLECTION_SESSIONS,
                queries      = listOf(Query.equal("userId", userId))
            )
            response.documents.forEach { doc ->
                sessionDao.insert(AppwriteSession.fromDocument(doc).toRoomSession())
            }
            Result.success(Unit)
        } catch (e: AppwriteException) {
            Result.failure(Exception(e.message ?: "Session sync failed"))
        }
    }

    // ─── Write ───────────────────────────────────────────────────────────

    /**
     * Log a new practice session.
     * [hobbyDocumentId] is the Appwrite document ID of the parent hobby.
     */
    suspend fun logSession(userId: String, hobbyDocumentId: String, session: Session): Result<Session> {
        return try {
            val data = mapOf(
                "userId"          to userId,
                "hobbyId"         to hobbyDocumentId,
                "durationMinutes" to session.durationMinutes,
                "sessionDate"     to session.sessionDate,
                "notes"           to session.notes,
                "createdAt"       to session.createdAt
            )
            databases.createDocument(
                databaseId   = AppwriteConfig.DATABASE_ID,
                collectionId = AppwriteConfig.COLLECTION_SESSIONS,
                documentId   = ID.unique(),
                data         = data,
                permissions  = listOf(
                    Permission.read(Role.user(userId)),
                    Permission.write(Role.user(userId))
                )
            )
            val roomId = sessionDao.insert(session)
            Result.success(session.copy(id = roomId))
        } catch (e: AppwriteException) {
            val roomId = sessionDao.insert(session)
            Result.success(session.copy(id = roomId))
        }
    }

    /** Delete a session from Appwrite and Room. */
    suspend fun deleteSession(documentId: String, session: Session): Result<Unit> {
        return try {
            if (documentId.isNotEmpty()) {
                databases.deleteDocument(
                    databaseId   = AppwriteConfig.DATABASE_ID,
                    collectionId = AppwriteConfig.COLLECTION_SESSIONS,
                    documentId   = documentId
                )
            }
            sessionDao.delete(session)
            Result.success(Unit)
        } catch (e: AppwriteException) {
            sessionDao.delete(session)
            Result.success(Unit)
        }
    }
}
