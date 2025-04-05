package com.denreyes.githubuserexplorer.network

import com.denreyes.githubuserexplorer.model.UserSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {

    @GET("search/users") // add q
    suspend fun searchUser(
        @Query("q") query: String
    ): UserSearchResponse
}