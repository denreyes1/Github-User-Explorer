package com.denreyes.githubuserexplorer

import android.app.Application
import com.denreyes.githubuserexplorer.di.appModules
import org.koin.core.context.startKoin

class GithubApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModules)
        }
    }
}