package com.moliverac8.recipevault.framework.room

import android.net.Uri
import com.moliverac8.domain.Recipe as DomainRecipe
import com.moliverac8.domain.Ingredient as DomainIngredient
import com.moliverac8.domain.RecipeWithIng as DomainRecipeWithIng

fun DomainRecipe.toRoom(): Recipe {
    val aux = if (id != -1) id
    else 0
    return Recipe(aux, name, timeToCook, dishType, dietType, instructions, Uri.parse(image), description)
}

fun Recipe.toDomain(): DomainRecipe =
    DomainRecipe(recipeID, recipeName, timeToCook, dishType, dietType, instructions, image.toString(), description)

fun DomainIngredient.toRoom(): Ingredient {
    val aux = if (id != -1) id
    else 0
    return Ingredient(aux, name, unit, quantity)
}


fun Ingredient.toDomain(): DomainIngredient =
    DomainIngredient(ingID, ingName, unit, quantity)

fun RecipeWithIng.toDomain(): DomainRecipeWithIng {
    val list = mutableListOf<DomainIngredient>()
    ings.forEach { ing ->
        list.add(ing.toDomain())
    }
    return DomainRecipeWithIng(recipe.toDomain(), list)
}
