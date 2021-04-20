package com.moliverac8.recipevault.framework.di

import android.app.Application
import android.content.Context
import com.moliverac8.data.LocalRecipesDataSource
import com.moliverac8.data.RecipesRepository
import com.moliverac8.recipevault.framework.room.FakeRecipesDataSourceImpl
import com.moliverac8.recipevault.framework.room.LocalRecipeDatabase
import com.moliverac8.recipevault.framework.room.LocalRecipesDataSourceImpl
import com.moliverac8.recipevault.framework.workmanager.BackupUserData
import com.moliverac8.recipevault.framework.workmanager.DropboxManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Shares lifecycle with the app
class DataModule {

    //    Comentar descomentar estos dos para usarlos
    @Provides
    @Singleton
    fun databaseProvider(app: Application): LocalRecipeDatabase =
        LocalRecipeDatabase.getInstance(app)

    @Provides
    fun localRecipesDataSourceProvider(db: LocalRecipeDatabase): LocalRecipesDataSource =
        LocalRecipesDataSourceImpl(db)

    //    Comentar/descomentar para usarlo
    /*@Provides
    fun fakeDataSource(): LocalRecipesDataSource = FakeRecipesDataSourceImpl()
*/
    @Provides
    fun recipesRepositoryProvider(dataSource: LocalRecipesDataSource): RecipesRepository =
        RecipesRepository(dataSource)

    @Provides
    @Singleton
    fun dropboxManagerProvider(@ApplicationContext context: Context): DropboxManager = DropboxManager(context)

}