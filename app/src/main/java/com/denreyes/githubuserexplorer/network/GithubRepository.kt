package com.denreyes.githubuserexplorer.network

import com.denreyes.githubuserexplorer.model.User

/**
 * Repository that handles data operations for GitHub user search.
 *
 * @property service An instance of [GithubApi] used to make network requests.
 */
class GithubRepository(
    private val service: GithubApi = ApiService.service
) {

    /**
     * Searches GitHub users with the given query string.
     *
     * @param query The search term entered by the user.
     * @return A [Result] containing a list of [User] on success or an exception on failure.
     */
    suspend fun searchUser(query: String): Result<List<User>> {
        return try {
            Result.success(service.searchUser(query).items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
