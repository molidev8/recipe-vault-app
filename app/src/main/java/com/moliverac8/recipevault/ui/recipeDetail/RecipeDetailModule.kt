package com.moliverac8.recipevault.ui.recipeDetail

import com.moliverac8.data.RecipesRepository
import com.moliverac8.usecases.GetAllRecipes
import com.moliverac8.usecases.GetRecipeByID
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RecipeDetailModule {

    @Provides
    fun getRecipeById(recipesRepository: RecipesRepository): GetRecipeByID =
        GetRecipeByID(recipesRepository)

}