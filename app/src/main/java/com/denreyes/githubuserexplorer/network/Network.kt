package com.denreyes.githubuserexplorer.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object that provides a configured Retrofit instance and the GitHub API service.
 */
object ApiService {

    private const val BASE_URL = "https://api.github.com/"

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: GithubApi = getRetrofit().create(GithubApi::class.java)
}
