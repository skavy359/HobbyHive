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
    private val userPrefs = UserPreferencesRepository(application)
    
    // Appwrite Repositories
    private val communityRepository = com.example.hobbyhive.appwrite.repository.AppwriteCommunityRepository(
        database.communityDao(),
        userPrefs
    )
    private val realtimeRepository = com.example.hobbyhive.appwrite.repository.AppwriteRealtimeRepository()
    
    private val userRepository = UserRepository(database.userDao()) // Keep for getting current user's name locally

    val posts: StateFlow<List<ForumPost>> = communityRepository.getAllPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groups: StateFlow<List<HobbyGroup>> = communityRepository.getAllGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var realtimeJob: kotlinx.coroutines.Job? = null

    init {
        viewModelScope.launch {
            // Initial sync from Appwrite
            communityRepository.fetchAndSyncPosts()
            communityRepository.fetchAndSyncGroups()
        }

        // Start listening to live post updates
        startRealtimeSubscription()
    }

    private fun startRealtimeSubscription() {
        realtimeJob = viewModelScope.launch {
            realtimeRepository.subscribeToForumPosts().collect { event ->
                // The underlying Room database is our cache.
                // Instead of manually parsing the RealtimeEvent and updating the UI state here,
                // we can just re-trigger a fetchAndSync() when any event occurs.
                // This keeps the offline-first architecture clean.
                communityRepository.fetchAndSyncPosts()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        realtimeJob?.cancel() // Always clean up WebSocket
    }

    fun upvotePost(post: ForumPost) {
        viewModelScope.launch {
            communityRepository.upvotePost(post)
        }
    }

    fun toggleGroupJoin(group: HobbyGroup) {
        viewModelScope.launch {
            communityRepository.toggleGroupJoinStatus(group)
        }
    }

    fun createPost(title: String, content: String, category: String) {
        viewModelScope.launch {
            val userId = userPrefs.userId.first() ?: return@launch
            val authorName = userRepository.getUserById(userId)?.fullName ?: "Anonymous"
            communityRepository.createPost(userId.toString(), authorName, title, content, category)
        }
    }

    fun getPost(postId: Long) = database.communityDao().getPostById(postId)

    fun getComments(postId: Long) = communityRepository.getCommentsForPost(postId)

    fun addComment(postId: Long, content: String) {
        viewModelScope.launch {
            val userId = userPrefs.userId.first() ?: return@launch
            val authorName = userRepository.getUserById(userId)?.fullName ?: "Anonymous"
            
            // Fetch post to get its Appwrite ID
            val post = database.communityDao().getPostById(postId).first()
            val postDocumentId = post?.appwriteId ?: ""
            
            communityRepository.addComment(userId.toString(), authorName, postDocumentId, postId, content)
        }
    }
}
