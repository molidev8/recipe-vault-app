package com.moliverac8.recipevault.ui.recipeList

import com.moliverac8.data.RecipesRepository
import com.moliverac8.usecases.GetAllRecipes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RecipeListModule {

    @Provides
    fun getAllRecipes(recipesRepository: RecipesRepository): GetAllRecipes =
        GetAllRecipes(recipesRepository)
}