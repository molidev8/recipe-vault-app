package com.moliverac8.recipevault.framework.workmanager

import android.content.Context
import android.os.Environment
import android.util.Log
import com.dropbox.core.DbxException
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.android.Auth
import com.dropbox.core.http.OkHttp3Requestor
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.WriteMode
import com.moliverac8.recipevault.BACKUP
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

private const val API_KEY = "1an6pdc5pzzppws"

class DropboxManager(private val context: Context) {

    private val config: DbxRequestConfig = DbxRequestConfig.newBuilder("recipe-vault")
        .withHttpRequestor(OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
        .build()
    private var client: DbxClientV2? = null

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

    fun uploadFile(input: FileInputStream): Boolean {
        return try {
            client?.files()?.uploadBuilder("/recipe-vault-backup.zip")
                ?.withMode(WriteMode.OVERWRITE)
                ?.uploadAndFinish(input)
            true
        } catch (e: DbxException) {
            throw DbxException("Error uploading files ${e.localizedMessage}")
        }
    }

    fun downloadFile(output: FileOutputStream) {
        try {
            client?.files()?.download("/recipe-vault-backup.zip")?.download(output as OutputStream)
        } catch (e: DbxException) {
            throw DbxException("Error downloading files ${e.localizedMessage}")
        }
    }

    fun getDateOfLastBackup(): Date? {
        return client?.files()?.download("/recipe-vault-backup.zip")?.result?.serverModified
    }

    // Devuelve el tamaño de la copia en bytes
    fun getSizeOfLastBackup(): Long {
        return client?.files()?.download("/recipe-vault-backup.zip")?.result?.size ?: 0L
    }
}