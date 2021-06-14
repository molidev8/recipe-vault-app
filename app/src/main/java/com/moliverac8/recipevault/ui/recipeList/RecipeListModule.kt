package com.moliverac8.recipevault.ui.recipeList

import com.moliverac8.data.RecipesRepository
import com.moliverac8.usecases.DeleteRecipe
import com.moliverac8.usecases.GetAllRecipes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module to inject dependencies in the RecipeListFragment codebase attached to the
 * application component following the Singleton pattern
 */
@Module
@InstallIn(SingletonComponent::class)
class RecipeListModule {

    @Provides
    fun getAllRecipes(recipesRepository: RecipesRepository): GetAllRecipes =
        GetAllRecipes(recipesRepository)

    @Provides
    fun deleteRecipe(recipesRepository: RecipesRepository): DeleteRecipe =
        DeleteRecipe(recipesRepository)
}