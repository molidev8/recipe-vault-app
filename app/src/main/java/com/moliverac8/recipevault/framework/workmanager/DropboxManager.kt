package com.moliverac8.recipevault.framework.workmanager

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.android.Auth
import com.dropbox.core.http.OkHttp3Requestor
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream

private val API_KEY = "1an6pdc5pzzppws"

// Este token luego será dinámico
private val ACCESS_TOKEN =
    "sl.AvSLUmlHmavuHJxDj_Wh0l0k_BlC7AScsh4cKe53XYIOZenuzqT5HV7jM47H9T1WzSu-UIsnE8NWmS8BfozzZ3W8tVFGKSItYdMUWOuNYLXpZfDY4kcygmmSuayOmPfL3c0uUtE"

class DropboxManager(private val context: Context) {

    private val config: DbxRequestConfig = DbxRequestConfig.newBuilder("recipe-vault")
        .withHttpRequestor(OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
        .build()
    private var client: DbxClientV2? = null
    private val backupDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    private var prefs: SharedPreferences =
        context.getSharedPreferences("recipe-vault", Context.MODE_PRIVATE)

    //Descomentar este bloque en testing
    init {
        client = DbxClientV2(config, ACCESS_TOKEN)
    }

    fun startOAuth2Authentication() {
        Auth.startOAuth2PKCE(
            context, API_KEY, config,
            listOf("account_info.read", "files.content.write")
        )
    }

    fun initDropboxClient() {
        val dbxCredential = Auth.getDbxCredential()
        val credential = DbxCredential(
            dbxCredential.accessToken,
            -1L,
            dbxCredential.refreshToken,
            dbxCredential.appKey
        )
        if (client == null) {
            client = DbxClientV2(config, credential)
        }
    }

    fun uploadBackup() {
        val input = FileInputStream(backupDir?.listFiles()?.get(1))
        client?.files()?.upload("/recipe-vault-backup.zip")?.uploadAndFinish(input)
    }

    fun downloadBackup() {
        val file = File.createTempFile("recipeVault", "backup", backupDir)
        val output = FileOutputStream(file) as OutputStream
        client?.files()?.download("/recipe-vault-backup.zip")?.download(output)
    }
}