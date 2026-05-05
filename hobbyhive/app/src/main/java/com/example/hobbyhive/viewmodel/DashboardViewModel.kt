package com.example.hobbyhive.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyhive.data.*
import com.example.hobbyhive.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

// ═══════════════════════════════════════════════════
// DashboardViewModel — Aggregated stats for dashboard
// ═══════════════════════════════════════════════════

data class DashboardUiState(
    val userName: String = "",
    val totalHobbies: Int = 0,
    val activeHobbies: Int = 0,
    val currentStreak: Int = 0,
    val weeklyMinutes: Int = 0,
    val recentSessions: List<SessionWithHobby> = emptyList(),
    val activeHobbyList: List<Hobby> = emptyList(),
    val dailyMinutesMap: Map<Long, Int> = emptyMap(),
    val isLoading: Boolean = true
)

data class SessionWithHobby(
    val session: Session,
    val hobbyName: String
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val database = HobbyHiveDatabase.getDatabase(application)
    val hobbyRepository = HobbyRepository(database.hobbyDao())
    val sessionRepository = SessionRepository(database.sessionDao())
    val goalRepository = GoalRepository(database.goalDao())
    val userPreferencesRepository = UserPreferencesRepository(application)
    val userRepository = UserRepository(database.userDao())

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            // Load user name
            val userId = userPreferencesRepository.userId.first()
            val user = userId?.let { userRepository.getUserById(it) }
            _uiState.update { it.copy(userName = user?.fullName ?: "User") }

            // Combine hobby stats
            combine(
                hobbyRepository.getHobbyCount(),
                hobbyRepository.getHobbyCountByStatus(HobbyStatus.ACTIVE),
                hobbyRepository.getHobbiesByStatus(HobbyStatus.ACTIVE),
                sessionRepository.getRecentSessions(5)
            ) { total, active, activeList, recent ->
                Triple(Pair(total, active), activeList, recent)
            }.collect { (counts, activeList, recentSessions) ->
                val sessionWithHobbies = recentSessions.map { session ->
                    val hobby = hobbyRepository.getHobbyByIdOnce(session.hobbyId)
                    SessionWithHobby(session, hobby?.name ?: "Unknown")
                }
                _uiState.update {
                    it.copy(
                        totalHobbies = counts.first,
                        activeHobbies = counts.second,
                        activeHobbyList = activeList.take(5),
                        recentSessions = sessionWithHobbies,
                        isLoading = false
                    )
                }
            }
        }

        // Weekly minutes
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val weekStart = cal.timeInMillis
            val weekEnd = weekStart + 7 * 24 * 60 * 60 * 1000L

            sessionRepository.getWeeklyMinutes(weekStart, weekEnd).collect { minutes ->
                _uiState.update { it.copy(weeklyMinutes = minutes) }
            }
        }

        // Streak calculation
        viewModelScope.launch {
            val streak = calculateStreak()
            _uiState.update { it.copy(currentStreak = streak) }
        }

        // Daily minutes for heatmap
        viewModelScope.launch {
            val map = mutableMapOf<Long, Int>()
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)

            for (i in 0 until 84) { // 12 weeks
                val dayEnd = cal.timeInMillis + 24 * 60 * 60 * 1000L - 1
                val mins = sessionRepository.getDailyMinutes(cal.timeInMillis, dayEnd)
                if (mins > 0) map[cal.timeInMillis] = mins
                cal.add(Calendar.DAY_OF_YEAR, -1)
            }
            _uiState.update { it.copy(dailyMinutesMap = map) }
        }
    }

    private suspend fun calculateStreak(): Int {
        var streak = 0
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        for (i in 0 until 365) {
            val dayStart = cal.timeInMillis
            val dayEnd = dayStart + 24 * 60 * 60 * 1000L - 1
            val count = sessionRepository.getSessionCountForDate(dayStart, dayEnd)
            if (count > 0) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }

    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour in 5..11 -> "Good morning"
            hour in 12..16 -> "Good afternoon"
            hour in 17..21 -> "Good evening"
            else -> "You're up late"
        }
    }

    fun getGreetingEmoji(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "🌅"
            hour < 17 -> "☀️"
            else -> "🌙"
        }
    }
}
