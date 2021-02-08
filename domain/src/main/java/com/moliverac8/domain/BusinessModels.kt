package com.moliverac8.domain

data class Recipe(
    val name: String,
    val timeToCook: Int,
    val dishType: List<DishType>,
    val dietType: List<DietType>,
    val instructions: String,
    val image: String, //URI
    val description: String
)

data class Ingredient(
    val name: String,
    val unit: String,
    val quantity: Double
)

enum class DishType {
    BREAKFAST, MEAL, DINNER
}

enum class DietType {
    VEGETARIAN, VEGAN, REGULAR
}