package com.moliverac8.recipevault.ui.search

import com.moliverac8.data.RepositoryInterface
import com.moliverac8.domain.Recipe
import com.moliverac8.domain.RecipeWithIng

class FakeRepository : RepositoryInterface {

    private val list = mutableListOf<RecipeWithIng>()

    override suspend fun getRecipesWithIngs(): List<RecipeWithIng> {
        return list
    }

    override suspend fun getRecipesWithIngsById(id: Int): RecipeWithIng {
        return list.find { it.domainRecipe.id == id } ?: RecipeWithIng(Recipe(), mutableListOf())
    }

    override suspend fun insertRecipeWithIngredient(recipeWithIng: RecipeWithIng): Long {
        list.add(recipeWithIng)
        return 1
    }

    override suspend fun updateRecipeWithIngredients(old: RecipeWithIng, new: RecipeWithIng) {
        list.remove(old)
        list.add(new)
    }

    override suspend fun deleteRecipeWithIng(recipe: RecipeWithIng) {
        list.remove(recipe)
    }
}