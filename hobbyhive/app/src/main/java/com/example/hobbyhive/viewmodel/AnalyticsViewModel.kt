package com.example.hobbyhive.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyhive.data.*
import com.example.hobbyhive.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class AnalyticsUiState(
    val totalHoursLogged: Int = 0,
    val totalSessions: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val weeklyHours: List<Pair<String, Float>> = emptyList(),
    val topHobbies: List<HobbyTimeEntry> = emptyList(),
    val engagementScore: Float = 0f,
    val burnoutRisk: String = "Low",
    val insights: List<String> = emptyList(),
    val isLoading: Boolean = true
)

data class HobbyTimeEntry(val hobbyName: String, val emoji: String, val totalMinutes: Int)

data class AnalyticsData(
    val totalMin: Int,
    val totalSess: Int,
    val hobbyCount: Int,
    val hobbies: List<Hobby>
)

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = HobbyHiveDatabase.getDatabase(application)
    val sessionRepository = SessionRepository(database.sessionDao())
    val hobbyRepository = HobbyRepository(database.hobbyDao())
    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init { loadAnalytics() }

    private fun loadAnalytics() {
        viewModelScope.launch {
            combine(
                sessionRepository.getTotalMinutes(),
                sessionRepository.getSessionCount(),
                hobbyRepository.getHobbyCount(),
                hobbyRepository.getAllHobbies()
            ) { totalMin, totalSess, hobbyCount, hobbies ->
                AnalyticsData(totalMin ?: 0, totalSess, hobbyCount, hobbies)
            }.collectLatest { data ->
                val totalMin = data.totalMin
                val totalSess = data.totalSess
                val hobbyCount = data.hobbyCount
                val hobbies = data.hobbies

                // Top hobbies
                val topEntries = hobbies.map { h ->
                    val min = sessionRepository.getTotalMinutesForHobby(h.id).first() ?: 0
                    HobbyTimeEntry(h.name, h.category.emoji, min)
                }.filter { it.totalMinutes > 0 }.sortedByDescending { it.totalMinutes }.take(5)

                // Weekly
                val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                val cal = Calendar.getInstance()
                while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) cal.add(Calendar.DAY_OF_YEAR, -1)
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
                val weekly = (0 until 7).map { i ->
                    val s = cal.timeInMillis + i * 86400000L
                    days[i] to (sessionRepository.getDailyMinutes(s, s + 86399999L) / 60f)
                }

                // Streaks
                val currStreak = calcStreak(false)
                val maxStreak = calcStreak(true)

                // Engagement & Insights
                val eng = ((totalSess.coerceAtMost(100) / 100f) * 40 + (totalMin.coerceAtMost(6000) / 6000f) * 40 + (hobbyCount.coerceAtMost(10) / 10f) * 20).coerceIn(0f, 100f)
                val cal2 = Calendar.getInstance(); cal2.set(Calendar.HOUR_OF_DAY, 0); cal2.set(Calendar.MINUTE, 0); cal2.set(Calendar.SECOND, 0); cal2.set(Calendar.MILLISECOND, 0)
                var w7 = 0; repeat(7) { cal2.add(Calendar.DAY_OF_YEAR, -it); val ds = cal2.timeInMillis; w7 += sessionRepository.getDailyMinutes(ds, ds + 86399999L) }
                val burn = when { w7 / 7 > 240 -> "High"; w7 / 7 > 120 -> "Moderate"; else -> "Low" }
                
                val ins = mutableListOf<String>()
                if (totalSess > 0) ins.add("Avg session: ${totalMin / totalSess}min. ${if (totalMin / totalSess > 45) "Great focus!" else "Try longer sessions."}")
                if (hobbyCount > 3) ins.add("Exploring $hobbyCount hobbies — variety keeps it fresh! 🌈")
                if (totalMin > 600) ins.add("Over 10 hours logged — real expertise building! 💪")
                if (totalSess == 0) ins.add("Log your first session to see insights! 🚀")

                _uiState.update { 
                    it.copy(
                        totalHoursLogged = totalMin / 60,
                        totalSessions = totalSess,
                        currentStreak = currStreak,
                        longestStreak = maxStreak,
                        weeklyHours = weekly,
                        topHobbies = topEntries,
                        engagementScore = eng,
                        burnoutRisk = burn,
                        insights = ins,
                        isLoading = false
                    ) 
                }
            }
        }
    }

    private suspend fun calcStreak(longest: Boolean): Int {
        var s = 0; var mx = 0; val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY,0); cal.set(Calendar.MINUTE,0); cal.set(Calendar.SECOND,0); cal.set(Calendar.MILLISECOND,0)
        repeat(365) {
            val ds = cal.timeInMillis
            if (sessionRepository.getSessionCountForDate(ds, ds+86399999L) > 0) { s++; mx = maxOf(mx,s) }
            else { if (!longest) return s; s = 0 }
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        return if (longest) mx else s
    }
}
