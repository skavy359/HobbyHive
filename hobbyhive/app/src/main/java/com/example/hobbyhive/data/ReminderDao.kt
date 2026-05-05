package com.example.hobbyhive.data

import androidx.room.*
import com.example.hobbyhive.model.Reminder
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════
// Reminder DAO — Notification scheduling queries
// ═══════════════════════════════════════════════════

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders WHERE hobbyId = :hobbyId ORDER BY timeInMillis ASC")
    fun getRemindersForHobby(hobbyId: Long): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders ORDER BY timeInMillis ASC")
    fun getAllReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE isEnabled = 1 ORDER BY timeInMillis ASC")
    fun getEnabledReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE id = :reminderId LIMIT 1")
    suspend fun getReminderById(reminderId: Long): Reminder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder): Long

    @Update
    suspend fun update(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)

    @Query("DELETE FROM reminders WHERE hobbyId = :hobbyId")
    suspend fun deleteRemindersForHobby(hobbyId: Long)
}
