package com.denreyes.githubuserexplorer.network

import com.denreyes.githubuserexplorer.model.User
import com.denreyes.githubuserexplorer.model.UserDetails

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

    /**
     * Fetches detailed information of a GitHub user by ID.
     *
     * @param id The unique ID of the GitHub user.
     * @return A [Result] containing [UserDetails] on success or an exception on failure.
     */
    suspend fun getUserDetails(id: Int): Result<UserDetails> {
        return try {
            Result.success(service.getUserDetails(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetches followers of a GitHub user by ID.
     *
     * @param id The unique ID of the GitHub user.
     * @return A [Result] containing a list of [User] on success or an exception on failure.
     */
    suspend fun getUserFollowers(id: Int): Result<List<User>> {
        return try {
            Result.success(service.getUserFollowers(id).items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetches following of a GitHub user by ID.
     *
     * @param id The unique ID of the GitHub user.
     * @return A [Result] containing a list of [User] on success or an exception on failure.
     */
    suspend fun getUserFollowing(id: Int): Result<List<User>> {
        return try {
            Result.success(service.getUserFollowing(id).items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
