package com.denreyes.githubuserexplorer.ui

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denreyes.githubuserexplorer.network.GithubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    val searchUIState = MutableStateFlow(SearchUIState())
    private val repository = GithubRepository()

    private val _searchQuery = MutableStateFlow(TextFieldValue(""))
    val searchQuery: StateFlow<TextFieldValue> = _searchQuery

    fun onSearchQueryChange(newQuery: TextFieldValue) {
        _searchQuery.value = newQuery
    }

    fun searchUser(query: String) {
        viewModelScope.launch {
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