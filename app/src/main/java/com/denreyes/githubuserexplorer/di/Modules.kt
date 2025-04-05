package com.denreyes.githubuserexplorer.di

import com.denreyes.githubuserexplorer.network.GithubApi
import com.denreyes.githubuserexplorer.network.GithubRepository
import com.denreyes.githubuserexplorer.ui.followersfollowing.FollowerFollowingViewModel
import com.denreyes.githubuserexplorer.ui.search.SearchViewModel
import com.denreyes.githubuserexplorer.ui.userdetails.UserDetailsViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * DI module that provides dependencies for the app using Koin.
 */
val appModules = module {

    // Provides Dispatcher.IO for coroutines requiring background operations
    single { Dispatchers.IO }

    // Creates and provides a Retrofit instance configured with base URL and Gson converter
    single {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Provides an implementation of the GithubApi using the Retrofit instance
    single<GithubApi> { get<Retrofit>().create(GithubApi::class.java) }

    // Provides the GithubRepository with the injected GithubApi
    single { GithubRepository(get()) }

    // ViewModels
    viewModel { SearchViewModel(get()) }
    viewModel { FollowerFollowingViewModel(get()) }
    viewModel { UserDetailsViewModel(get()) }
}