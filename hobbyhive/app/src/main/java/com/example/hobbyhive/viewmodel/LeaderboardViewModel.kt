package com.example.hobbyhive.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyhive.data.HobbyHiveDatabase
import com.example.hobbyhive.data.HobbyRepository
import com.example.hobbyhive.data.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════
// LeaderboardViewModel — Local ranking of hobbies
// ═══════════════════════════════════════════════════

data class LeaderboardEntry(
    val rank: Int,
    val hobbyName: String,
    val emoji: String,
    val value: Int,
    val metric: String
)

data class LeaderboardUiState(
    val topTimeHobbies: List<LeaderboardEntry> = emptyList(),
    val topSessionHobbies: List<LeaderboardEntry> = emptyList(),
    val isLoading: Boolean = true
)

class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {

    private val database = HobbyHiveDatabase.getDatabase(application)
    private val hobbyRepository = HobbyRepository(database.hobbyDao())
    private val sessionRepository = SessionRepository(database.sessionDao())

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadLeaderboards()
    }

    private fun loadLeaderboards() {
        viewModelScope.launch {
            val hobbies = hobbyRepository.getAllHobbies().first()
            
            val timeEntries = hobbies.map { hobby ->
                val totalMinutes = sessionRepository.getTotalMinutesForHobby(hobby.id).first() ?: 0
                Triple(hobby.name, hobby.category.emoji, totalMinutes)
            }.filter { it.third > 0 }
            .sortedByDescending { it.third }
            .mapIndexed { index, triple ->
                LeaderboardEntry(index + 1, triple.first, triple.second, triple.third / 60, "hrs")
            }

            val sessionEntries = hobbies.map { hobby ->
                val sessionCount = sessionRepository.getSessionCountForHobby(hobby.id).first()
                Triple(hobby.name, hobby.category.emoji, sessionCount)
            }.filter { it.third > 0 }
            .sortedByDescending { it.third }
            .mapIndexed { index, triple ->
                LeaderboardEntry(index + 1, triple.first, triple.second, triple.third, "sessions")
            }

            _uiState.update {
                it.copy(
                    topTimeHobbies = timeEntries,
                    topSessionHobbies = sessionEntries,
                    isLoading = false
                )
            }
        }
    }
}
