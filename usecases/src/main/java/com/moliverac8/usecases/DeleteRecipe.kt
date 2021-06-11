package com.moliverac8.usecases

import com.moliverac8.data.RecipesRepository
import com.moliverac8.data.RepositoryInterface
import com.moliverac8.domain.RecipeWithIng

class DeleteRecipe(private val recipesRepository: RepositoryInterface) {
    suspend operator fun invoke(recipeWithIng: RecipeWithIng) =
        recipesRepository.deleteRecipeWithIng(recipeWithIng)
}