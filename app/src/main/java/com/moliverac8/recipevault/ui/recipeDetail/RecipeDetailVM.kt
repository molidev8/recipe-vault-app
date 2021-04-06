package com.moliverac8.recipevault.ui.recipeDetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moliverac8.domain.Ingredient
import com.moliverac8.domain.Recipe
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.recipevault.GENERAL
import com.moliverac8.usecases.GetRecipeByID
import com.moliverac8.usecases.SaveRecipe
import com.moliverac8.usecases.UpdateRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecipeDetailVM @Inject constructor(
    private val getRecipeByID: GetRecipeByID,
    private val saveRecipeWithIng: SaveRecipe,
    private val updateRecipe: UpdateRecipe
) : ViewModel() {

    val recipeWithIng: LiveData<RecipeWithIng>
        get() = _recipeWithIng
    private val _recipeWithIng = MutableLiveData<RecipeWithIng>()

    private var tempIngs: List<Ingredient> = mutableListOf()
    private var tempRecipe: Recipe = Recipe()

    /**
     * Cuando la receta sea nueva, se coloca en livedata una receta vacía
     */
    fun getRecipe(id: Int) {
        viewModelScope.launch {
            withContext(IO) {
                if (id == -1) _recipeWithIng.postValue(RecipeWithIng(Recipe(), listOf()))
                else {
                    val rec = getRecipeByID(id)
                    Log.d(GENERAL, "VM - Receta cargada $rec")
                    _recipeWithIng.postValue(rec)
                }
            }
        }
    }

    fun saveIngredients(ings: List<Ingredient>) {
        viewModelScope.launch {
            withContext(IO) {
                tempIngs = ings
            }
        }
    }

    fun saveRecipe(recipe: Recipe) {
        viewModelScope.launch {
            withContext(IO) {
                tempRecipe = recipe
            }
        }
    }

    fun updateRecipe(old: RecipeWithIng) {
        viewModelScope.launch {
            withContext(IO) {
                updateRecipe(old, RecipeWithIng(tempRecipe, tempIngs))
            }
        }
    }

    fun saveEverything() {
        viewModelScope.launch {
            withContext(IO) {
                saveRecipeWithIng(RecipeWithIng(tempRecipe, tempIngs))
            }
        }
    }


}