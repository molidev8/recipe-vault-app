package com.moliverac8.recipevault.framework.di

import android.app.Application
import com.moliverac8.data.LocalRecipesDataSource
import com.moliverac8.data.RecipesRepository
import com.moliverac8.recipevault.framework.room.FakeRecipesDataSourceImpl
import com.moliverac8.recipevault.framework.room.LocalRecipeDatabase
import com.moliverac8.recipevault.framework.room.LocalRecipesDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Shares lifecycle with the app
class DataModule {

    /*@Provides
    @Singleton
    fun databaseProvider(app: Application): LocalRecipeDatabase =
        LocalRecipeDatabase.getInstance(app)*/

    /*@Provides
    fun localRecipesDataSourceProvider(db: LocalRecipeDatabase): LocalRecipesDataSource =
        LocalRecipesDataSourceImpl(db)*/

    @Provides
    fun fakeDataSource(): LocalRecipesDataSource = FakeRecipesDataSourceImpl()

    @Provides
    fun recipesRepositoryProvider(dataSource: LocalRecipesDataSource): RecipesRepository =
        RecipesRepository(dataSource)

}