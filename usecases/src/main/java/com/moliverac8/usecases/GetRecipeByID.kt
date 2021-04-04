package com.moliverac8.usecases

import com.moliverac8.data.RecipesRepository
import com.moliverac8.domain.Recipe
import com.moliverac8.domain.RecipeWithIng

class GetRecipeByID(private val recipesRepository: RecipesRepository) {
    suspend operator fun invoke(id: Int): RecipeWithIng = recipesRepository.getRecipesWithIngsById(id)
}