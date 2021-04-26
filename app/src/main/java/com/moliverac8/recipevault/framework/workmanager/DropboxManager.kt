package com.moliverac8.recipevault.framework.workmanager

import android.content.Context
import android.os.Environment
import android.util.Log
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.android.Auth
import com.dropbox.core.http.OkHttp3Requestor
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import com.moliverac8.recipevault.BACKUP
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream

private const val API_KEY = "1an6pdc5pzzppws"

class DropboxManager(private val context: Context) {

    private val config: DbxRequestConfig = DbxRequestConfig.newBuilder("recipe-vault")
        .withHttpRequestor(OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
        .build()
    private var client: DbxClientV2? = null
    private val backupDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

    fun startOAuth2Authentication() {
        Auth.startOAuth2PKCE(
            context, API_KEY, config,
            listOf("account_info.read", "files.content.write", "files.content.read")
        )
    }

    fun initDropboxClient(credential: DbxCredential) {
        val newCredential = DbxCredential(
            credential.accessToken,
            -1L,
            credential.refreshToken,
            credential.appKey
        )
        if (client == null) {
            client = DbxClientV2(config, newCredential)
        }
    }

    fun uploadBackup(): Boolean {
        return try {
            val input = FileInputStream(backupDir?.listFiles()?.find { it.name == "recipe-vault-backup.zip" })
            val result = client?.files()?.deleteV2("/recipe-vault-backup.zip")
            Log.d(BACKUP, "Eliminando copia en Dropbox ${result.toString()}")
            client?.files()?.upload("/recipe-vault-backup.zip")
                ?.uploadAndFinish(input)
            true
        } catch (e: Exception) {
            Log.d(BACKUP, "Error al subir el backup ${e.localizedMessage}")
            false
        }
    }

    fun downloadBackup() {
        val output = FileOutputStream(File(backupDir, "recipe-vault-backup.zip")) as OutputStream
        client?.files()?.download("/recipe-vault-backup.zip")?.download(output)
    }
}