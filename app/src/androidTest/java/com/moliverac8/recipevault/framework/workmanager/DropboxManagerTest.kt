package com.moliverac8.recipevault.framework.workmanager

import androidx.test.core.app.ApplicationProvider
import com.dropbox.core.oauth.DbxCredential
import org.junit.Before
import org.junit.Test

class DropboxManagerTest {

    // Solicitar nuevo token en caso de error (caduca a las 4h)
    private val accessToken =
        "sl.AyGBWxJimkgL7rllZfodAWhvnmoEhoDhctW124cgXRCbcMPCo-mqZuHjE7o-tD4R9wpELunkvG-EjGuED6hQEx-J8w00dVlASXmbb7xHQCZ7ziC4nfmOt10TNU5UjqS12RurAV4"
    private lateinit var dropboxManager: DropboxManager

    @Before
    fun loginToDropbox() {
        dropboxManager = DropboxManager(ApplicationProvider.getApplicationContext())
        dropboxManager.initDropboxClient(DbxCredential(accessToken))
    }
}