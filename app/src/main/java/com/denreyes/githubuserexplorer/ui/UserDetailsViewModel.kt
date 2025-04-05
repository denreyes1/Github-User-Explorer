package com.denreyes.githubuserexplorer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denreyes.githubuserexplorer.network.GithubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserDetailsViewModel() : ViewModel() {
    val detailsUIState = MutableStateFlow(UserDetailsUIState())
    private val repository = GithubRepository()

    fun fetchUserDetails(id: Int) {
        viewModelScope.launch {
            detailsUIState.update { it.copy(isLoading = true, error = null, user = null) }
            val result = repository.getUserDetails(id)
            result
                .onSuccess { user ->
                    detailsUIState.update {
                        it.copy(isLoading = false, user = user)
                    }
                }
                .onFailure { exception ->
                    detailsUIState.update {
                        it.copy(isLoading = false, error = exception.message ?: "Unknown error")
                    }
                }
        }
    }
}
