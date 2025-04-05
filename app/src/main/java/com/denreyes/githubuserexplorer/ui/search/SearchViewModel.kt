package com.denreyes.githubuserexplorer.ui.search

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denreyes.githubuserexplorer.network.GithubRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for handling GitHub user search operations.
 *
 * @property repository An instance of [GithubRepository] for data operations.
 */
class SearchViewModel(
    private val repository: GithubRepository
) : ViewModel() {

    val searchUIState = MutableStateFlow(SearchUIState())

    private val _searchQuery = MutableStateFlow(TextFieldValue(""))
    val searchQuery: StateFlow<TextFieldValue> = _searchQuery

    private var searchJob: Job? = null

    fun onSearchQueryChange(newQuery: TextFieldValue) {
        _searchQuery.value = newQuery

        searchJob?.cancel() // Cancel the previous job if it's still running

        if (newQuery.text.length >= 3) {
            searchJob = viewModelScope.launch {
                delay(500) // Wait for 500ms after the last keystroke
                searchUser(newQuery.text)
            }
        } else {
            clearUsers()
        }
    }

    fun searchUser(query: String) {
        viewModelScope.launch {
            searchUIState.update { it.copy(isLoading = true, error = null, users = emptyList()) }
            val result = repository.searchUser(query)
            result.onSuccess { users ->
                searchUIState.update { it.copy(isLoading = false, users = users) }
            }.onFailure { exception ->
                searchUIState.update {
                    it.copy(isLoading = false, error = exception.message ?: "Unknown error")
                }
            }
        }
    }

    fun clearUsers() {
        searchUIState.update { it.copy(users = emptyList(), error = null, isLoading = false) }
    }

    fun refreshData() {
        val currentQuery = _searchQuery.value.text
        if (currentQuery.length >= 3) {
            searchUser(currentQuery)
        }
    }
}
