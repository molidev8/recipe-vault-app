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
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.Strings
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

private var API_KEY = Strings.get(R.string.dropbox_key)

/**
 * Manages all the interaction with the Dropbox backend
 *
 * @property context context in which the manager is loaded
 * @constructor Creates a [DropboxManager] to access the backend
 */
class DropboxManager(private val context: Context) {

    private val config: DbxRequestConfig = DbxRequestConfig.newBuilder("recipe-vault")
        .withHttpRequestor(OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
        .build()
    private var client: DbxClientV2? = null

    /**
     * Launches the browser for the user to log in to Dropbox and get the credentials
     */
    fun startOAuth2Authentication() {
        Auth.startOAuth2PKCE(
            context, API_KEY, config,
            listOf("account_info.read", "files.content.write", "files.content.read")
        )
    }

    /**
     * Loads the Dropbox [DbxClientV2] to access backend functions
     * @param credential Represents the user credentials
     */
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

    /**
     * Uploads a file into the user's Dropbox
     * @param input A [FileInputStream] to the file that is going to be uploaded
     * @return true in case the upload went well, false otherwise
     */
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

    /**
     * Downloads a file from the user's Dropbox
     * @param output A [FileOutputStream] to the path where is going to be downloaded
     */
    fun downloadFile(output: FileOutputStream) {
        try {
            client?.files()?.download("/recipe-vault-backup.zip")?.download(output as OutputStream)
        } catch (e: DbxException) {
            throw DbxException("Error downloading files ${e.localizedMessage}")
        }
    }

    /**
     * Retrieves the date of the last backup uploaded to the user's Dropbox
     * @returns a [Date] of the last upload
     */
    fun getDateOfLastBackup(): Date? {
        return client?.files()?.download("/recipe-vault-backup.zip")?.result?.serverModified
    }

    /**
     * Retrieves the size of the last uploaded backup
     * @return the number of bytes of the last backup
     */
    fun getSizeOfLastBackup(): Long {
        return client?.files()?.download("/recipe-vault-backup.zip")?.result?.size ?: 0L
    }
}