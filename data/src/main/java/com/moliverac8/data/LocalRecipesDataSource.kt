package com.moliverac8.data

import com.moliverac8.domain.RecipeWithIng

interface LocalRecipesDataSource {
    suspend fun insertRecipeWithIng(recipeWithIng: RecipeWithIng): Long
    suspend fun getRecipeWithIngById(id: Int): RecipeWithIng
    suspend fun getAllRecipesWithIng(): List<RecipeWithIng>
}