package com.denreyes.githubuserexplorer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denreyes.githubuserexplorer.network.GithubRepository
import kotlinx.coroutines.async

class SearchViewModel : ViewModel() {
    private val repository = GithubRepository()

    init {
        searchUser("denreyes")
    }

    private fun searchUser(query: String) {
        viewModelScope.async {
            val result = repository.searchUser(query)
            result.onSuccess { podcasts ->
                // Handle Success
            }.onFailure { exception ->
                // Handle Error
            }
        }
    }
}