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

/**
 * Represents an access point to the SQLite [RoomDatabase]
 * @constructor Creates an unique instance of [LocalRecipeDatabase] that lives with the lifecycle
 * of the application
 */
abstract class LocalRecipeDatabase : RoomDatabase() {
    abstract fun dao(): LocalRecipeDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: LocalRecipeDatabase? = null

        /**
         * Creates an instance of [LocalRecipeDatabase] following the Singleton pattern
         * @param context context of the application
         * @constructor returns an unique instance of [LocalRecipeDatabase]
         * @return an instance of [LocalRecipeDatabase]
         */
        fun getInstance(context: Context): LocalRecipeDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LocalRecipeDatabase::class.java,
                        DATABASE_NAME

                    ).setJournalMode(JournalMode.TRUNCATE).build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}