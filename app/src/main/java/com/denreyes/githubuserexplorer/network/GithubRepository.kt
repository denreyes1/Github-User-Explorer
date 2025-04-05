package com.denreyes.githubuserexplorer.network

import com.denreyes.githubuserexplorer.model.User

class GithubRepository(
    private val service: GithubApi = ApiService.service
) {

    suspend fun searchUser(query: String): Result<List<User>> {
        return try {
            Result.success(service.searchUser(query).items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}