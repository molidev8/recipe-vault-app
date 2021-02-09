package com.moliverac8.recipevault.framework.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface LocalRecipeDatabaseDao {

    @Transaction
    @Query("select * from Recipe where recipeID = (:id)")
    fun getRecipeWithIngredientsByID(id: Int): RecipeWithIng

    @Transaction
    @Query("select * from Recipe")
    fun getRecipeWithIngredients(): List<RecipeWithIng>

    @Insert
    fun insertRecipe(recipe: Recipe): Long

    @Insert
    fun insertIngredient(ing: Ingredient): Long

    @Query("select * from Ingredient where ingID = (:id)")
    fun getIngredient(id: Int): Ingredient

    @Insert
    fun insertRecipeIngCross(ref: Recipe_Ing): Long
}