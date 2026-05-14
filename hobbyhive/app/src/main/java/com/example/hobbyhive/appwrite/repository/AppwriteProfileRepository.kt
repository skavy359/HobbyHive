package com.example.hobbyhive.appwrite.repository

import com.example.hobbyhive.appwrite.AppwriteClient
import com.example.hobbyhive.appwrite.AppwriteConfig
import com.example.hobbyhive.data.UserDao
import com.example.hobbyhive.data.UserPreferencesRepository
import com.example.hobbyhive.data.UserRepository
import com.example.hobbyhive.model.User
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Query
import io.appwrite.Role
import io.appwrite.exceptions.AppwriteException
import kotlinx.coroutines.flow.first

class AppwriteProfileRepository(
    userDao: UserDao,
    private val userPreferencesRepository: UserPreferencesRepository
) : UserRepository(userDao) {

    private val databases get() = AppwriteClient.databases

    private suspend fun getCurrentAppwriteUserId(): String =
        userPreferencesRepository.appwriteUserId.first() ?: ""

    override suspend fun register(fullName: String, email: String, password: String): Result<User> {
        val result = super.register(fullName, email, password)
        if (result.isSuccess) {
            syncProfileToAppwrite(fullName, "")
        }
        return result
    }

    override suspend fun updateProfile(userId: Long, fullName: String, about: String): Result<User> {
        val result = super.updateProfile(userId, fullName, about)
        if (result.isSuccess) {
            syncProfileToAppwrite(fullName, about)
        }
        return result
    }

    suspend fun syncProfileToAppwrite(fullName: String, about: String): Result<Unit> {
        return try {
            val appwriteUserId = getCurrentAppwriteUserId()
            if (appwriteUserId.isEmpty()) return Result.failure(Exception("Not logged in to Appwrite"))

            // Check if profile document already exists
            val response = databases.listDocuments(
                databaseId = AppwriteConfig.DATABASE_ID,
                collectionId = AppwriteConfig.COLLECTION_PROFILES,
                queries = listOf(Query.equal("userId", appwriteUserId))
            )

            val data = mapOf(
                "userId" to appwriteUserId,
                "fullName" to fullName,
                "about" to about
            )

            if (response.documents.isEmpty()) {
                databases.createDocument(
                    databaseId = AppwriteConfig.DATABASE_ID,
                    collectionId = AppwriteConfig.COLLECTION_PROFILES,
                    documentId = ID.unique(),
                    data = data,
                    permissions = listOf(
                        Permission.read(Role.any()),
                        Permission.write(Role.user(appwriteUserId))
                    )
                )
            } else {
                databases.updateDocument(
                    databaseId = AppwriteConfig.DATABASE_ID,
                    collectionId = AppwriteConfig.COLLECTION_PROFILES,
                    documentId = response.documents[0].id,
                    data = data
                )
            }
            Result.success(Unit)
        } catch (e: AppwriteException) {
            Result.failure(e)
        }
    }
}
