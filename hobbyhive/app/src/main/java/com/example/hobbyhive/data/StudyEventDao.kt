package com.example.hobbyhive.data

import androidx.room.*
import com.example.hobbyhive.model.EventStatus
import com.example.hobbyhive.model.StudyEvent
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════
// StudyEvent DAO — Calendar event queries
// ═══════════════════════════════════════════════════

@Dao
interface StudyEventDao {

    @Query("SELECT * FROM study_events ORDER BY startTime ASC")
    fun getAllEvents(): Flow<List<StudyEvent>>

    @Query("SELECT * FROM study_events WHERE startTime BETWEEN :monthStart AND :monthEnd ORDER BY startTime ASC")
    fun getEventsForMonth(monthStart: Long, monthEnd: Long): Flow<List<StudyEvent>>

    @Query("SELECT * FROM study_events WHERE startTime BETWEEN :dayStart AND :dayEnd ORDER BY startTime ASC")
    fun getEventsForDate(dayStart: Long, dayEnd: Long): Flow<List<StudyEvent>>

    @Query("SELECT * FROM study_events WHERE id = :eventId")
    suspend fun getEventById(eventId: Long): StudyEvent?

    @Query("SELECT COUNT(*) FROM study_events WHERE status = :status")
    fun getCountByStatus(status: EventStatus): Flow<Int>

    @Query("SELECT * FROM study_events WHERE status = 'PLANNED' AND endTime < :now")
    suspend fun getOverdueEvents(now: Long): List<StudyEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: StudyEvent): Long

    @Update
    suspend fun update(event: StudyEvent)

    @Delete
    suspend fun delete(event: StudyEvent)

    @Query("DELETE FROM study_events WHERE id = :eventId")
    suspend fun deleteById(eventId: Long)
}
