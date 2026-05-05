package com.example.hobbyhive.data

import com.example.hobbyhive.model.EventStatus
import com.example.hobbyhive.model.StudyEvent
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════
// StudyEvent Repository — Clean API for ViewModels
// ═══════════════════════════════════════════════════

class StudyEventRepository(private val studyEventDao: StudyEventDao) {

    fun getAllEvents(): Flow<List<StudyEvent>> = studyEventDao.getAllEvents()

    fun getEventsForMonth(monthStart: Long, monthEnd: Long): Flow<List<StudyEvent>> =
        studyEventDao.getEventsForMonth(monthStart, monthEnd)

    fun getEventsForDate(dayStart: Long, dayEnd: Long): Flow<List<StudyEvent>> =
        studyEventDao.getEventsForDate(dayStart, dayEnd)

    suspend fun getEventById(id: Long): StudyEvent? = studyEventDao.getEventById(id)

    fun getCountByStatus(status: EventStatus): Flow<Int> =
        studyEventDao.getCountByStatus(status)

    suspend fun getOverdueEvents(now: Long): List<StudyEvent> =
        studyEventDao.getOverdueEvents(now)

    suspend fun insertEvent(event: StudyEvent): Long = studyEventDao.insert(event)

    suspend fun updateEvent(event: StudyEvent) = studyEventDao.update(event)

    suspend fun deleteEvent(event: StudyEvent) = studyEventDao.delete(event)

    suspend fun deleteEventById(id: Long) = studyEventDao.deleteById(id)

    suspend fun markOverdueAsMissed() {
        val now = System.currentTimeMillis()
        val overdue = studyEventDao.getOverdueEvents(now)
        overdue.forEach { event ->
            studyEventDao.update(event.copy(status = EventStatus.MISSED))
        }
    }
}
