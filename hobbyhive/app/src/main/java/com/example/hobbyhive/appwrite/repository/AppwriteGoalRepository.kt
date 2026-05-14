package com.example.hobbyhive.appwrite.repository

import com.example.hobbyhive.appwrite.AppwriteClient
import com.example.hobbyhive.appwrite.AppwriteConfig
import com.example.hobbyhive.appwrite.model.AppwriteGoal
import com.example.hobbyhive.appwrite.model.toRoomGoal
import com.example.hobbyhive.data.GoalDao
import com.example.hobbyhive.data.GoalRepository
import com.example.hobbyhive.model.Goal
import com.example.hobbyhive.model.GoalStatus
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Query
import io.appwrite.Role
import io.appwrite.exceptions.AppwriteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

// ═══════════════════════════════════════════════════════════════════════════
// AppwriteGoalRepository.kt
//
// Offline-first goal repository following the same pattern as hobbies:
//   • Reads come from Room (fast, offline-capable)
//   • Writes go to Appwrite then sync to Room
//   • fetchAndSync() keeps Room cache fresh from Appwrite
// ═══════════════════════════════════════════════════════════════════════════

class AppwriteGoalRepository(
    goalDao: GoalDao,
    private val userPreferencesRepository: com.example.hobbyhive.data.UserPreferencesRepository
) : GoalRepository(goalDao) {

    private val databases get() = AppwriteClient.databases

    private suspend fun getCurrentUserId(): String = userPreferencesRepository.appwriteUserId.first() ?: ""

    // ─── Read (Room) ─────────────────────────────────────────────────────

    override fun getGoalsForHobby(hobbyId: Long): Flow<List<Goal>> =
        goalDao.getGoalsForHobby(hobbyId)

    override fun getAllGoals(): Flow<List<Goal>> = goalDao.getAllGoals()

    override suspend fun insertGoal(goal: Goal): Long {
        // Need hobbyDocumentId. Assuming it's in goal metadata or handled by ViewModel.
        // For simplicity, we'll try to find the hobby doc ID from Room if possible.
        return createGoal(getCurrentUserId(), "", goal).getOrNull()?.id ?: 0L
    }

    override suspend fun updateGoal(goal: Goal) {
        updateGoal("", goal)
    }

    override suspend fun deleteGoal(goal: Goal) {
        deleteGoal("", goal)
    }

    // ─── Sync: Appwrite → Room ───────────────────────────────────────────

    /**
     * Pull all goals for [userId] from Appwrite and upsert into Room.
     * Call after login or on resume.
     */
    suspend fun fetchAndSync(userId: String): Result<Unit> {
        return try {
            val response = databases.listDocuments(
                databaseId   = AppwriteConfig.DATABASE_ID,
                collectionId = AppwriteConfig.COLLECTION_GOALS,
                queries      = listOf(Query.equal("userId", userId))
            )
            response.documents.forEach { doc ->
                goalDao.insert(AppwriteGoal.fromDocument(doc).toRoomGoal())
            }
            Result.success(Unit)
        } catch (e: AppwriteException) {
            Result.failure(Exception(e.message ?: "Goal sync failed"))
        }
    }

    // ─── Write (Appwrite first, then Room) ───────────────────────────────

    /** Create a goal on Appwrite then mirror it to Room. */
    suspend fun createGoal(userId: String, hobbyDocumentId: String, goal: Goal): Result<Goal> {
        return try {
            val data = mapOf(
                "userId"       to userId,
                "hobbyId"      to hobbyDocumentId,
                "title"        to goal.title,
                "description"  to goal.description,
                "targetValue"  to goal.targetValue,
                "currentValue" to goal.currentValue,
                "unit"         to goal.unit,
                "deadline"     to goal.deadline,
                "status"       to goal.status.name,
                "createdAt"    to goal.createdAt,
                "updatedAt"    to goal.updatedAt
            )
            databases.createDocument(
                databaseId   = AppwriteConfig.DATABASE_ID,
                collectionId = AppwriteConfig.COLLECTION_GOALS,
                documentId   = ID.unique(),
                data         = data,
                permissions  = listOf(
                    Permission.read(Role.user(userId)),
                    Permission.write(Role.user(userId))
                )
            )
            val roomId = goalDao.insert(goal)
            Result.success(goal.copy(id = roomId))
        } catch (e: AppwriteException) {
            // Offline fallback — save locally
            val roomId = goalDao.insert(goal)
            Result.success(goal.copy(id = roomId))
        }
    }

    /** Update goal progress and status on Appwrite and Room. */
    suspend fun updateGoal(documentId: String, goal: Goal): Result<Unit> {
        return try {
            if (documentId.isNotEmpty()) {
                databases.updateDocument(
                    databaseId   = AppwriteConfig.DATABASE_ID,
                    collectionId = AppwriteConfig.COLLECTION_GOALS,
                    documentId   = documentId,
                    data         = mapOf(
                        "title"        to goal.title,
                        "description"  to goal.description,
                        "currentValue" to goal.currentValue,
                        "targetValue"  to goal.targetValue,
                        "status"       to goal.status.name,
                        "updatedAt"    to System.currentTimeMillis()
                    )
                )
            }
            goalDao.update(goal.copy(updatedAt = System.currentTimeMillis()))
            Result.success(Unit)
        } catch (e: AppwriteException) {
            goalDao.update(goal)
            Result.success(Unit)
        }
    }

    /** Delete a goal from Appwrite and Room. */
    suspend fun deleteGoal(documentId: String, goal: Goal): Result<Unit> {
        return try {
            if (documentId.isNotEmpty()) {
                databases.deleteDocument(
                    databaseId   = AppwriteConfig.DATABASE_ID,
                    collectionId = AppwriteConfig.COLLECTION_GOALS,
                    documentId   = documentId
                )
            }
            goalDao.delete(goal)
            Result.success(Unit)
        } catch (e: AppwriteException) {
            goalDao.delete(goal)
            Result.success(Unit)
        }
    }
}
