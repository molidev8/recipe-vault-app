package com.moliverac8.recipevault.framework.room

import android.net.Uri
import android.os.Parcelable
import androidx.room.*
import com.moliverac8.domain.DietType
import com.moliverac8.domain.DishType
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Recipe(
    @PrimaryKey(autoGenerate = true) val recipeID: Int,
    val recipeName: String,
    val timeToCook: Int,
    val dishType: List<DishType>,
    val dietType: DietType,
    val instructions: String,
    val image: Uri,
    val description: String
) : Parcelable

@Parcelize
@Entity
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val ingID: Int,
    val ingName: String,
    val unit: String,
    val quantity: Double
) : Parcelable


@Entity(primaryKeys = ["recipeID", "ingID"], indices = arrayOf(Index(value = ["ingID"])))
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

class DishTypeConverter {
    @TypeConverter
    fun listToString(type: List<DishType>): String {
        return type.joinToString()
    }

    @TypeConverter
    fun stringToList(type: String): List<DishType> {
        val list = mutableListOf<DishType>()
        type.split(",").forEach {
            when (it) {
                "BREAKFAST" -> list.add(DishType.BREAKFAST)
                "MEAL" -> list.add(DishType.MEAL)
                else -> list.add(DishType.DINNER)
            }
        }
        return list
    }
}

class DietTypeConverter {
    @TypeConverter
    fun dietToString(type: DietType): String {
        return type.toString()
    }

    @TypeConverter
    fun stringToList(type: String): DietType = when (type) {
        "REGULAR" -> DietType.REGULAR
        "VEGAN" -> DietType.VEGAN
        else -> DietType.VEGETARIAN
    }
}

class UriConverter {
    @TypeConverter
    fun uriToString(uri: Uri): String =
        uri.toString()

    @TypeConverter
    fun stringToUri(uri: String): Uri =
        Uri.parse(uri)
}