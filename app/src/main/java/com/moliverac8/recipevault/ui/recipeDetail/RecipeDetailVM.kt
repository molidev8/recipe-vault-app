package com.moliverac8.recipevault.ui.recipeDetail

import androidx.lifecycle.*
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

    fun getRecipe(id: Int) {
        viewModelScope.launch {
            _recipeWithIng.value = getRecipeByID.invoke(id)
        }
    }
}