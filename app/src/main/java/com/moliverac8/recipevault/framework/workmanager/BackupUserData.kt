package com.moliverac8.recipevault.framework.workmanager

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import com.moliverac8.recipevault.framework.room.DATABASE_NAME
import com.moliverac8.recipevault.framework.room.LocalRecipeDatabase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.withContext
import java.lang.Exception
import kotlinx.coroutines.Dispatchers.IO
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton


private const val BACKUP_REQUEST_CODE = 1
private const val BACKUP_NAME = "backup"

class BackupUserData(private val context: Context) {

    val dropboxManager: DropboxManager by lazy {
        EntryPointAccessors.fromApplication(
            context,
            BackupEntryPoint::class.java
        ).dropboxManager()
    }


    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BackupEntryPoint {
        fun dropboxManager(): DropboxManager
    }

    suspend fun doBackup(context: Context): Boolean = withContext(IO) {
        val dbFile = backupRoomDatabase(context)
        dbFile?.let {
            zipBackupFiles(context, dbFile)
            uploadToDropbox()
            return@withContext true
        }
        return@withContext false
    }

    suspend fun restoreBackup(context: Context): Boolean = withContext(IO) {
        return@withContext true
    }

    private fun backupRoomDatabase(context: Context): File? {
        // Me aseguro de que no ocurren transacciones
        LocalRecipeDatabase.getInstance(context).close()

        val dbPath = context.getDatabasePath(DATABASE_NAME)
        val backupDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

        // Si ya existe una copia anterior, la elimino
        val tempFile = File.createTempFile("recipeVault", BACKUP_NAME, backupDir)
        if (tempFile.exists()) tempFile.delete()

        return try {
            val dbFile = File(dbPath.toURI())
            dbFile.copyTo(tempFile)
            dbFile
        } catch (e: Exception) {
            Log.d(com.moliverac8.recipevault.IO, "Error al hacer backup $e")
            null
        }
    }

    private fun zipBackupFiles(context: Context, db: File) {
        val photosDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val backupDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val output =
            ZipOutputStream(BufferedOutputStream(FileOutputStream(backupDir?.path + "/backup.zip")))

        photosDir?.listFiles()?.forEach { file ->
            val entry = ZipEntry(file.name)
            val input = BufferedInputStream(FileInputStream(file))
            output.putNextEntry(entry)
            input.copyTo(output)
            input.close()
            output.closeEntry()
        }

        val entry = ZipEntry(db.name)
        val input = BufferedInputStream(FileInputStream(db))
        output.putNextEntry(entry)
        input.copyTo(output)
        output.closeEntry()
        output.close()
    }

    private fun uploadToDropbox() {
        //Comentar esta linea en testing
//        dropboxManager.initDropboxClient()
        dropboxManager.uploadBackup()
    }

    private fun restoreRoomDatabase(context: Context)  {
        LocalRecipeDatabase.getInstance(context).close()

        val dbPath = context.getDatabasePath(DATABASE_NAME)
        val backupDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val backupFile = backupDir?.listFiles()?.first()

        try {
            val dbFile = File(dbPath.toURI())
            backupFile?.copyTo(dbFile)
        } catch (e: Exception) {
            Log.d(com.moliverac8.recipevault.IO, "Error al restaurar fichero $e")
        }
    }

    private fun downloadFromDropbox() {
        //dropboxManager.initDropboxClient()
        dropboxManager.downloadBackup()
    }

    private fun unzipBackupFiles() {

    }

}