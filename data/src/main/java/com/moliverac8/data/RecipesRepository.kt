package com.moliverac8.data

import com.moliverac8.domain.RecipeWithIng

class RecipesRepository(private val localRecipeDatabase: LocalRecipesDataSource) :
    RepositoryInterface {

    override suspend fun getRecipesWithIngs(): List<RecipeWithIng> =
        localRecipeDatabase.getAllRecipesWithIng()

    override suspend fun getRecipesWithIngsById(id: Int): RecipeWithIng =
        localRecipeDatabase.getRecipeWithIngById(id)

    override suspend fun insertRecipeWithIngredient(recipeWithIng: RecipeWithIng): Long =
        localRecipeDatabase.insertRecipeWithIng(recipeWithIng)

    override suspend fun updateRecipeWithIngredients(old: RecipeWithIng, new: RecipeWithIng) =
        localRecipeDatabase.updateRecipeWithIng(old, new)

    override suspend fun deleteRecipeWithIng(recipe: RecipeWithIng) =
        localRecipeDatabase.deleteRecipeWithIng(recipe)
}