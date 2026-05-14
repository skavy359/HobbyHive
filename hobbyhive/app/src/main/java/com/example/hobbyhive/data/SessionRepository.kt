package com.example.hobbyhive.data

import com.example.hobbyhive.model.Session
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════
// Session Repository — Clean API for ViewModels
// ═══════════════════════════════════════════════════

open class SessionRepository(protected val sessionDao: SessionDao) {

    open fun getAllSessions(): Flow<List<Session>> = sessionDao.getAllSessions()

    open fun getSessionsForHobby(hobbyId: Long): Flow<List<Session>> =
        sessionDao.getSessionsForHobby(hobbyId)

    open fun getSessionsByDateRange(start: Long, end: Long): Flow<List<Session>> =
        sessionDao.getSessionsByDateRange(start, end)

    open suspend fun getSessionById(id: Long): Session? = sessionDao.getSessionById(id)

    open fun getTotalMinutes(): Flow<Int?> = sessionDao.getTotalMinutes()

    open fun getTotalMinutesForHobby(hobbyId: Long): Flow<Int?> =
        sessionDao.getTotalMinutesForHobby(hobbyId)

    open fun getSessionCount(): Flow<Int> = sessionDao.getSessionCount()

    open fun getSessionCountForHobby(hobbyId: Long): Flow<Int> =
        sessionDao.getSessionCountForHobby(hobbyId)

    open suspend fun getSessionCountForDate(dayStart: Long, dayEnd: Long): Int =
        sessionDao.getSessionCountForDate(dayStart, dayEnd)

    open suspend fun getDailyMinutes(dayStart: Long, dayEnd: Long): Int =
        sessionDao.getDailyMinutes(dayStart, dayEnd)

    open fun getWeeklyMinutes(weekStart: Long, weekEnd: Long): Flow<Int> =
        sessionDao.getWeeklyMinutes(weekStart, weekEnd)

    open fun getRecentSessions(limit: Int = 5): Flow<List<Session>> =
        sessionDao.getRecentSessions(limit)

    open fun getTotalMinutesForRange(start: Long, end: Long): Flow<Int> =
        sessionDao.getTotalMinutesForRange(start, end)

    open suspend fun insertSession(session: Session): Long = sessionDao.insert(session)

    open suspend fun updateSession(session: Session) = sessionDao.update(session)

    open suspend fun deleteSession(session: Session) = sessionDao.delete(session)

    open suspend fun deleteSessionById(id: Long) = sessionDao.deleteById(id)
}
