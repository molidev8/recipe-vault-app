package com.moliverac8.recipevault.framework.room

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.moliverac8.domain.DietType
import com.moliverac8.domain.DishType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@SmallTest
@RunWith(AndroidJUnit4::class)
class LocalRecipesDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

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
    fun insertRecipeWithIngAndGetById() = runBlockingTest {
        //GIVEN
        val recipe = Recipe(
            1, "Ensalada", 20, listOf(DishType.MEAL),
            DietType.VEGETARIAN, "Cocinar", Uri.parse("dummyUri"),
            "Lorem ipsum")
        val ing = Ingredient(1, "Lechuga", "hojas", 3.0)
        val cross = Recipe_Ing(1, 1)

        //WHEN
        db.dao().apply {
            insertRecipe(recipe)
            insertIngredient(ing)
            insertRecipeIngCross(cross)
        }

        val loaded = db.dao().getRecipeWithIngredientsByID(recipe.recipeID)

        //THEN
        assertThat(loaded.recipe, `is`(recipe))
        assertThat(loaded.ings.first(), `is`(ing))
    }

    @Test
    fun insertAndUpdateRecipeWithIng() = runBlockingTest {
        //GIVEN
        val recipe = Recipe(
            1, "Ensalada", 20, listOf(DishType.MEAL),
            DietType.VEGETARIAN, "Cocinar", Uri.parse("dummyUri"),
            "Lorem ipsum")
        val ing = Ingredient(1, "Lechuga", "hojas", 3.0)
        val cross = Recipe_Ing(1, 1)

        //WHEN
        db.dao().apply {
            insertRecipe(recipe)
            insertIngredient(ing)
            insertRecipeIngCross(cross)
        }

        val updatedRecipe = Recipe(
            1, "Ensalada2", 200, listOf(DishType.BREAKFAST),
            DietType.VEGETARIAN, "Cocinar", Uri.parse("dummyUri"),
            "Lorem ipsum")

        val updatedIng = Ingredient(1, "Lechuga2", "hojasssss", 3.0)

        db.dao().updateIngredient(updatedIng)
        db.dao().updateRecipe(updatedRecipe)

        val loaded = db.dao().getRecipeWithIngredientsByID(recipe.recipeID)

        //THEN
        assertThat(loaded.recipe, `is`(updatedRecipe))
        assertThat(loaded.ings.first(), `is`(updatedIng))
    }
}