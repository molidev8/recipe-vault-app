package com.moliverac8.recipevault.ui.recipeDetail

import androidx.lifecycle.*
import com.moliverac8.domain.Recipe
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.usecases.GetRecipeByID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailVM @Inject constructor(
    private val getRecipeByID: GetRecipeByID
) : ViewModel() {

    val recipeWithIng: LiveData<RecipeWithIng>
        get() = _recipeWithIng
    private val _recipeWithIng = MutableLiveData<RecipeWithIng>()

    /**
     * Cuándo la receta sea nueva, se coloca en livedata una receta vacía
     */
    fun getRecipe(id: Int) {
        viewModelScope.launch {
            if (id == -1) _recipeWithIng.value = RecipeWithIng(Recipe(), listOf())
            else _recipeWithIng.value = getRecipeByID.invoke(id)
        }
    }
}