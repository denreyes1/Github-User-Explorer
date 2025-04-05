package com.denreyes.githubuserexplorer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denreyes.githubuserexplorer.network.GithubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for managing follower and following state.
 */
class FollowerFollowingViewModel : ViewModel() {

    // State flows to track the UI state for followers and following lists
    val followerUIState = MutableStateFlow(FollowerUIState())
    val followingUIState = MutableStateFlow(FollowingUIState())

    // Repository to interact with GitHub API
    private val repository = GithubRepository()

    /**
     * Fetches the followers of a user by ID and updates the UI state.
     *
     * @param id The user ID to fetch followers for.
     */
    fun fetchFollowers(id: Int) {
        viewModelScope.launch {
            // Set loading state while fetching data
            followerUIState.update { it.copy(isLoading = true, error = null, users = emptyList()) }

            // Fetch followers from the repository
            val result = repository.getUserFollowers(id)
            result.onSuccess { users ->
                // Update state with fetched users on success
                followerUIState.update { it.copy(isLoading = false, users = users) }
            }.onFailure { exception ->
                // Update state with error on failure
                followerUIState.update { it.copy(isLoading = false, error = exception.message ?: "Unknown error") }
            }
        }
    }

    /**
     * Fetches the users that a user is following by ID and updates the UI state.
     *
     * @param id The user ID to fetch following for.
     */
    fun fetchFollowing(id: Int) {
        viewModelScope.launch {
            // Set loading state while fetching data
            followingUIState.update { it.copy(isLoading = true, error = null, users = emptyList()) }

            // Fetch following users from the repository
            val result = repository.getUserFollowing(id)
            result.onSuccess { users ->
                // Update state with fetched users on success
                followingUIState.update { it.copy(isLoading = false, users = users) }
            }.onFailure { exception ->
                // Update state with error on failure
                followingUIState.update { it.copy(isLoading = false, error = exception.message ?: "Unknown error") }
            }
        }
    }
}