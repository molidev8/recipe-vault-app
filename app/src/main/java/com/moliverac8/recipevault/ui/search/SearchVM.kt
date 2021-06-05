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
import javax.inject.Inject

@HiltViewModel
class SearchVM @Inject constructor(
    private val getRecipes: GetAllRecipes
) : ViewModel() {

    private val _recipes = MutableLiveData<List<RecipeWithIng>>()
    val recipes: LiveData<List<RecipeWithIng>>
        get() = _recipes

    fun filterRecipes(filter: String) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val recipes = getRecipes()
                val filteredRecipes = recipes.filter { recipe ->
                    recipe.domainRecipe.name.contains(filter) || recipe.domainRecipe.description.contains(
                        filter) || recipe.ings.any { it.name.contains(filter) }
                }
                _recipes.postValue(filteredRecipes)
            }
        }
    }
}