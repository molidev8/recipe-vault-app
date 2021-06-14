package com.moliverac8.recipevault.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.usecases.GetAllRecipes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchVM @Inject constructor(
    private val getRecipes: GetAllRecipes
) : ViewModel() {

    private val _recipes = MutableLiveData<List<RecipeWithIng>>()
    val recipes: LiveData<List<RecipeWithIng>>
        get() = _recipes

    /**
     * Filters the recipes from the complete collection of recipes obtained from the database.
     * It includes filter by recipe name, ingredients and description
     */
    fun filterRecipes(filter: String) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val lowerCaseFilter = filter.lowercase(Locale.getDefault())
                val recipes = getRecipes()
                val filteredRecipes = recipes.filter { recipe ->
                    recipe.domainRecipe.name.lowercase(Locale.getDefault())
                        .contains(lowerCaseFilter) || recipe.domainRecipe.description.lowercase(
                        Locale.getDefault()
                    ).contains(
                        lowerCaseFilter
                    ) || recipe.ings.any {
                        it.name.lowercase(Locale.getDefault()).contains(lowerCaseFilter)
                    }
                }
                _recipes.postValue(filteredRecipes)
            }
        }
    }
}