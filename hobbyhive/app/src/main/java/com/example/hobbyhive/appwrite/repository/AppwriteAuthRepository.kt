package com.example.hobbyhive.appwrite.repository

import com.example.hobbyhive.appwrite.AppwriteClient
import com.example.hobbyhive.appwrite.AppwriteConfig
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.User

// ═══════════════════════════════════════════════════════════════════════════
// AppwriteAuthRepository.kt
//
// Handles all Appwrite authentication operations:
//   • signUp    — creates a new Appwrite user account + opens an email session
//   • login     — opens an email session for existing user
//   • logout    — deletes the current session (invalidates server-side token)
//   • getUser   — returns the currently logged-in Appwrite User object
//   • isActive  — checks whether a valid session exists right now
//
// All functions return Result<T> so callers can use fold{} without try/catch.
// ═══════════════════════════════════════════════════════════════════════════

class AppwriteAuthRepository {

    private val account get() = AppwriteClient.account

    /**
     * Register a new user.
     * Creates the Appwrite account and immediately opens an email session
     * so the user is logged in right after signing up.
     *
     * @param name     Display name shown in the Appwrite Console
     * @param email    User's email address
     * @param password Must be at least 8 characters (Appwrite requirement)
     */
    suspend fun signUp(name: String, email: String, password: String): Result<User<Map<String, Any>>> {
        return try {
            // Step 1: Create the user account
            account.create(
                userId   = ID.unique(),   // Appwrite generates a unique ID
                email    = email,
                password = password,
                name     = name
            )
            // Step 2: Immediately log in so the session is established
            account.createEmailPasswordSession(email = email, password = password)
            // Step 3: Return the freshly-created user
            val user = account.get()
            Result.success(user)
        } catch (e: AppwriteException) {
            Result.failure(Exception(friendlyMessage(e)))
        }
    }

    /**
     * Log in an existing user with email + password.
     * Creates a new Appwrite session (stored as a cookie by the SDK).
     */
    suspend fun login(email: String, password: String): Result<User<Map<String, Any>>> {
        return try {
            account.createEmailPasswordSession(email = email, password = password)
            val user = account.get()
            Result.success(user)
        } catch (e: AppwriteException) {
            Result.failure(Exception(friendlyMessage(e)))
        }
    }

    /**
     * Log out the current user by deleting the active session.
     * The string "current" tells Appwrite to delete whichever session is active.
     */
    suspend fun logout(): Result<Unit> {
        return try {
            account.deleteSession(sessionId = "current")
            Result.success(Unit)
        } catch (e: AppwriteException) {
            Result.failure(Exception(friendlyMessage(e)))
        }
    }

    /**
     * Fetch the currently authenticated Appwrite User.
     * Returns null-wrapped failure if no session is active.
     */
    suspend fun getCurrentUser(): Result<User<Map<String, Any>>> {
        return try {
            val user = account.get()
            Result.success(user)
        } catch (e: AppwriteException) {
            Result.failure(Exception(friendlyMessage(e)))
        }
    }

    /**
     * Lightweight session check — returns true if a valid session exists.
     * Use this in SplashScreen to decide where to navigate.
     */
    suspend fun isSessionActive(): Boolean {
        return try {
            account.get()   // throws if no session
            true
        } catch (e: AppwriteException) {
            false
        }
    }

    /**
     * Update the display name and email on the Appwrite account.
     * Called from the Profile / Settings screen.
     */
    suspend fun updateProfile(name: String): Result<User<Map<String, Any>>> {
        return try {
            val user = account.updateName(name = name)
            Result.success(user)
        } catch (e: AppwriteException) {
            Result.failure(Exception(friendlyMessage(e)))
        }
    }

    /**
     * Change account password. Requires the current password for security.
     */
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        return try {
            account.updatePassword(password = newPassword, oldPassword = oldPassword)
            Result.success(Unit)
        } catch (e: AppwriteException) {
            Result.failure(Exception(friendlyMessage(e)))
        }
    }

    // ─── Private helpers ─────────────────────────────────────────────────

    /**
     * Maps Appwrite error codes to user-friendly messages.
     * The raw Appwrite messages are technical and not suitable for display.
     */
    private fun friendlyMessage(e: AppwriteException): String {
        return when (e.code) {
            401  -> "Incorrect email or password."
            409  -> "An account with this email already exists."
            429  -> "Too many attempts. Please wait a moment and try again."
            500  -> "Server error. Please try again later."
            else -> e.message ?: "An unexpected error occurred."
        }
    }
}
