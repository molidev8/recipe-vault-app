package com.moliverac8.recipevault.framework.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

const val DATABASE_NAME = "recipes_db"

@Database(
    entities = [Recipe::class, Ingredient::class, Recipe_Ing::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(DishTypeConverter::class, DietTypeConverter::class, UriConverter::class)


abstract class LocalRecipeDatabase : RoomDatabase() {
    abstract fun dao(): LocalRecipeDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: LocalRecipeDatabase? = null

        fun getInstance(context: Context): LocalRecipeDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LocalRecipeDatabase::class.java,
                        DATABASE_NAME
                    ).build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}