package com.denreyes.githubuserexplorer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denreyes.githubuserexplorer.network.GithubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FollowerViewModel() : ViewModel() {
    val followerUIState = MutableStateFlow(FollowerUIState())
    private val repository = GithubRepository()

    fun fetchFollowers(id: Int) {
        viewModelScope.launch {
            followerUIState.update { it.copy(isLoading = true, error = null, users = emptyList()) }
            val result = repository.getUserFollowers(id)
            result.onSuccess { users ->
                followerUIState.update {
                    it.copy(isLoading = false, users = users)
                }
            }.onFailure { exception ->
                followerUIState.update {
                    it.copy(isLoading = false, error = exception.message ?: "Unknown error")
                }
            }
        }
    }
}
