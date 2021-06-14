package com.moliverac8.data

import com.moliverac8.domain.RecipeWithIng

/**
 * Defines the methods for the local data source
 */
interface LocalRecipesDataSource {
    /**
     * Inserts a [RecipeWithIng] into the local data source
     * @param recipeWithIng Recipe with a list of ingredients of type [RecipeWithIng]
     * @return The number of rows affected by the query
     */
    suspend fun insertRecipeWithIng(recipeWithIng: RecipeWithIng): Long

    /**
     * Searches [RecipeWithIng] in the local data source
     * @param id identifier of the [RecipeWithIng]
     * @return The result of the query
     */
    suspend fun getRecipeWithIngById(id: Int): RecipeWithIng

    /**
     * Gets all the [RecipeWithIng] from the local data source
     * @return A list of all the [RecipeWithIng]
     */
    suspend fun getAllRecipesWithIng(): List<RecipeWithIng>

    /**
     * Updates a [RecipeWithIng] with new data
     * @param old The old recipe with a list of ingredients of type [RecipeWithIng]
     * @param new The new recipe with a list of ingredients of type [RecipeWithIng]
     */
    suspend fun updateRecipeWithIng(old: RecipeWithIng, new: RecipeWithIng)

    /**
     * Removes a [RecipeWithIng] with from the local data source
     * @param recipe The [RecipeWithIng] that is going to be removed
     */
    suspend fun deleteRecipeWithIng(recipe: RecipeWithIng)
}