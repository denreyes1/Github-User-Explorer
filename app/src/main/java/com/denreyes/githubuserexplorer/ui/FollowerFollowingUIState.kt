package com.denreyes.githubuserexplorer.ui

import com.denreyes.githubuserexplorer.model.User

/**
 * Data class representing the UI state for the Search user list.
 *
 * @param isLoading Boolean flag indicating if users are being loaded.
 * @param users List of followers/following.
 * @param error Error message if loading fails.
 */
data class FollowerUIState (
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val error: String? = null
)

data class FollowingUIState (
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val error: String? = null
)
