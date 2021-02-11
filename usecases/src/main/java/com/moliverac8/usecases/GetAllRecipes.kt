package com.moliverac8.usecases

import com.moliverac8.data.RecipesRepository
import com.moliverac8.domain.RecipeWithIng

class GetAllRecipes(private val recipesRepository: RecipesRepository) {
    suspend fun invoke(): List<RecipeWithIng> = recipesRepository.getRecipesWithIngs()
}