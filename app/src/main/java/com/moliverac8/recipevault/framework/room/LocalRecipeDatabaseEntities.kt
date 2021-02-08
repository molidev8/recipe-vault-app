package com.moliverac8.recipevault.framework.room

import android.net.Uri
import android.os.Parcelable
import androidx.room.*
import com.moliverac8.domain.DietType
import com.moliverac8.domain.DishType
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Recipe(
    @PrimaryKey(autoGenerate = true) val recipeID: Int,
    val recipeName: String,
    val timeToCook: Int,
    val dishType: List<DishType>,
    val dietType: List<DietType>,
    val instructions: String,
    val image: Uri,
    val description: String
) : Parcelable

@Entity
@Parcelize
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val ingID: Int,
    val ingName: String,
    val unit: String,
    val quantity: Double
) : Parcelable


@Entity(primaryKeys = ["recipeID", "ingID"])
data class Recipe_Ing(
    val recipeID: Int,
    val ingID: Int
)

data class RecipeWithIng(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "recipeID",
        entityColumn = "ingID",
        associateBy = Junction(Recipe_Ing::class)
    )
    val ings: List<Ingredient>
)