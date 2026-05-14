package com.example.hobbyhive.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyhive.data.HobbyHiveDatabase
import com.example.hobbyhive.model.Hobby
import com.example.hobbyhive.model.Session
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════
// SessionsViewModel — Session management
// ═══════════════════════════════════════════════════

data class SessionsUiState(
    val sessions: List<SessionWithHobbyName> = emptyList(),
    val hobbies: List<Hobby> = emptyList(),
    val totalSessions: Int = 0,
    val totalMinutes: Int = 0,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val successMessage: String? = null
)

data class SessionWithHobbyName(
    val session: Session,
    val hobbyName: String
)

class SessionsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = HobbyHiveDatabase.getDatabase(application)
    private val userPrefs = com.example.hobbyhive.data.UserPreferencesRepository(application)
    
    val sessionRepository = com.example.hobbyhive.appwrite.repository.AppwriteSessionRepository(
        database.sessionDao(),
        userPrefs
    )
    val hobbyRepository = com.example.hobbyhive.appwrite.repository.AppwriteHobbyRepository(
        database.hobbyDao(),
        userPrefs
    )

    private val _uiState = MutableStateFlow(SessionsUiState())
    val uiState: StateFlow<SessionsUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        loadSessions()
        loadHobbies()
        loadStats()
        
        // Initial sync
        viewModelScope.launch {
            val userId = userPrefs.appwriteUserId.first()
            if (!userId.isNullOrEmpty()) {
                sessionRepository.fetchAndSync(userId)
            }
        }
    }

    private fun loadSessions() {
        viewModelScope.launch {
            combine(
                sessionRepository.getAllSessions(),
                _searchQuery
            ) { sessions, query ->
                sessions to query
            }.collect { (sessions, query) ->
                val withNames = sessions.map { session ->
                    val hobby = hobbyRepository.getHobbyByIdOnce(session.hobbyId)
                    SessionWithHobbyName(session, hobby?.name ?: "Unknown Hobby")
                }
                val filtered = if (query.isBlank()) withNames else {
                    withNames.filter {
                        it.hobbyName.contains(query, ignoreCase = true) ||
                        it.session.notes.contains(query, ignoreCase = true)
                    }
                }
                _uiState.update { it.copy(sessions = filtered, isLoading = false) }
            }
        }
    }

    private fun loadHobbies() {
        viewModelScope.launch {
            hobbyRepository.getAllHobbies().collect { hobbies ->
                _uiState.update { it.copy(hobbies = hobbies) }
            }
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            combine(
                sessionRepository.getSessionCount(),
                sessionRepository.getTotalMinutes()
            ) { count, minutes -> count to (minutes ?: 0) }
                .collect { (count, minutes) ->
                    _uiState.update { it.copy(totalSessions = count, totalMinutes = minutes) }
                }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun logSession(hobbyId: Long, durationMinutes: Int, sessionDate: Long, notes: String) {
        viewModelScope.launch {
            // Find hobby doc ID
            val hobby = database.hobbyDao().getHobbyById(hobbyId).first()
            val hobbyDocId = hobby?.appwriteId ?: ""
            val userId = userPrefs.appwriteUserId.first() ?: ""

            sessionRepository.logSession(
                userId = userId,
                hobbyDocumentId = hobbyDocId,
                session = Session(
                    hobbyId = hobbyId,
                    durationMinutes = durationMinutes,
                    sessionDate = sessionDate,
                    notes = notes
                )
            )
            showSuccess("Session logged successfully!")
        }
    }

    fun updateSession(session: Session) {
        viewModelScope.launch {
            // updateSession not fully implemented in AppwriteSessionRepository yet, but we'll use Room for now
            // since we primarily log/delete sessions.
            database.sessionDao().update(session)
            showSuccess("Session updated!")
        }
    }

    fun deleteSession(session: Session) {
        viewModelScope.launch {
            sessionRepository.deleteSession(session.appwriteId ?: "", session)
            showSuccess("Session deleted")
        }
    }

    private fun showSuccess(message: String) {
        _uiState.update { it.copy(successMessage = message) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            _uiState.update { it.copy(successMessage = null) }
        }
    }

    fun formatDuration(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return when {
            hours == 0 -> "${mins}m"
            mins == 0 -> "${hours}h"
            else -> "${hours}h ${mins}m"
        }
    }
}
