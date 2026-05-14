package com.example.hobbyhive.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyhive.data.HobbyHiveDatabase
import com.example.hobbyhive.data.HobbyRepository
import com.example.hobbyhive.model.Hobby
import com.example.hobbyhive.model.HobbyStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HobbyUiState(
    val hobbies: List<Hobby> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedTab: Int = 0,
    val isGridView: Boolean = false
)

class HobbyViewModel(application: Application) : AndroidViewModel(application) {
    private val database = HobbyHiveDatabase.getDatabase(application)
    val userPrefs = com.example.hobbyhive.data.UserPreferencesRepository(application)
    // Appwrite replaces the local Room HobbyRepository
    val hobbyRepository = com.example.hobbyhive.appwrite.repository.AppwriteHobbyRepository(
        database.hobbyDao(),
        userPrefs
    )

    private val _selectedTab = MutableStateFlow(0)
    private val _searchQuery = MutableStateFlow("")
    private val _isGridView = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            val userId = userPrefs.userId.first()
            if (userId != null) {
                // Keep Room cache fresh from Appwrite Cloud
                hobbyRepository.fetchAndSync(userId.toString())
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HobbyUiState> = combine(
        _selectedTab, _searchQuery, _isGridView
    ) { tab, query, grid -> Triple(tab, query, grid) }
        .flatMapLatest { (tab, query, grid) ->
            val hobbiesFlow = if (query.isNotBlank()) {
                hobbyRepository.searchHobbies(query)
            } else {
                when (tab) {
                    // TODO: Replace with Appwrite queries if filtering on the cloud is desired.
                    // For now, since we sync all hobbies to Room, we can still use the Room queries.
                    1 -> database.hobbyDao().getHobbiesByStatus(HobbyStatus.ACTIVE)
                    2 -> database.hobbyDao().getHobbiesByStatus(HobbyStatus.COMPLETED)
                    3 -> database.hobbyDao().getHobbiesByStatus(HobbyStatus.ARCHIVED)
                    else -> hobbyRepository.getAllHobbies()
                }
            }
            hobbiesFlow.map { hobbies ->
                HobbyUiState(hobbies = hobbies, isLoading = false, searchQuery = query, selectedTab = tab, isGridView = grid)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HobbyUiState())

    fun setTab(index: Int) { _selectedTab.value = index }
    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun toggleGridView() { _isGridView.value = !_isGridView.value }

    fun deleteHobby(hobby: Hobby) {
        viewModelScope.launch {
            // Document ID is temporarily stored in imageUri, or empty if it was created offline only
            hobbyRepository.deleteHobby(hobby.imageUri ?: "", hobby)
        }
    }
}
