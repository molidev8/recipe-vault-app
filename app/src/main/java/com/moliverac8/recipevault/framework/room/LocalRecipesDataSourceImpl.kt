package com.moliverac8.recipevault.framework.room

import android.net.Uri
import com.moliverac8.data.LocalRecipesDataSource
import com.moliverac8.domain.DietType
import com.moliverac8.domain.DishType
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

class FakeRecipesDataSourceImpl : LocalRecipesDataSource {

    val recipe = Recipe(
        1, "Ensalada de patata", 20, listOf(DishType.MEAL),
        DietType.VEGETARIAN, "Cocinar", Uri.parse(
            "android.resource://com.moliverac8.recipevault/drawable/ensalada_de_patata_y_aguacate"),
        "Ensalada de pata y aguacate. En esta ocasión, vamos a preparar un plato " +
                "frío que se hace en muy pocos minutos"
        )
        val ing = Ingredient (1, "Lechuga", "hojas", 3.0)
    val cross = Recipe_Ing(1, 1)


    override suspend fun insertRecipeWithIng(recipeWithIng: RecipeWithIng): Long {
        TODO("Not yet implemented")
    }

    override suspend fun getRecipeWithIngById(id: Int): RecipeWithIng {
        val ings = mutableListOf(ing, ing, ing)
        return RecipeWithIng(recipe, ings).toDomain()
    }

    override suspend fun getAllRecipesWithIng(): List<RecipeWithIng> =
        withContext(IO) {
            val ings = mutableListOf(ing, ing, ing)
            val sample = RecipeWithIng(recipe, ings).toDomain()
            val list = mutableListOf(sample, sample, sample, sample)
            return@withContext list
        }

}