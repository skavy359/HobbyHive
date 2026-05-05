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
    val hobbyRepository = HobbyRepository(database.hobbyDao())

    private val _selectedTab = MutableStateFlow(0)
    private val _searchQuery = MutableStateFlow("")
    private val _isGridView = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HobbyUiState> = combine(
        _selectedTab, _searchQuery, _isGridView
    ) { tab, query, grid -> Triple(tab, query, grid) }
        .flatMapLatest { (tab, query, grid) ->
            val hobbiesFlow = if (query.isNotBlank()) {
                hobbyRepository.searchHobbies(query)
            } else {
                when (tab) {
                    1 -> hobbyRepository.getHobbiesByStatus(HobbyStatus.ACTIVE)
                    2 -> hobbyRepository.getHobbiesByStatus(HobbyStatus.COMPLETED)
                    3 -> hobbyRepository.getHobbiesByStatus(HobbyStatus.ARCHIVED)
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
        viewModelScope.launch { hobbyRepository.deleteHobby(hobby) }
    }
}
