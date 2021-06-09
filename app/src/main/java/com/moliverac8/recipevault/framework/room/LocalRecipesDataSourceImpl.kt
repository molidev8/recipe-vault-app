package com.moliverac8.recipevault.framework.room

import android.util.Log
import com.moliverac8.data.LocalRecipesDataSource
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.recipevault.GENERAL
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext


class LocalRecipesDataSourceImpl(
    db: LocalRecipeDatabase,
    private val dispatcher: CoroutineDispatcher = IO
) :
    LocalRecipesDataSource {

    private val dao = db.dao()

    override suspend fun insertRecipeWithIng(recipeWithIng: RecipeWithIng): Long =
        withContext(dispatcher) {
            val idRecipe = dao.insertRecipe(recipeWithIng.domainRecipe.toRoom())
            val list = recipeWithIng.ings.map { it.toRoom() }
            list.forEach { ingredient ->
                val idIng = dao.insertIngredient(ingredient)
                dao.insertRecipeIngCross(
                    Recipe_Ing(
                        idRecipe.toInt(), if (idIng != -1L) idIng.toInt()
                        else ingredient.ingID
                    )
                )
            }
            return@withContext idRecipe
        }

    override suspend fun getRecipeWithIngById(id: Int): RecipeWithIng =
        withContext(dispatcher) {
            dao.getRecipeWithIngredientsByID(id).toDomain()
        }


    override suspend fun getAllRecipesWithIng(): List<RecipeWithIng> =
        withContext(dispatcher) {
            val list = mutableListOf<RecipeWithIng>()
            dao.getRecipeWithIngredients().forEach {
                list.add(it.toDomain())
            }
            return@withContext list
        }

    override suspend fun updateRecipeWithIng(old: RecipeWithIng, new: RecipeWithIng) {
        withContext(dispatcher) {
            dao.updateRecipe(new.domainRecipe.toRoom())
            val ings = new.ings.subtract(old.ings).map { it.toRoom() }

            ings.forEach { ingredient ->
                if (ingredient.ingID == 0) {
                    val idIng = dao.insertIngredient(ingredient)
                    dao.insertRecipeIngCross(Recipe_Ing(new.domainRecipe.id, idIng.toInt()))
                } else {
                    dao.updateIngredient(ingredient)
                }
            }
        }
    }

    override suspend fun deleteRecipeWithIng(recipe: RecipeWithIng) {
        withContext(dispatcher) {
            dao.deleteRecipe(recipe.domainRecipe.toRoom())
            recipe.ings.forEach { ing ->
                dao.deleteRecipeIngCross(Recipe_Ing(recipe.domainRecipe.id, ing.id))
            }
        }
    }
}