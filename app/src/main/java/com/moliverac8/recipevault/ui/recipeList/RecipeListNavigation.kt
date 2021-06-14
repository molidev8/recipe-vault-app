package com.moliverac8.recipevault.ui.recipeList

import android.view.View

/**
 * Defines the navigation transactions from the RecipeListNavigation
 */
interface RecipeListNavigation {
    fun navigateToNewRecipe()
    fun navigateToExistingRecipe(id: Int, recipeCard: View)
    fun navigateToSearch()
}