package com.moliverac8.recipevault.ui.account

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moliverac8.recipevault.framework.workmanager.BackupUserData
import com.moliverac8.recipevault.framework.workmanager.DropboxManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.jvm.Throws

@HiltViewModel
class AccountViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val backupUserData: BackupUserData by lazy {
        BackupUserData(context)
    }

    private val dropboxManager: DropboxManager by lazy {
        EntryPointAccessors.fromApplication(
            context,
            BackupUserData.BackupEntryPoint::class.java
        ).dropboxManager()
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BackupEntryPoint {
        fun dropboxManager(): DropboxManager
    }

    fun loginToDropbox() = viewModelScope.launch { dropboxManager.startOAuth2Authentication() }

    fun makeBackup(coroutineExceptionHandler: CoroutineExceptionHandler) =
        CoroutineScope(IO + coroutineExceptionHandler).launch { backupUserData.doBackup() }

    fun restoreBackup(coroutineExceptionHandler: CoroutineExceptionHandler) =
        CoroutineScope(IO + coroutineExceptionHandler).launch { backupUserData.restoreBackup() }
}