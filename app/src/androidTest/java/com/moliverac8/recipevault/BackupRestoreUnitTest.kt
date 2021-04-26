package com.moliverac8.recipevault

import android.content.Context
import android.net.Uri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moliverac8.domain.DietType
import com.moliverac8.domain.DishType
import com.moliverac8.recipevault.framework.room.Ingredient
import com.moliverac8.recipevault.framework.room.LocalRecipeDatabase
import com.moliverac8.recipevault.framework.room.Recipe
import com.moliverac8.recipevault.framework.room.Recipe_Ing
import com.moliverac8.recipevault.framework.workmanager.BackupUserData
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BackupRestoreUnitTest {

    private lateinit var backupUserData: BackupUserData
    private lateinit var context: Context
    private lateinit var db: LocalRecipeDatabase

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        backupUserData = BackupUserData(context)
        db = LocalRecipeDatabase.getInstance(context)
    }

    @Test
    fun testBackupRestore() {
        insertRecipeWithIng()
            assert(runBlocking { backupUserData.restoreBackup() })
    }

    @After
    fun clearUp() {
        db.clearAllTables()
    }

    fun insertRecipeWithIng() {
        val recipe = Recipe(
            1, "Ensalada", 20, listOf(DishType.MEAL),
            DietType.VEGETARIAN, "Cocinar", Uri.parse("dummyUri"),
            "Lorem ipsum")
        val ing = Ingredient(1, "Lechuga", "hojas", 3.0)
        val cross = Recipe_Ing(1, 1)

        db.dao().apply {
            insertRecipe(recipe)
            insertIngredient(ing)
            insertRecipeIngCross(cross)
        }
    }
}