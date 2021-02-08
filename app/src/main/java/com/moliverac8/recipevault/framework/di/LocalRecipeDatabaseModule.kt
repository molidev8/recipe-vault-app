package com.moliverac8.recipevault.framework.di

import android.content.Context
import com.moliverac8.recipevault.framework.room.LocalRecipeDatabase
import com.moliverac8.recipevault.framework.room.LocalRecipeDatabaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponentManager::class)
object LocalRecipeDatabaseModule {

    @Provides
    fun provideDao(database: LocalRecipeDatabase): LocalRecipeDatabaseDao =
        database.dao()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LocalRecipeDatabase =
        LocalRecipeDatabase.getInstance(context)
}