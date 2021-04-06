package com.moliverac8.recipevault.framework.room

import androidx.room.*

@Dao
interface LocalRecipeDatabaseDao {

    @Transaction
    @Query("select * from Recipe where recipeID = (:id)")
    fun getRecipeWithIngredientsByID(id: Int): RecipeWithIng

    @Transaction
    @Query("select * from Recipe")
    fun getRecipeWithIngredients(): List<RecipeWithIng>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipe(recipe: Recipe): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIngredient(ing: Ingredient): Long

    @Query("select * from Ingredient where ingID = (:id)")
    fun getIngredient(id: Int): Ingredient

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipeIngCross(ref: Recipe_Ing): Long

    @Update
    fun updateRecipe(recipe: Recipe)

    @Update
    fun updateRecipeIngCross(ref: Recipe_Ing)

    @Update
    fun updateIngredient(ing: Ingredient)
}