package com.example.hobbyhive.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyhive.data.HobbyHiveDatabase
import com.example.hobbyhive.data.UserPreferencesRepository
import com.example.hobbyhive.data.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════
// AuthViewModel — Login, Register, Session management
// ═══════════════════════════════════════════════════

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val database = HobbyHiveDatabase.getDatabase(application)
    val userPreferencesRepository = UserPreferencesRepository(application)
    // Appwrite replaces the local Room UserRepository for authentication
    val appwriteAuthRepository = com.example.hobbyhive.appwrite.repository.AppwriteAuthRepository()

    private val _loginState = MutableStateFlow(AuthUiState())
    val loginState: StateFlow<AuthUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(AuthUiState())
    val registerState: StateFlow<AuthUiState> = _registerState.asStateFlow()

    fun login(email: String, password: String, keepLoggedIn: Boolean) {
        viewModelScope.launch {
            _loginState.value = AuthUiState(isLoading = true)

            val result = appwriteAuthRepository.login(email, password)
            result.fold(
                onSuccess = { user ->
                    // Save the REAL Appwrite user ID so repositories can query by userId
                    userPreferencesRepository.saveAppwriteUserId(user.id)
                    userPreferencesRepository.saveAppwriteSession("active_session")
                    // Keep dummy legacy token so UI doesn't break if it reads AUTH_TOKEN
                    val token = "${user.id}_${System.currentTimeMillis()}"
                    userPreferencesRepository.saveAuthToken(token, user.id, keepLoggedIn)
                    _loginState.value = AuthUiState(isSuccess = true)
                },
                onFailure = { error ->
                    _loginState.value = AuthUiState(errorMessage = error.message)
                }
            )
        }
    }

    fun register(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = AuthUiState(isLoading = true)

            val result = appwriteAuthRepository.signUp(fullName, email, password)
            result.fold(
                onSuccess = { user ->
                    // Save the REAL Appwrite user ID so repositories can query by userId
                    userPreferencesRepository.saveAppwriteUserId(user.id)
                    userPreferencesRepository.saveAppwriteSession("active_session")
                    _registerState.value = AuthUiState(isSuccess = true)
                },
                onFailure = { error ->
                    _registerState.value = AuthUiState(errorMessage = error.message)
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            appwriteAuthRepository.logout()
            userPreferencesRepository.clearAuth()
        }
    }

    fun clearLoginError() {
        _loginState.value = _loginState.value.copy(errorMessage = null)
    }

    fun clearRegisterError() {
        _registerState.value = _registerState.value.copy(errorMessage = null)
    }

    fun resetLoginState() {
        _loginState.value = AuthUiState()
    }

    fun resetRegisterState() {
        _registerState.value = AuthUiState()
    }
}
