package com.moliverac8.recipevault.ui.recipeList

import android.view.View

interface RecipeListNavigation {
    fun navigateToNewRecipe()
    fun navigateToExistingRecipe(id: Int, recipeCard: View)
    fun navigateToSearch()
}