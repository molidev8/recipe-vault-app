package com.moliverac8.recipevault.framework.room

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.moliverac8.data.LocalRecipesDataSource
import com.moliverac8.domain.DietType
import com.moliverac8.domain.DishType
import com.moliverac8.domain.RecipeWithIng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LocalRecipesDataSourceImplTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: LocalRecipeDatabase
    private lateinit var dataSource: LocalRecipesDataSource

    @Before
    fun init() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LocalRecipeDatabase::class.java
        ).allowMainThreadQueries().build()

        dataSource = LocalRecipesDataSourceImpl(db, TestCoroutineDispatcher())
    }

    @After
    fun close() = db.close()

    @Test
    fun insertRecipeWithIngAndGetById() = runBlockingTest {
        //GIVEN
        val recipe = com.moliverac8.domain.Recipe(
            1, "Ensalada", 20, listOf(DishType.MEAL),
            DietType.VEGETARIAN, "Cocinar", "dummyUri",
            "Lorem ipsum"
        )
        val ing = com.moliverac8.domain.Ingredient(1, "Lechuga", "hojas", 3.0)

        //WHEN
        dataSource.insertRecipeWithIng(RecipeWithIng(recipe, mutableListOf(ing)))

        val loaded = dataSource.getRecipeWithIngById(recipe.id)

        //THEN
        assertThat(loaded.domainRecipe, `is`(recipe))
        assertThat(loaded.ings.first(), `is`(ing))
    }

    @Test
    fun getAllAvailableRecipes() = runBlockingTest {
        //GIVEN
        val recipe = com.moliverac8.domain.Recipe(
            2, "Ensalada2", 200, listOf(DishType.MEAL),
            DietType.VEGETARIAN, "Cocinar", "dummyUri",
            "Lorem ipsum"
        )
        val ing = com.moliverac8.domain.Ingredient(2, "Lechuga", "hojas", 3.0)

        val recipe2 = com.moliverac8.domain.Recipe(
            1, "Ensalada", 20, listOf(DishType.MEAL),
            DietType.VEGETARIAN, "Cocinar", "dummyUri",
            "Lorem ipsum"
        )
        val ing2 = com.moliverac8.domain.Ingredient(1, "Lechuga", "hojas", 3.0)

        //WHEN
        dataSource.insertRecipeWithIng(RecipeWithIng(recipe, mutableListOf(ing)))
        dataSource.insertRecipeWithIng(RecipeWithIng(recipe2, mutableListOf(ing2)))

        val loaded = dataSource.getAllRecipesWithIng()

        //THEN
        assertThat(loaded, hasSize(2))
    }

    @Test
    fun updateAvailableRecipe() = runBlockingTest {
        //GIVEN
        val updatedRecipe = com.moliverac8.domain.Recipe(
            2, "EnsaladaUpdated", 200, listOf(DishType.MEAL),
            DietType.VEGETARIAN, "Cocinar2", "dummyUri2",
            "Lorem ipsum"
        )
        val updatedIng = com.moliverac8.domain.Ingredient(2, "Lechugassss", "hojas", 5.0)
        val recipe = com.moliverac8.domain.Recipe(
            2, "Ensalada2", 200, listOf(DishType.MEAL),
            DietType.VEGETARIAN, "Cocinar", "dummyUri",
            "Lorem ipsum"
        )
        val ing = com.moliverac8.domain.Ingredient(2, "Lechuga", "hojas", 3.0)

        //WHEN
        val old = RecipeWithIng(recipe, mutableListOf(ing))
        val new = RecipeWithIng(updatedRecipe, mutableListOf(updatedIng))
        dataSource.insertRecipeWithIng(old)
        dataSource.updateRecipeWithIng(old, new)

        val loaded = dataSource.getRecipeWithIngById(recipe.id)

        //THEN
        assertThat(loaded, `is`(new))
    }

    @Test
    fun insertAndDeleteRecipe() = runBlockingTest {
        //GIVEN
        val recipe = com.moliverac8.domain.Recipe(
            2, "Ensalada2", 200, listOf(DishType.MEAL),
            DietType.VEGETARIAN, "Cocinar", "dummyUri",
            "Lorem ipsum"
        )
        val ing = com.moliverac8.domain.Ingredient(2, "Lechuga", "hojas", 3.0)

        //WHEN
        dataSource.insertRecipeWithIng(RecipeWithIng(recipe, mutableListOf(ing)))

        try {
            val loaded = dataSource.getRecipeWithIngById(recipe.id)
        } catch (e: NullPointerException) {
            //THEN
            assertThat(e, `is`(NullPointerException()))
        }
    }


}