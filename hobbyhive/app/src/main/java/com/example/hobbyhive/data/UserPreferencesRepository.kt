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
        private val AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val LOGIN_TIMESTAMP = longPreferencesKey("login_timestamp")
        private val KEEP_LOGGED_IN = booleanPreferencesKey("keep_logged_in")
        private val USER_ID = longPreferencesKey("user_id")
        private val THEME_MODE = stringPreferencesKey("theme_mode")  // "light", "dark", "system"
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")

        private const val SEVEN_DAYS_MS = 7 * 24 * 60 * 60 * 1000L
    }

    // ─── Auth ───

    suspend fun saveAuthToken(token: String, userId: Long, keepLoggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[AUTH_TOKEN] = token
            prefs[LOGIN_TIMESTAMP] = System.currentTimeMillis()
            prefs[KEEP_LOGGED_IN] = keepLoggedIn
            prefs[USER_ID] = userId
        }
    }

    suspend fun clearAuth() {
        context.dataStore.edit { prefs ->
            prefs.remove(AUTH_TOKEN)
            prefs.remove(LOGIN_TIMESTAMP)
            prefs.remove(KEEP_LOGGED_IN)
            prefs.remove(USER_ID)
        }
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
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
