package com.moliverac8.recipevault.ui.recipeList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.moliverac8.usecases.GetAllRecipes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecipeListVM @Inject constructor(
    private val getRecipes: GetAllRecipes
) : ViewModel() {

    val recipes = liveData {
        val recipes = getRecipes.invoke()
        emit(recipes)
    }

}