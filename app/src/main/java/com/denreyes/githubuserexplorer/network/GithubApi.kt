package com.denreyes.githubuserexplorer.network

import com.denreyes.githubuserexplorer.model.UserDetails
import com.denreyes.githubuserexplorer.model.UserSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface that defines the GitHub API endpoints used in the app.
 */
interface GithubApi {

    /**
     * Searches for GitHub users matching the provided query.
     *
     * Example endpoint: `GET https://api.github.com/search/users?q=denreyes`
     *
     * @param query The search query used to find GitHub users.
     */
    @GET("search/users")
    suspend fun searchUser(
        @Query("q") query: String
    ): UserSearchResponse

    /**
     * Fetches details of a specific GitHub user.
     *
     * Example: GET https://api.github.com/users/denreyes
     *
     * @param id GitHub user ID.
     */
    @GET("user/{id}")
    suspend fun getUserDetails(
        @Path("id") id: Int
    ): UserDetails
}
