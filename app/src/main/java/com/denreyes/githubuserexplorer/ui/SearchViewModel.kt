package com.denreyes.githubuserexplorer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denreyes.githubuserexplorer.network.GithubRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class SearchViewModel : ViewModel() {
    val searchUIState = MutableStateFlow(SearchUIState())
    private val repository = GithubRepository()

    fun searchUser(query: String) {
        viewModelScope.async {
            searchUIState.update { it.copy(isLoading = true, error = null, users = emptyList()) }
            val result = repository.searchUser(query)
            result.onSuccess { users ->
                searchUIState.update {
                    it.copy(isLoading = false, users = users)
                }
            }.onFailure { exception ->
                searchUIState.update {
                    it.copy(isLoading = false, error = exception.message ?: "Unknown error")
                }
            }
        }
    }

    fun clearUsers() {
        searchUIState.update {
            it.copy(
                users = emptyList(),
                error = null,
                isLoading = false
            )
        }
    }

}