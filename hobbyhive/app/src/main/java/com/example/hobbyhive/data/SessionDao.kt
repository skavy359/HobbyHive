package com.example.hobbyhive.data

import androidx.room.*
import com.example.hobbyhive.model.Session
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════
// Session DAO — Time tracking queries
// ═══════════════════════════════════════════════════

@Dao
interface SessionDao {

    @Query("SELECT * FROM sessions ORDER BY sessionDate DESC")
    fun getAllSessions(): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE hobbyId = :hobbyId ORDER BY sessionDate DESC")
    fun getSessionsForHobby(hobbyId: Long): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE sessionDate BETWEEN :start AND :end ORDER BY sessionDate DESC")
    fun getSessionsByDateRange(start: Long, end: Long): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): Session?

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM sessions")
    fun getTotalMinutes(): Flow<Int>

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM sessions WHERE hobbyId = :hobbyId")
    fun getTotalMinutesForHobby(hobbyId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM sessions")
    fun getSessionCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM sessions WHERE hobbyId = :hobbyId")
    fun getSessionCountForHobby(hobbyId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM sessions WHERE sessionDate BETWEEN :dayStart AND :dayEnd")
    suspend fun getSessionCountForDate(dayStart: Long, dayEnd: Long): Int

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM sessions WHERE sessionDate BETWEEN :start AND :end")
    fun getTotalMinutesForRange(start: Long, end: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM sessions WHERE sessionDate BETWEEN :dayStart AND :dayEnd")
    suspend fun getDailyMinutes(dayStart: Long, dayEnd: Long): Int

    @Query("""
        SELECT COALESCE(SUM(durationMinutes), 0) FROM sessions 
        WHERE sessionDate BETWEEN :weekStart AND :weekEnd
    """)
    fun getWeeklyMinutes(weekStart: Long, weekEnd: Long): Flow<Int>

    @Query("SELECT * FROM sessions ORDER BY sessionDate DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<Session>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: Session): Long

    @Update
    suspend fun update(session: Session)

    @Delete
    suspend fun delete(session: Session)

    @Query("DELETE FROM sessions WHERE id = :sessionId")
    suspend fun deleteById(sessionId: Long)

    @Query("DELETE FROM sessions WHERE hobbyId = :hobbyId")
    suspend fun deleteSessionsForHobby(hobbyId: Long)
}
