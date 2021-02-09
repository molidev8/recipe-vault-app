package com.moliverac8.recipevault

import android.net.Uri
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.moliverac8.domain.DietType
import com.moliverac8.domain.DishType
import com.moliverac8.recipevault.framework.room.Ingredient
import com.moliverac8.recipevault.framework.room.LocalRecipeDatabase
import com.moliverac8.recipevault.framework.room.Recipe
import com.moliverac8.recipevault.framework.room.Recipe_Ing
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class LocalRecipesDaoTest {

    private lateinit var db: LocalRecipeDatabase

    @Before
    fun initDb() {
        db = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            LocalRecipeDatabase::class.java
        ).build()
    }

    @After
    fun close() = db.close()

    @Test
    fun insertRecipeAndGetById() {
        //GIVEN
        val recipe = Recipe(
            1,
            "Ensalada",
            20,
            listOf(DishType.MEAL),
            listOf(DietType.VEGETARIAN),
            "Cocinar",
            Uri.parse("dummyUri"),
            "Lorem ipsum"
        )

        //WHEN
        db.dao().insertRecipe(recipe)

        val loaded = db.dao().getRecipeWithIngredientsByID(recipe.recipeID)

        //THEN
        assert(loaded.recipe == recipe)
    }

    @Test
    fun insertRecipeWithIngAndGetById() {
        //GIVEN
        val recipe = Recipe(
            1, "Ensalada", 20, listOf(DishType.MEAL),
            listOf(DietType.VEGETARIAN), "Cocinar", Uri.parse("dummyUri"),
            "Lorem ipsum")
        val ing = Ingredient(1, "Lechuga", "hojas", 3.0)
        val cross = Recipe_Ing(1, 1)

        //WHEN
        db.dao().insertRecipe(recipe)
        db.dao().insertIngredient(ing)
        db.dao().insertRecipeIngCross(cross)

        val loaded = db.dao().getRecipeWithIngredientsByID(recipe.recipeID)

        //THEN
        assert(loaded.recipe == recipe)
        assert(loaded.ings.first() == ing)
    }
}