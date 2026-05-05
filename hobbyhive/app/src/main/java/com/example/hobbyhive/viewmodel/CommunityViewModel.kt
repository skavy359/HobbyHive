package com.example.hobbyhive.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyhive.data.CommunityRepository
import com.example.hobbyhive.data.HobbyHiveDatabase
import com.example.hobbyhive.data.UserPreferencesRepository
import com.example.hobbyhive.data.UserRepository
import com.example.hobbyhive.model.ForumComment
import com.example.hobbyhive.model.ForumPost
import com.example.hobbyhive.model.HobbyGroup
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CommunityViewModel(application: Application) : AndroidViewModel(application) {
    private val database = HobbyHiveDatabase.getDatabase(application)
    private val repository = CommunityRepository(database.communityDao())
    private val userPrefs = UserPreferencesRepository(application)
    private val userRepository = UserRepository(database.userDao())

    val posts: StateFlow<List<ForumPost>> = repository.getAllPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groups: StateFlow<List<HobbyGroup>> = repository.getAllGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            if (repository.getAllGroups().first().isEmpty()) {
                repository.seedInitialGroups()
            }
        }
    }

    fun upvotePost(post: ForumPost) {
        viewModelScope.launch {
            repository.upvotePost(post)
        }
    }

    fun toggleGroupJoin(group: HobbyGroup) {
        viewModelScope.launch {
            repository.toggleGroupJoinStatus(group)
        }
    }

    fun createPost(title: String, content: String, category: String) {
        viewModelScope.launch {
            val userId = userPrefs.userId.first()
            val authorName = userId?.let { userRepository.getUserById(it)?.fullName } ?: "Anonymous"
            repository.insertPost(
                ForumPost(title = title, content = content, authorName = authorName, category = category)
            )
        }
    }

    fun getPost(postId: Long) = repository.getPostById(postId)

    fun getComments(postId: Long) = repository.getCommentsForPost(postId)

    fun addComment(postId: Long, content: String) {
        viewModelScope.launch {
            val userId = userPrefs.userId.first()
            val authorName = userId?.let { userRepository.getUserById(it)?.fullName } ?: "Anonymous"
            repository.addComment(ForumComment(postId = postId, authorName = authorName, content = content))
        }
    }
}
