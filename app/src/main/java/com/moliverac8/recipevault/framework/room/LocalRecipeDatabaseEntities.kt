package com.moliverac8.recipevault.framework.room

import android.net.Uri
import android.os.Parcelable
import androidx.room.*
import com.moliverac8.domain.DietType
import com.moliverac8.domain.DishType
import kotlinx.parcelize.Parcelize

/**
 * Represents the Recipe SQL table
 *
 * @property recipeID identifier of the recipe, PK of the table
 * @property recipeName title of the recipe
 * @property timeToCook time to cook the recipe in minutes
 * @property dishType list of dishtypes for the recipe
 * @property dietType determines the diet the recipe belongs to
 * @property instructions contains the instructions as a JSON file
 * @property image contains the URI of the image in the local device
 * @property description small description about the recipe
 * @constructor Creates a recipe that con be stored in the SQLite database
 */
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

/**
 * Represents the Ingredient SQL table
 *
 * @property ingID identifier of the ingredient, PK of the table
 * @property unit unit used to measure the ingredient
 * @property quantity quantity in the unit specified by the param [unit]
 * @constructor Creates an ingredient that con be stored in the SQLite database
 */
@Parcelize
@Entity
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val ingID: Int,
    val ingName: String,
    val unit: String,
    val quantity: Double
) : Parcelable

/**
 * Represents the cross reference SQL table between [Recipe] and [Ingredient]
 *
 * @property recipeID identifier of the recipe
 * @property ingID identifier of the ingredient
 * @constructor Create a cross reference object that con be stored in the SQLite database
 */
@Entity(primaryKeys = ["recipeID", "ingID"], indices = [Index(value = ["ingID"])])
data class Recipe_Ing(
    val recipeID: Int,
    val ingID: Int
)

/**
 * Represents the table that Room uses to retrieve a query of a [Recipe] with all of its [Ingredient]
 *
 * @property recipe recipe to retrieve
 * @property ings list of ingredients associated with the recipe
 * @constructor Create a [RecipeWithIng] object with the results of the query
 */
data class RecipeWithIng(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "recipeID",
        entityColumn = "ingID",
        associateBy = Junction(Recipe_Ing::class)
    )
    val ings: List<Ingredient>
)

/**
 * Represents a [TypeConverter] that Room uses to transform an unsupported data type before inserting it to
 * the SQLite database
 */
class DishTypeConverter {

    /**
     * Transforms a list of [DishType] into a [String]
     */
    @TypeConverter
    fun listToString(type: List<DishType>): String {
        return type.joinToString()
    }

    /**
     * Transforms a [String] into a list of [DishType]
     */
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

/**
 * Represents a [TypeConverter] that Room uses to transform an unsupported data type before inserting it to
 * the SQLite database
 */
class DietTypeConverter {
    /**
     * Transforms a [DietType] into a [String]
     */
    @TypeConverter
    fun dietToString(type: DietType): String {
        return type.toString()
    }

    /**
     * Transforms a [String] into a [DietType]
     */
    @TypeConverter
    fun stringToList(type: String): DietType = when (type) {
        "REGULAR" -> DietType.REGULAR
        "VEGAN" -> DietType.VEGAN
        else -> DietType.VEGETARIAN
    }
}

/**
 * Represents a [TypeConverter] that Room uses to transform an unsupported data type before inserting it to
 * the SQLite database
 */
class UriConverter {
    /**
     * Transforms a [Uri] into a [String]
     */
    @TypeConverter
    fun uriToString(uri: Uri): String =
        uri.toString()

    /**
     * Transforms a [String] into a [Uri]
     */
    @TypeConverter
    fun stringToUri(uri: String): Uri =
        Uri.parse(uri)
}