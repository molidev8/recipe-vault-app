package com.moliverac8.recipevault.framework.workmanager

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dropbox.core.DbxException
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
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.*
import java.lang.IllegalArgumentException
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlin.jvm.Throws


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

    // Inicializo el cliente de Dropbox para conseguir el token del usuario

    init {
        if (serializedCredential != null) {
            val credential = DbxCredential.Reader.readFully(serializedCredential)
            dropboxManager.initDropboxClient(credential)
        }
    }

    /**
     * La función retorna un Bool para que WorkManager sepa que la tarea se ha realizado sin errores
     */
    fun doBackup(): Boolean = try {
        // Cierro la base de datos antes de realizar operaciones
        LocalRecipeDatabase.getInstance(context).close()

        val output = FileOutputStream(backupDir?.path + "/recipe-vault-backup.zip")
        zipFiles(output, context.getDatabasePath(DATABASE_NAME), photosDir)
        val zipFile = FileInputStream(
            backupDir?.listFiles()?.find { it.name == "recipe-vault-backup.zip" })
        uploadToDropbox(zipFile)
    } catch (e: FileNotFoundException) {
        Log.d(BACKUP, "File Not Found ${e.localizedMessage}")
        throw Exception()
    } catch (e: Exception) {
        throw Exception()
    }


    fun restoreBackup() = try {// Cierro la base de datos antes de realizar operaciones
        LocalRecipeDatabase.getInstance(context).close()
        downloadFromDropbox(FileOutputStream(File(backupDir, "recipe-vault-backup.zip")))
        unzipBackupFiles()
        restoreRoomDatabase()
    } catch (e: Exception) {
        throw Exception()
    }


    /*@Throws(IOException::class)
    private suspend fun backupRoomDatabase(): File = withContext(IO) {

        val dbFile = context.getDatabasePath(DATABASE_NAME)

        // Si ya existe una copia anterior, la elimino
        val tempFile = File(backupDir, "backup.db")
        if (tempFile.exists()) tempFile.delete()

        dbFile.copyTo(tempFile)
        return@withContext tempFile
    }*/

    private fun zipFiles(output: FileOutputStream, vararg input: File?) {
        val zipOutput = ZipOutputStream(BufferedOutputStream(output))

        val addZipEntry = { file: File ->
            val entry = ZipEntry(file.name)
            val zipInput = BufferedInputStream(FileInputStream(file))
            zipOutput.putNextEntry(entry)
            zipInput.run {
                copyTo(zipOutput)
                close()
            }
            zipOutput.closeEntry()
        }

        try {
            input.forEach { file ->
                file?.let {
                    if (!file.isDirectory) addZipEntry(file)
                    else file.listFiles()?.forEach { file ->
                        addZipEntry(file)
                    }
                }
            }
        } catch (e: IllegalArgumentException) {
            Log.d(BACKUP, "Filename too long ${e.localizedMessage}")
            throw Exception()
        } catch (e: ZipException) {
            Log.d(BACKUP, "Zip formatting error ${e.localizedMessage}")
            throw Exception()
        } catch (e: IOException) {
            Log.d(BACKUP, "IO error ${e.localizedMessage}")
            throw Exception()
        }
    }

    private fun uploadToDropbox(input: FileInputStream): Boolean = dropboxManager.uploadFile(input)

    private fun restoreRoomDatabase() {

        val dbPath = context.getDatabasePath(DATABASE_NAME)
        val backupFile = backupDir?.listFiles()?.find { it.name == "backup.db" }

        try {
            backupFile?.copyTo(dbPath, true)
        } catch (e: Exception) {
            Log.d(BACKUP, "Error al restaurar fichero $e")
        }
    }

    private fun downloadFromDropbox(output: FileOutputStream) = dropboxManager.downloadFile(output)

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

    // Devuelve el tamaño de la copia en bytes
    fun getBackupSize(): Long {
        val file = File(backupDir?.path + "/recipe-vault-backup.zip")
        return if (file.exists()) {
            file.totalSpace
        } else {
            0
        }
    }

}