package com.moliverac8.recipevault.ui.recipeList

import android.util.Log
import androidx.lifecycle.*
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.usecases.GetAllRecipes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecipeListVM @Inject constructor(
    private val getRecipes: GetAllRecipes
) : ViewModel() {

    private val _recipes = MutableLiveData<List<RecipeWithIng>>()
    val recipes: LiveData<List<RecipeWithIng>>
        get() = _recipes

    fun updateRecipes() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _recipes.postValue(getRecipes())
            }
        }
    }
}