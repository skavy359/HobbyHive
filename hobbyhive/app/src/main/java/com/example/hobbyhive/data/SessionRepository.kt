package com.example.hobbyhive.data

import com.example.hobbyhive.model.Session
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════
// Session Repository — Clean API for ViewModels
// ═══════════════════════════════════════════════════

class SessionRepository(private val sessionDao: SessionDao) {

    fun getAllSessions(): Flow<List<Session>> = sessionDao.getAllSessions()

    fun getSessionsForHobby(hobbyId: Long): Flow<List<Session>> =
        sessionDao.getSessionsForHobby(hobbyId)

    fun getSessionsByDateRange(start: Long, end: Long): Flow<List<Session>> =
        sessionDao.getSessionsByDateRange(start, end)

    suspend fun getSessionById(id: Long): Session? = sessionDao.getSessionById(id)

    fun getTotalMinutes(): Flow<Int> = sessionDao.getTotalMinutes()

    fun getTotalMinutesForHobby(hobbyId: Long): Flow<Int> =
        sessionDao.getTotalMinutesForHobby(hobbyId)

    fun getSessionCount(): Flow<Int> = sessionDao.getSessionCount()

    fun getSessionCountForHobby(hobbyId: Long): Flow<Int> =
        sessionDao.getSessionCountForHobby(hobbyId)

    suspend fun getSessionCountForDate(dayStart: Long, dayEnd: Long): Int =
        sessionDao.getSessionCountForDate(dayStart, dayEnd)

    suspend fun getDailyMinutes(dayStart: Long, dayEnd: Long): Int =
        sessionDao.getDailyMinutes(dayStart, dayEnd)

    fun getWeeklyMinutes(weekStart: Long, weekEnd: Long): Flow<Int> =
        sessionDao.getWeeklyMinutes(weekStart, weekEnd)

    fun getRecentSessions(limit: Int = 5): Flow<List<Session>> =
        sessionDao.getRecentSessions(limit)

    fun getTotalMinutesForRange(start: Long, end: Long): Flow<Int> =
        sessionDao.getTotalMinutesForRange(start, end)

    suspend fun insertSession(session: Session): Long = sessionDao.insert(session)

    suspend fun updateSession(session: Session) = sessionDao.update(session)

    suspend fun deleteSession(session: Session) = sessionDao.delete(session)

    suspend fun deleteSessionById(id: Long) = sessionDao.deleteById(id)
}
