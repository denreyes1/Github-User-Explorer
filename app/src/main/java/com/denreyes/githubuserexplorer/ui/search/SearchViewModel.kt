package com.denreyes.githubuserexplorer.ui.search

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denreyes.githubuserexplorer.network.GithubRepository
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

    // Holds the current UI state of the search screen
    val searchUIState = MutableStateFlow(SearchUIState())

    // Manages the current search query
    private val _searchQuery = MutableStateFlow(TextFieldValue(""))
    val searchQuery: StateFlow<TextFieldValue> = _searchQuery

    /**
     * Updates the search query.
     *
     * @param newQuery The new query entered by the user.
     */
    fun onSearchQueryChange(newQuery: TextFieldValue) {
        _searchQuery.value = newQuery
    }

    /**
     * Initiates a search for GitHub users based on the query.
     *
     * @param query The search query term.
     */
    fun searchUser(query: String) {
        viewModelScope.launch {
            searchUIState.update { it.copy(isLoading = true, error = null, users = emptyList()) }
            val result = repository.searchUser(query)
            result.onSuccess { users ->
                searchUIState.update { it.copy(isLoading = false, users = users) }
            }.onFailure { exception ->
                searchUIState.update { it.copy(isLoading = false, error = exception.message ?: "Unknown error") }
            }
        }
    }

    /**
     * Clears the list of users in the UI state.
     */
    fun clearUsers() {
        searchUIState.update { it.copy(users = emptyList(), error = null, isLoading = false) }
    }
}