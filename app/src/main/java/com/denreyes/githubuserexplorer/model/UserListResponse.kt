package com.denreyes.githubuserexplorer.model


/**
 * Represents the response from the GitHub User Search API.
 *
 * This response is typically returned from:
 * `GET https://api.github.com/search/users?q=query`
 *
 * @property items A list of [User] objects representing GitHub users returned from the search query.
 */
data class UserListResponse(
    val items: List<User>
)