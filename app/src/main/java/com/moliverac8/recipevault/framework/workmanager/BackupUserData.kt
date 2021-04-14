package com.moliverac8.recipevault.framework.workmanager

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import com.moliverac8.recipevault.framework.room.DATABASE_NAME
import com.moliverac8.recipevault.framework.room.LocalRecipeDatabase
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception
import kotlinx.coroutines.Dispatchers.IO


private const val BACKUP_REQUEST_CODE = 1
private const val BACKUP_NAME = "backup"

class BackupUserData() {

    fun onBackupRequest() {
        val mimeType = arrayOf("application/json")
//        val fileName =
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/zip"
            putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
//            putExtra(Intent.EXTRA_TITLE, fileName)
        }
    }

    suspend fun backupRoomDatabase(context: Context): Boolean =
        withContext(IO) {
            // Me aseguro de que no ocurren transacciones
            LocalRecipeDatabase.getInstance(context).close()

            val dbPath = context.getDatabasePath(DATABASE_NAME)
            val backupDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

            // Si ya existe una copia anterior, la elimino
            val tempFile = File.createTempFile("recipeVault", BACKUP_NAME, backupDir)
            if (tempFile.exists()) tempFile.delete()

            try {
                val dbFile = File(dbPath.toURI())
                dbFile.copyTo(tempFile)
                return@withContext true
            } catch (e: Exception) {
                Log.d(com.moliverac8.recipevault.IO, "Error al copiar fichero $e")
                return@withContext false
            }
        }

}