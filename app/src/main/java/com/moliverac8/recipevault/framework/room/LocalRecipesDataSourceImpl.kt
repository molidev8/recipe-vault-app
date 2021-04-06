package com.moliverac8.recipevault.framework.room

import android.net.Uri
import android.util.Log
import com.moliverac8.data.LocalRecipesDataSource
import com.moliverac8.domain.DietType
import com.moliverac8.domain.DishType
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.recipevault.GENERAL
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

    override suspend fun updateRecipeWithIng(old: RecipeWithIng, new: RecipeWithIng) {
        withContext(IO) {
            dao.updateRecipe(new.domainRecipe.toRoom())
            val ings = new.ings.subtract(old.ings).map { it.toRoom() }
            Log.d(GENERAL, "Nueva $ings")
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
}

class FakeRecipesDataSourceImpl : LocalRecipesDataSource {

    val recipe = Recipe(
        1, "Ensalada de patata", 20, listOf(DishType.MEAL, DishType.DINNER),
        DietType.VEGETARIAN, """[ "Cocemos las patatas sin pelar.
En una olla, ponemos las patatas y añadimos agua
hasta que estén bien cubiertas. Lo calentamos, y
dejamos que las patatas se cocinen durante 35 minutos.
Pasado este tiempo, las pinchamos con un cuchillo para
comprobar si están completamente cocinadas. Si el
cuchillo las atraviesa con facilidad hasta el centro,
puedes retirarlas del fuego.", "Luego comer", "Cocemos las patatas sin pelar.
En una olla, ponemos las patatas y añadimos agua
hasta que estén bien cubiertas. Lo calentamos, y
dejamos que las patatas se cocinen durante 35 minutos.
Pasado este tiempo, las pinchamos con un cuchillo para
comprobar si están completamente cocinadas. Si el
cuchillo las atraviesa con facilidad hasta el centro,
puedes retirarlas del fuego." ]""", Uri.parse(
            "android.resource://com.moliverac8.recipevault/drawable/ensalada_de_patata_y_aguacate"
        ),
        "Ensalada de patata y aguacate. En esta ocasión, vamos a preparar un plato " +
                "frío que se hace en muy pocos minutos"
    )
    val recipe2 = Recipe(
        2, "Ensalada de patata", 20, listOf(DishType.MEAL),
        DietType.VEGETARIAN, """[ "Cocemos las patatas sin pelar.
En una olla, ponemos las patatas y añadimos agua
hasta que estén bien cubiertas. Lo calentamos, y
dejamos que las patatas se cocinen durante 35 minutos.
Pasado este tiempo, las pinchamos con un cuchillo para
comprobar si están completamente cocinadas. Si el
cuchillo las atraviesa con facilidad hasta el centro,
puedes retirarlas del fuego.", "Cocemos las patatas sin pelar.
En una olla, ponemos las patatas y añadimos agua
hasta que estén bien cubiertas. Lo calentamos, y
dejamos que las patatas se cocinen durante 35 minutos.
Pasado este tiempo, las pinchamos con un cuchillo para
comprobar si están completamente cocinadas. Si el
cuchillo las atraviesa con facilidad hasta el centro,
puedes retirarlas del fuego.", "Terminar y recoger" ]""", Uri.parse(
            "android.resource://com.moliverac8.recipevault/drawable/ensalada_de_patata_y_aguacate"
        ),
        "Ensalada de patata y aguacate. En esta ocasión, vamos a preparar un plato " +
                "frío que se hace en muy pocos minutos"
    )
    val ing = Ingredient(1, "Lechuga", "hojas", 3.0)
    val cross = Recipe_Ing(1, 1)
    var list = mutableListOf<RecipeWithIng>()

    init {
        val ings = mutableListOf(ing, ing, ing)
        val sample = RecipeWithIng(recipe, ings).toDomain()
        val sample2 = RecipeWithIng(recipe2, ings).toDomain()

        list = mutableListOf(sample, sample2)
    }

    override suspend fun insertRecipeWithIng(recipeWithIng: RecipeWithIng): Long {
        list.add(recipeWithIng)
        return 1
    }

    override suspend fun getRecipeWithIngById(id: Int): RecipeWithIng {
        return list.filter { id == id }.first()
    }

    override suspend fun getAllRecipesWithIng(): List<RecipeWithIng> = list
    override suspend fun updateRecipeWithIng(old: RecipeWithIng, new: RecipeWithIng) {
        TODO("Not yet implemented")
    }
}