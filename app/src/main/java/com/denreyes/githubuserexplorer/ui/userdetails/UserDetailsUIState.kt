package com.denreyes.githubuserexplorer.ui.userdetails

import com.denreyes.githubuserexplorer.model.UserDetails

/**
 * Data class representing the UI state for the Search user list.
 *
 * @param isLoading Boolean flag indicating if users are being loaded.
 * @param user List of searched users.
 * @param error Error message if loading fails.
 */
data class UserDetailsUIState (
    val isLoading: Boolean = false,
    val user: UserDetails? = null,
    val error: String? = null
)