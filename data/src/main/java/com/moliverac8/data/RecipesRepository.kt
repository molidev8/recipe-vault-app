package com.moliverac8.data

import com.moliverac8.domain.RecipeWithIng

class RecipesRepository(private val localRecipeDatabase: LocalRecipesDataSource) {

    suspend fun getRecipesWithIngs(): List<RecipeWithIng> =
        localRecipeDatabase.getAllRecipesWithIng()

    suspend fun getRecipesWithIngsById(id: Int): RecipeWithIng =
        localRecipeDatabase.getRecipeWithIngById(id)

    suspend fun insertRecipeWithIngredient(recipeWithIng: RecipeWithIng): Long =
        localRecipeDatabase.insertRecipeWithIng(recipeWithIng)
}