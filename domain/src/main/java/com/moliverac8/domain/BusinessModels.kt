package com.moliverac8.domain

data class Recipe(
    val id: Int,
    val name: String,
    val timeToCook: Int,
    val dishType: List<DishType>,
    val dietType: DietType,
    val instructions: String, //Realmente es un JSON pero se almacena como un string
    val image: String, //URI
    val description: String
)

data class Ingredient(
    val id: Int,
    val name: String,
    val unit: String,
    val quantity: Double
)

data class RecipeWithIng(
    val domainRecipe: Recipe,
    val ings: List<Ingredient>
)

enum class DishType {
    BREAKFAST, MEAL, DINNER
}

enum class DietType {
    VEGETARIAN, VEGAN, REGULAR
}