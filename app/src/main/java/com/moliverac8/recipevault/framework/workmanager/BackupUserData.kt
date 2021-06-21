package com.moliverac8.recipevault.framework.workmanager

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dropbox.core.oauth.DbxCredential
import com.moliverac8.recipevault.BACKUP
import com.moliverac8.recipevault.framework.room.DATABASE_NAME
import com.moliverac8.recipevault.framework.room.LocalRecipeDatabase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Class to manage and execute all the tasks related with the backup/restore of the user data
 *
 * @property context context in which the manager is loaded
 * @constructor Creates a [BackupUserData] to access the backend
 */
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
     * Initiates the backup process
     * @return true in case the upload went well, false otherwise
     */
    fun doBackup(): Boolean = try {
        LocalRecipeDatabase.getInstance(context).close()

        val output = FileOutputStream(backupDir?.path + "/recipe-vault-backup.zip")
        zipFiles(output, context.getDatabasePath(DATABASE_NAME), photosDir)
        val zipFile = FileInputStream(
            backupDir?.listFiles()?.find { it.name == "recipe-vault-backup.zip" })
        uploadToDropbox(zipFile)
    } catch (e: FileNotFoundException) {
        throw Exception()
    } catch (e: Exception) {
        throw Exception()
    }

    /**
     * Initiates the restore process
     */
    fun restoreBackup() = try {// Cierro la base de datos antes de realizar operaciones
        LocalRecipeDatabase.getInstance(context).close()
        downloadFromDropbox(FileOutputStream(File(backupDir, "recipe-vault-backup.zip")))
        unzipBackupFiles()
        restoreRoomDatabase()
        LocalRecipeDatabase.getInstance(context)
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

    /**
     * Compress all the files for the backup into a .zip file
     * @param output A [FileOutputStream] where the zip file is going to be saved
     * @param input The files that are going to be compressed into the .zip file
     */
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
                        if (file.extension == "jpg") addZipEntry(file)
                    }
                }
            }
            zipOutput.close()
        } catch (e: IllegalArgumentException) {
            throw Exception()
        } catch (e: ZipException) {
            throw Exception()
        } catch (e: IOException) {
            throw Exception()
        }
    }

    /**
     * Uploads a file into the user's Dropbox
     * @param input A [FileInputStream] to the file that is going to be uploaded
     * @return true in case the upload went well, false otherwise
     */
    private fun uploadToDropbox(input: FileInputStream): Boolean = dropboxManager.uploadFile(input)

    /**
     * Restores the local Room database with the backup downloaded from Dropbox
     */
    private fun restoreRoomDatabase() {

        val dbPath = context.getDatabasePath(DATABASE_NAME)
        val backupFile = backupDir?.listFiles()?.find { it.name == "recipes_db" }

        try {
            backupFile?.copyTo(dbPath, true)
        } catch (e: Exception) {
        }
    }

    /**
     * Downloads the backup store in the Dropbox's user
     * @param output A [FileOutputStream] where the files is going to be downloaded
     */
    private fun downloadFromDropbox(output: FileOutputStream) = dropboxManager.downloadFile(output)


    /**
     * Decompresses the .zip file with the files for the restoration process into the Documents folder
     */
    private fun unzipBackupFiles() {
        val input =
            ZipFile(backupDir?.path + "/recipe-vault-backup.zip")

        val entries: Enumeration<out ZipEntry?> = input.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            val output = entry?.run {
                if (name == "recipes_db")
                    BufferedOutputStream(FileOutputStream(File(backupDir, name)))
                else
                    BufferedOutputStream(FileOutputStream(File(photosDir, name)))
            }

            val data = input.getInputStream(entry)
            val bytes = data.readBytes()
            output?.write(bytes)
            data.close()
            output?.close()
        }
    }

    /**
     * Retrieves the size of the last uploaded backup
     * @return the number of bytes of the last backup
     */
    fun getBackupSize(): Long {
        val file = File(backupDir?.path + "/recipe-vault-backup.zip")
        return if (file.exists()) {
            file.length()
        } else {
            0
        }
    }

}