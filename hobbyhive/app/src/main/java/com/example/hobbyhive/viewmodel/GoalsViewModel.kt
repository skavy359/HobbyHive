package com.example.hobbyhive.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyhive.data.GoalRepository
import com.example.hobbyhive.data.HobbyHiveDatabase
import com.example.hobbyhive.data.HobbyRepository
import com.example.hobbyhive.model.Goal
import com.example.hobbyhive.model.GoalStatus
import com.example.hobbyhive.model.Hobby
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════
// GoalsViewModel — Goal management
// ═══════════════════════════════════════════════════

data class GoalsUiState(
    val goals: List<GoalWithHobby> = emptyList(),
    val hobbies: List<Hobby> = emptyList(),
    val activeCount: Int = 0,
    val completedCount: Int = 0,
    val atRiskCount: Int = 0,
    val isLoading: Boolean = true,
    val successMessage: String? = null
)

data class GoalWithHobby(
    val goal: Goal,
    val hobbyName: String,
    val hobbyEmoji: String
)

class GoalsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = HobbyHiveDatabase.getDatabase(application)
    private val userPrefs = com.example.hobbyhive.data.UserPreferencesRepository(application)
    
    val goalRepository = com.example.hobbyhive.appwrite.repository.AppwriteGoalRepository(
        database.goalDao(),
        userPrefs
    )
    val hobbyRepository = com.example.hobbyhive.appwrite.repository.AppwriteHobbyRepository(
        database.hobbyDao(),
        userPrefs
    )

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    init {
        loadGoals()
        loadHobbies()
        loadCounts()
        
        // Initial sync
        viewModelScope.launch {
            val userId = userPrefs.appwriteUserId.first()
            if (!userId.isNullOrEmpty()) {
                goalRepository.fetchAndSync(userId)
            }
        }
    }

    private fun loadGoals() {
        viewModelScope.launch {
            goalRepository.getAllGoals().collect { goals ->
                val withHobbies = goals.map { goal ->
                    val hobby = hobbyRepository.getHobbyByIdOnce(goal.hobbyId)
                    GoalWithHobby(goal, hobby?.name ?: "Unknown", hobby?.category?.emoji ?: "🌟")
                }
                _uiState.update { it.copy(goals = withHobbies, isLoading = false) }
            }
        }
    }

    private fun loadHobbies() {
        viewModelScope.launch {
            hobbyRepository.getAllHobbies().collect { hobbies ->
                _uiState.update { it.copy(hobbies = hobbies) }
            }
        }
    }

    private fun loadCounts() {
        viewModelScope.launch {
            combine(
                goalRepository.getGoalCountByStatus(GoalStatus.IN_PROGRESS),
                goalRepository.getGoalCountByStatus(GoalStatus.COMPLETED),
                goalRepository.getGoalCountByStatus(GoalStatus.AT_RISK)
            ) { active, completed, atRisk -> Triple(active, completed, atRisk) }
                .collect { (active, completed, atRisk) ->
                    _uiState.update {
                        it.copy(activeCount = active, completedCount = completed, atRiskCount = atRisk)
                    }
                }
        }
    }

    fun createGoal(
        hobbyId: Long, title: String, description: String,
        targetValue: Int, unit: String, deadline: Long?
    ) {
        viewModelScope.launch {
            // Find hobby documentId for linking
            val hobby = database.hobbyDao().getHobbyById(hobbyId).first()
            val hobbyDocId = hobby?.appwriteId ?: ""
            val userId = userPrefs.appwriteUserId.first() ?: ""

            goalRepository.createGoal(
                userId = userId,
                hobbyDocumentId = hobbyDocId,
                goal = Goal(
                    hobbyId = hobbyId, title = title, description = description,
                    targetValue = targetValue, unit = unit, deadline = deadline
                )
            )
            showSuccess("Goal created! 🎯")
        }
    }

    fun updateGoalProgress(goal: Goal, newValue: Int) {
        viewModelScope.launch {
            val status = when {
                newValue >= goal.targetValue -> GoalStatus.COMPLETED
                goal.deadline != null && goal.deadline < System.currentTimeMillis() && newValue < goal.targetValue -> GoalStatus.FAILED
                else -> goal.status
            }
            val updated = goal.copy(currentValue = newValue, status = status, updatedAt = System.currentTimeMillis())
            goalRepository.updateGoal(goal.appwriteId ?: "", updated)
            if (status == GoalStatus.COMPLETED) showSuccess("Goal completed! 🎉")
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            goalRepository.deleteGoal(goal.appwriteId ?: "", goal)
            showSuccess("Goal deleted")
        }
    }

    private fun showSuccess(message: String) {
        _uiState.update { it.copy(successMessage = message) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            _uiState.update { it.copy(successMessage = null) }
        }
    }
}
