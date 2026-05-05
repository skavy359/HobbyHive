package com.example.hobbyhive.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyhive.data.*
import com.example.hobbyhive.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class PlannerUiState(
    val events: List<StudyEvent> = emptyList(),
    val hobbies: List<Hobby> = emptyList(),
    val selectedDate: Long = System.currentTimeMillis(),
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val plannedCount: Int = 0,
    val completedCount: Int = 0,
    val missedCount: Int = 0,
    val isLoading: Boolean = true,
    val successMessage: String? = null
)

class StudyPlannerViewModel(application: Application) : AndroidViewModel(application) {
    private val database = HobbyHiveDatabase.getDatabase(application)
    val eventRepository = StudyEventRepository(database.studyEventDao())
    val hobbyRepository = HobbyRepository(database.hobbyDao())
    private val _uiState = MutableStateFlow(PlannerUiState())
    val uiState: StateFlow<PlannerUiState> = _uiState.asStateFlow()

    init {
        loadEvents(); loadHobbies(); loadCounts()
        viewModelScope.launch { eventRepository.markOverdueAsMissed() }
    }

    private fun loadEvents() {
        viewModelScope.launch {
            eventRepository.getAllEvents().collect { events ->
                _uiState.update { it.copy(events = events, isLoading = false) }
            }
        }
    }

    private fun loadHobbies() {
        viewModelScope.launch {
            hobbyRepository.getAllHobbies().collect { h -> _uiState.update { it.copy(hobbies = h) } }
        }
    }

    private fun loadCounts() {
        viewModelScope.launch {
            combine(
                eventRepository.getCountByStatus(EventStatus.PLANNED),
                eventRepository.getCountByStatus(EventStatus.COMPLETED),
                eventRepository.getCountByStatus(EventStatus.MISSED)
            ) { p, c, m -> Triple(p, c, m) }.collect { (p, c, m) ->
                _uiState.update { it.copy(plannedCount = p, completedCount = c, missedCount = m) }
            }
        }
    }

    fun selectDate(millis: Long) { _uiState.update { it.copy(selectedDate = millis) } }
    fun selectMonth(month: Int, year: Int) { _uiState.update { it.copy(selectedMonth = month, selectedYear = year) } }

    fun getEventsForDate(date: Long): List<StudyEvent> {
        val cal = Calendar.getInstance().apply { timeInMillis = date }
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        val dayStart = cal.timeInMillis; val dayEnd = dayStart + 86399999L
        return _uiState.value.events.filter { it.startTime in dayStart..dayEnd }
    }

    fun getDatesWithEvents(): Set<Long> {
        val cal = Calendar.getInstance()
        return _uiState.value.events.map { e ->
            cal.timeInMillis = e.startTime
            cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }.toSet()
    }

    fun createEvent(title: String, description: String, hobbyId: Long?, start: Long, end: Long, color: String) {
        viewModelScope.launch {
            eventRepository.insertEvent(StudyEvent(title = title, description = description, hobbyId = hobbyId, startTime = start, endTime = end, color = color))
            showMsg("Event created! 📅")
        }
    }

    fun markComplete(event: StudyEvent) {
        viewModelScope.launch {
            eventRepository.updateEvent(event.copy(status = EventStatus.COMPLETED))
            showMsg("Marked as done! ✅")
        }
    }

    fun deleteEvent(event: StudyEvent) {
        viewModelScope.launch { eventRepository.deleteEvent(event); showMsg("Event deleted") }
    }

    private fun showMsg(msg: String) {
        _uiState.update { it.copy(successMessage = msg) }
        viewModelScope.launch { kotlinx.coroutines.delay(3000); _uiState.update { it.copy(successMessage = null) } }
    }
}
