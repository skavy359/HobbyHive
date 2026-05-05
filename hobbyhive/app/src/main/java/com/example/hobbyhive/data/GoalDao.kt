package com.example.hobbyhive.data

import androidx.room.*
import com.example.hobbyhive.model.Goal
import com.example.hobbyhive.model.GoalStatus
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════
// Goal DAO — Goal tracking queries
// ═══════════════════════════════════════════════════

@Dao
interface GoalDao {

    @Query("SELECT * FROM goals ORDER BY updatedAt DESC")
    fun getAllGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE hobbyId = :hobbyId ORDER BY updatedAt DESC")
    fun getGoalsForHobby(hobbyId: Long): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE status = :status ORDER BY updatedAt DESC")
    fun getGoalsByStatus(status: GoalStatus): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE id = :goalId")
    suspend fun getGoalById(goalId: Long): Goal?

    @Query("SELECT * FROM goals WHERE id = :goalId")
    fun getGoalByIdFlow(goalId: Long): Flow<Goal?>

    @Query("SELECT COUNT(*) FROM goals WHERE status = :status")
    fun getGoalCountByStatus(status: GoalStatus): Flow<Int>

    @Query("SELECT COUNT(*) FROM goals")
    fun getTotalGoalCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: Goal): Long

    @Update
    suspend fun update(goal: Goal)

    @Delete
    suspend fun delete(goal: Goal)

    @Query("DELETE FROM goals WHERE id = :goalId")
    suspend fun deleteById(goalId: Long)
}
