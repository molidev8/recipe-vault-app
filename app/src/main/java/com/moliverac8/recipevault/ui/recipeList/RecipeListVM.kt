package com.moliverac8.recipevault.ui.recipeList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.recipevault.toListOfString
import com.moliverac8.usecases.DeleteRecipe
import com.moliverac8.usecases.GetAllRecipes
import com.moliverac8.usecases.SaveRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecipeListVM @Inject constructor(
    private val getRecipes: GetAllRecipes,
    private val deleteRecipe: DeleteRecipe,
    private val saveRecipe: SaveRecipe
) : ViewModel() {

    private val _recipes = MutableLiveData<List<RecipeWithIng>>()
    val recipes: LiveData<List<RecipeWithIng>>
        get() = _recipes

    private var originalRecipes = listOf<RecipeWithIng>()

    fun loadOriginalRecipes() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _recipes.postValue(originalRecipes)
            }
        }
    }

    fun updateRecipes() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                originalRecipes = getRecipes()
                _recipes.postValue(originalRecipes.sortedBy { it.domainRecipe.name })
            }
        }
    }

    fun deleteRecipeOnDatabase(recipe: RecipeWithIng) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                deleteRecipe(recipe)
            }
        }
    }

    fun addRecipeToDatabase(recipe: RecipeWithIng) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                saveRecipe(recipe)
            }
        }
    }

    fun filterByChips(filters: List<String>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val recipes = getRecipes()
                val filteredRecipes = recipes.filter { recipe ->
                    filters.any { it == recipe.domainRecipe.dietType.name } ||
                            filters.any {
                                recipe.domainRecipe.dishType.toListOfString().contains(it)
                            }
                }
                _recipes.postValue(filteredRecipes)
            }
        }
    }
}