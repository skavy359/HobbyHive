package com.example.hobbyhive.data

import com.example.hobbyhive.model.Goal
import com.example.hobbyhive.model.GoalStatus
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════
// Goal Repository — Clean API for ViewModels
// ═══════════════════════════════════════════════════

open class GoalRepository(protected val goalDao: GoalDao) {

    open fun getAllGoals(): Flow<List<Goal>> = goalDao.getAllGoals()

    open fun getGoalsForHobby(hobbyId: Long): Flow<List<Goal>> =
        goalDao.getGoalsForHobby(hobbyId)

    open fun getGoalsByStatus(status: GoalStatus): Flow<List<Goal>> =
        goalDao.getGoalsByStatus(status)

    open suspend fun getGoalById(id: Long): Goal? = goalDao.getGoalById(id)

    open fun getGoalByIdFlow(id: Long): Flow<Goal?> = goalDao.getGoalByIdFlow(id)

    open fun getGoalCountByStatus(status: GoalStatus): Flow<Int> =
        goalDao.getGoalCountByStatus(status)

    open fun getTotalGoalCount(): Flow<Int> = goalDao.getTotalGoalCount()

    open suspend fun insertGoal(goal: Goal): Long = goalDao.insert(goal)

    open suspend fun updateGoal(goal: Goal) = goalDao.update(
        goal.copy(updatedAt = System.currentTimeMillis())
    )

    open suspend fun deleteGoal(goal: Goal) = goalDao.delete(goal)

    open suspend fun deleteGoalById(id: Long) = goalDao.deleteById(id)
}
