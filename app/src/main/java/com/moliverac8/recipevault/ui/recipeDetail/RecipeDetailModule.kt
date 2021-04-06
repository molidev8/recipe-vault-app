package com.moliverac8.recipevault.ui.recipeDetail

import com.moliverac8.data.RecipesRepository
import com.moliverac8.usecases.GetAllRecipes
import com.moliverac8.usecases.GetRecipeByID
import com.moliverac8.usecases.SaveRecipe
import com.moliverac8.usecases.UpdateRecipe
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

    @Provides
    fun saveRecipe(recipesRepository: RecipesRepository): SaveRecipe =
        SaveRecipe(recipesRepository)

    @Provides
    fun updateRecipe(recipesRepository: RecipesRepository): UpdateRecipe =
        UpdateRecipe(recipesRepository)
}