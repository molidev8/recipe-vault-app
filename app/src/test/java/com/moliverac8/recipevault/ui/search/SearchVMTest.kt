package com.moliverac8.recipevault.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moliverac8.domain.DietType
import com.moliverac8.domain.DishType
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.usecases.GetAllRecipes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SearchVMTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var getAllRecipes: GetAllRecipes
    private lateinit var viewModel: SearchVM
    private lateinit var repository: FakeRepository

    @Before
    fun init() {
        repository = FakeRepository()
        getAllRecipes = GetAllRecipes(repository)
        viewModel = SearchVM(getAllRecipes)
    }

    @Test
    fun filterRecipesByName() = runBlockingTest {
        //GIVEN
        val recipe = com.moliverac8.domain.Recipe(
            1, "Ensalada", 20, listOf(DishType.MEAL),
            DietType.VEGETARIAN, "Cocinar", "dummyUri",
            "Lorem ipsum"
        )
        val ing = com.moliverac8.domain.Ingredient(1, "Lechuga", "hojas", 3.0)
        val recipeWithIng = RecipeWithIng(recipe, mutableListOf(ing))
        repository.insertRecipeWithIngredient(recipeWithIng)

        //WHEN
        viewModel.filterRecipes("ensalada")

        //THEN
        val value = viewModel.recipes.getOrAwaitValue()

        assertThat(value.first(), `is`(recipeWithIng))
    }

    @Test
    fun filterRecipesByIng() = runBlockingTest {
        //GIVEN
        val recipe = com.moliverac8.domain.Recipe(
            1, "Ensalada", 20, listOf(DishType.MEAL),
            DietType.VEGETARIAN, "Cocinar", "dummyUri",
            "Lorem ipsum"
        )
        val ing = com.moliverac8.domain.Ingredient(1, "Lechuga", "hojas", 3.0)
        val recipeWithIng = RecipeWithIng(recipe, mutableListOf(ing))
        repository.insertRecipeWithIngredient(recipeWithIng)

        //WHEN
        viewModel.filterRecipes("lechuga")

        //THEN
        val value = viewModel.recipes.getOrAwaitValue()

        assertThat(value.first(), `is`(recipeWithIng))
    }

    @Test
    fun filterRecipesByDescription() = runBlockingTest {
        //GIVEN
        val recipe = com.moliverac8.domain.Recipe(
            1, "Ensalada", 20, listOf(DishType.MEAL),
            DietType.VEGETARIAN, "Cocinar", "dummyUri",
            "Lorem ipsum"
        )
        val ing = com.moliverac8.domain.Ingredient(1, "Lechuga", "hojas", 3.0)
        val recipeWithIng = RecipeWithIng(recipe, mutableListOf(ing))
        repository.insertRecipeWithIngredient(recipeWithIng)

        //WHEN
        viewModel.filterRecipes("lorem ipsum")

        //THEN
        val value = viewModel.recipes.getOrAwaitValue()

        assertThat(value.first(), `is`(recipeWithIng))
    }
}