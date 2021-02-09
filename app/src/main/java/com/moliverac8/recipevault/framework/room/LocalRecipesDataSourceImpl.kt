package com.moliverac8.recipevault.framework.room

import com.moliverac8.data.LocalRecipesDataSource
import com.moliverac8.domain.RecipeWithIng
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject


class LocalRecipesDataSourceImpl(db: LocalRecipeDatabase) : LocalRecipesDataSource {

    private val dao = db.dao()

    override suspend fun insertRecipeWithIng(recipeWithIng: RecipeWithIng): Long =
        withContext(IO) {
            val idRecipe = dao.insertRecipe(recipeWithIng.domainRecipe.toRoom())
            val list = recipeWithIng.ings.map { it.toRoom() }
            list.forEach { ingredient ->
                val idIng = dao.insertIngredient(ingredient)
                dao.insertRecipeIngCross(Recipe_Ing(idRecipe.toInt(), idIng.toInt()))
            }
            return@withContext idRecipe
        }

    override suspend fun getRecipeWithIngById(id: Int): RecipeWithIng =
        withContext(IO) {
            dao.getRecipeWithIngredientsByID(id).toDomain()
        }


    override suspend fun getAllRecipesWithIng(): List<RecipeWithIng> =
        withContext(IO) {
            val list = mutableListOf<RecipeWithIng>()
            dao.getRecipeWithIngredients().forEach {
                list.add(it.toDomain())
            }
            return@withContext list
        }
}