package com.moliverac8.recipevault.framework.workmanager

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dropbox.core.oauth.DbxCredential
import com.moliverac8.recipevault.BACKUP
import com.moliverac8.recipevault.GENERAL
import com.moliverac8.recipevault.framework.room.DATABASE_NAME
import com.moliverac8.recipevault.framework.room.LocalRecipeDatabase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream


private const val BACKUP_NAME = "backup"

class BackupUserData(private val context: Context) {

    private val dropboxManager: DropboxManager by lazy {
        EntryPointAccessors.fromApplication(
            context,
            BackupEntryPoint::class.java
        ).dropboxManager()
    }

    private val photosDir: File? by lazy { context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) }
    private val backupDir: File? by lazy { context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) }
    private val serializedCredential =
        context.getSharedPreferences("recipe-vault", AppCompatActivity.MODE_PRIVATE)
            .getString("credential", null)


    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BackupEntryPoint {
        fun dropboxManager(): DropboxManager
    }

    init {
        if (serializedCredential != null) {
            val credential = DbxCredential.Reader.readFully(serializedCredential)
            dropboxManager.initDropboxClient(credential)
        }
    }

    suspend fun doBackup(): Boolean = withContext(IO) {
        LocalRecipeDatabase.getInstance(context).close()
        val dbFile = backupRoomDatabase()
        Log.d(BACKUP, "db backup $dbFile")
        dbFile?.let {
            if (zipBackupFiles(dbFile)) {
                Log.d(BACKUP, "Zip realizado correctamente")
                try {
                    dbFile.delete()
                } catch (e: Exception) {
                    Log.d(BACKUP, e.localizedMessage!!)
                }
                return@withContext uploadToDropbox()
            } else {
                dbFile.delete()
                return@withContext false
            }
        }
        return@withContext false
    }

    suspend fun restoreBackup(): Boolean = withContext(IO) {
        LocalRecipeDatabase.getInstance(context).close()
        downloadFromDropbox()
        unzipBackupFiles()
        restoreRoomDatabase()
        return@withContext true
    }

    private fun backupRoomDatabase(): File? {

        val dbPath = context.getDatabasePath(DATABASE_NAME)
        val backupDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

        // Si ya existe una copia anterior, la elimino
        val tempFile = File(backupDir, "backup.db")
        if (tempFile.exists()) tempFile.delete()

        return try {
            val dbFile = File(dbPath.toURI())
            dbFile.copyTo(tempFile)
            tempFile
        } catch (e: Exception) {
            Log.d(BACKUP, "Error al hacer backup $e")
            null
        }
    }

    private fun zipBackupFiles(db: File): Boolean {
        val output =
            ZipOutputStream(BufferedOutputStream(FileOutputStream(backupDir?.path + "/recipe-vault-backup.zip")))

        return try {
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
            true
        } catch (e: Exception) {
            Log.d(BACKUP, "Error al comprimir ${e.localizedMessage}")
            false
        }
    }

    private fun uploadToDropbox(): Boolean = dropboxManager.uploadBackup()

    private fun restoreRoomDatabase() {

        val dbPath = context.getDatabasePath(DATABASE_NAME)
        val backupDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val backupFile = backupDir?.listFiles()?.find { it.name == "backup.db" }

        try {
            backupFile?.copyTo(dbPath, true)
        } catch (e: Exception) {
            Log.d(BACKUP, "Error al restaurar fichero $e")
        }
    }

    private fun downloadFromDropbox() {
        dropboxManager.downloadBackup()
    }

    private fun unzipBackupFiles() {
        val input =
            ZipFile(backupDir?.path + "/recipe-vault-backup.zip")

        val entries: Enumeration<out ZipEntry?> = input.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            val output = entry?.run {
                if (name == "backup.db") BufferedOutputStream(
                    FileOutputStream(File(backupDir, name))
                )
                else BufferedOutputStream(FileOutputStream(File(photosDir, name)))
            }

            val data = input.getInputStream(entry)
            do {
                val byte = data.read()
                output?.write(byte)
            } while (byte != -1)
            data.close()
            output?.close()
        }
    }

}