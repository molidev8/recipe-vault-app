package com.moliverac8.recipevault.framework.room

import androidx.room.*

@Dao
interface LocalRecipeDatabaseDao {

    @Transaction
    @Query("select * from Recipe where recipeID = (:id)")
    suspend fun getRecipeWithIngredientsByID(id: Int): RecipeWithIng

    @Transaction
    @Query("select * from Recipe")
    suspend fun getRecipeWithIngredients(): List<RecipeWithIng>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ing: Ingredient): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeIngCross(ref: Recipe_Ing): Long

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Update
    suspend fun updateRecipeIngCross(ref: Recipe_Ing)

    @Update
    suspend fun updateIngredient(ing: Ingredient)
}