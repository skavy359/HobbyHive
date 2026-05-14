package com.example.hobbyhive.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ═══════════════════════════════════════════════════
// DataStore Preferences — Auth, Theme, Onboarding
// ═══════════════════════════════════════════════════

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hobbyhive_prefs")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        private val APPWRITE_SESSION_ID = stringPreferencesKey("appwrite_session_id")
        private val APPWRITE_USER_ID    = stringPreferencesKey("appwrite_user_id")   // ← real Appwrite user ID
        private val AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val LOGIN_TIMESTAMP = longPreferencesKey("login_timestamp")
        private val KEEP_LOGGED_IN = booleanPreferencesKey("keep_logged_in")
        private val USER_ID = longPreferencesKey("user_id")
        private val THEME_MODE = stringPreferencesKey("theme_mode")  // "light", "dark", "system"
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val USER_NAME = stringPreferencesKey("user_name")

        private const val SEVEN_DAYS_MS = 7 * 24 * 60 * 60 * 1000L
    }

    // ─── Auth ───

    suspend fun saveAppwriteSession(sessionId: String) {
        context.dataStore.edit { prefs ->
            prefs[APPWRITE_SESSION_ID] = sessionId
        }
    }

    /** Save the real Appwrite user ID (e.g. "6a03xxxx…") after login/signup */
    suspend fun saveAppwriteUserId(appwriteUserId: String, fullName: String? = null) {
        context.dataStore.edit { prefs ->
            // Store in both AUTH_TOKEN (used by LoginScreen/RegisterScreen) and
            // APPWRITE_USER_ID so both code paths always have the correct ID
            prefs[AUTH_TOKEN] = appwriteUserId
            prefs[APPWRITE_USER_ID] = appwriteUserId
            fullName?.let { prefs[USER_NAME] = it }
        }
    }

    suspend fun saveAuthToken(token: String, userId: String, keepLoggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[AUTH_TOKEN] = token
            prefs[LOGIN_TIMESTAMP] = System.currentTimeMillis()
            prefs[KEEP_LOGGED_IN] = keepLoggedIn
            prefs[APPWRITE_USER_ID] = userId
            // Maintain a dummy Long ID for legacy components that still read USER_ID
            prefs[USER_ID] = 1L
        }
    }

    suspend fun clearAuth() {
        context.dataStore.edit { prefs ->
            prefs.remove(APPWRITE_SESSION_ID)
            prefs.remove(APPWRITE_USER_ID)
            prefs.remove(AUTH_TOKEN)
            prefs.remove(LOGIN_TIMESTAMP)
            prefs.remove(KEEP_LOGGED_IN)
            prefs.remove(USER_ID)
        }
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        val appwriteToken = prefs[APPWRITE_SESSION_ID]
        if (!appwriteToken.isNullOrEmpty()) return@map true

        val token = prefs[AUTH_TOKEN]
        val timestamp = prefs[LOGIN_TIMESTAMP] ?: 0L
        val keepLoggedIn = prefs[KEEP_LOGGED_IN] ?: false

        if (token.isNullOrEmpty()) return@map false
        if (!keepLoggedIn) return@map false

        // Check 7-day window
        val elapsed = System.currentTimeMillis() - timestamp
        elapsed < SEVEN_DAYS_MS
    }

    val userId: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[USER_ID]
    }

    val appwriteUserId: Flow<String?> = context.dataStore.data.map { prefs ->
        // AUTH_TOKEN stores the real Appwrite user ID (set by LoginScreen & RegisterScreen)
        // Fall back to APPWRITE_USER_ID if set by AuthViewModel path
        prefs[AUTH_TOKEN]?.takeIf { it.isNotEmpty() } ?: prefs[APPWRITE_USER_ID]
    }

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME] ?: "User"
    }

    // ─── Theme ───

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[THEME_MODE] = mode
        }
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[THEME_MODE] ?: "system"
    }

    // ─── Onboarding ───

    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED] = true
        }
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ONBOARDING_COMPLETED] ?: false
    }

    // ─── Notifications ───

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[NOTIFICATIONS_ENABLED] ?: true
    }
}
