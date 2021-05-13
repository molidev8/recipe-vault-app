package com.moliverac8.recipevault.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.dropbox.core.android.Auth
import com.dropbox.core.oauth.DbxCredential
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.moliverac8.recipevault.IO
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.framework.workmanager.BackupUserData
import com.moliverac8.recipevault.framework.workmanager.DropboxManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val REQUEST_IMAGE_CAPTURE = 1

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val dropboxManager: DropboxManager by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            BackupUserData.BackupEntryPoint::class.java
        ).dropboxManager()
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BackupEntryPoint {
        fun dropboxManager(): DropboxManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isTablet = applicationContext.resources.getBoolean(R.bool.isTablet)

        if (isTablet) {
            setContentView(R.layout.activity_main_tablet)
        } else {
            setContentView(R.layout.activity_main)
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.fragmentMaster) as NavHostFragment
            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)
            NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.navController)
            bottomNavigationView.background = null
            bottomNavigationView.menu.getItem(1).isEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()

        val prefs = getSharedPreferences("recipe-vault", MODE_PRIVATE)

        val serializedCredential = prefs.getString("credential", null)

        if (serializedCredential == null) {
            val credential = Auth.getDbxCredential()

            if (credential != null) {
                prefs.edit().putString("credential", credential.toString()).apply()
                dropboxManager.initDropboxClient(credential)
            }
        }
    }
}