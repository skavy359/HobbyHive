package com.example.hobbyhive.data

import com.example.hobbyhive.model.User
import java.security.MessageDigest

// ═══════════════════════════════════════════════════
// User Repository — Auth logic with password hashing
// ═══════════════════════════════════════════════════

class UserRepository(private val userDao: UserDao) {

    suspend fun register(fullName: String, email: String, password: String): Result<User> {
        // Check if user already exists
        val existing = userDao.getUserByEmail(email)
        if (existing != null) {
            return Result.failure(Exception("An account with this email already exists"))
        }

        val user = User(
            fullName = fullName,
            email = email,
            passwordHash = hashPassword(password)
        )
        val userId = userDao.insert(user)
        return Result.success(user.copy(id = userId))
    }

    suspend fun login(email: String, password: String): Result<User> {
        val user = userDao.getUserByEmail(email)
            ?: return Result.failure(Exception("No account found with this email"))

        if (user.passwordHash != hashPassword(password)) {
            return Result.failure(Exception("Incorrect password"))
        }

        return Result.success(user)
    }

    suspend fun getUserById(userId: Long): User? = userDao.getUserById(userId)

    fun getUserByIdFlow(userId: Long) = userDao.getUserByIdFlow(userId)

    suspend fun updateProfile(userId: Long, fullName: String, about: String): Result<User> {
        val user = userDao.getUserById(userId)
            ?: return Result.failure(Exception("User not found"))
        val updated = user.copy(fullName = fullName, about = about)
        userDao.update(updated)
        return Result.success(updated)
    }

    suspend fun changePassword(userId: Long, oldPassword: String, newPassword: String): Result<Unit> {
        val user = userDao.getUserById(userId)
            ?: return Result.failure(Exception("User not found"))

        if (user.passwordHash != hashPassword(oldPassword)) {
            return Result.failure(Exception("Current password is incorrect"))
        }
        if (newPassword.length < 6) {
            return Result.failure(Exception("New password must be at least 6 characters"))
        }

        userDao.update(user.copy(passwordHash = hashPassword(newPassword)))
        return Result.success(Unit)
    }

    suspend fun deleteAccount(userId: Long) {
        userDao.deleteById(userId)
    }

    private fun hashPassword(password: String): String {
        val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
