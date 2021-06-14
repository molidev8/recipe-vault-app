package com.moliverac8.recipevault

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

/**
 * Specifies that the app is using Hilt for dependency injection and creates a companion object
 * to access the context in any place inside the app codebase
 */
@HiltAndroidApp
class RecipeVaultApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: RecipeVaultApplication private set
    }
}