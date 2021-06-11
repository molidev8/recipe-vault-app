package com.moliverac8.data

import com.moliverac8.domain.RecipeWithIng

interface RepositoryInterface {

    suspend fun getRecipesWithIngs(): List<RecipeWithIng>

    suspend fun getRecipesWithIngsById(id: Int): RecipeWithIng

    suspend fun insertRecipeWithIngredient(recipeWithIng: RecipeWithIng): Long

    suspend fun updateRecipeWithIngredients(old: RecipeWithIng, new: RecipeWithIng)

    suspend fun deleteRecipeWithIng(recipe: RecipeWithIng)
}