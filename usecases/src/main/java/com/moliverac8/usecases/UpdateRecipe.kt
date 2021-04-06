package com.moliverac8.usecases

import com.moliverac8.data.RecipesRepository
import com.moliverac8.domain.RecipeWithIng

class UpdateRecipe(private val recipesRepository: RecipesRepository) {
    suspend operator fun invoke(old: RecipeWithIng, new: RecipeWithIng) =
        recipesRepository.updateRecipeWithIngredients(old, new)
}