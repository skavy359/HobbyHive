package com.example.hobbyhive.data

import com.example.hobbyhive.model.Goal
import com.example.hobbyhive.model.GoalStatus
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════
// Goal Repository — Clean API for ViewModels
// ═══════════════════════════════════════════════════

class GoalRepository(private val goalDao: GoalDao) {

    fun getAllGoals(): Flow<List<Goal>> = goalDao.getAllGoals()

    fun getGoalsForHobby(hobbyId: Long): Flow<List<Goal>> =
        goalDao.getGoalsForHobby(hobbyId)

    fun getGoalsByStatus(status: GoalStatus): Flow<List<Goal>> =
        goalDao.getGoalsByStatus(status)

    suspend fun getGoalById(id: Long): Goal? = goalDao.getGoalById(id)

    fun getGoalByIdFlow(id: Long): Flow<Goal?> = goalDao.getGoalByIdFlow(id)

    fun getGoalCountByStatus(status: GoalStatus): Flow<Int> =
        goalDao.getGoalCountByStatus(status)

    fun getTotalGoalCount(): Flow<Int> = goalDao.getTotalGoalCount()

    suspend fun insertGoal(goal: Goal): Long = goalDao.insert(goal)

    suspend fun updateGoal(goal: Goal) = goalDao.update(
        goal.copy(updatedAt = System.currentTimeMillis())
    )

    suspend fun deleteGoal(goal: Goal) = goalDao.delete(goal)

    suspend fun deleteGoalById(id: Long) = goalDao.deleteById(id)
}
