package com.moliverac8.recipevault.ui.account

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moliverac8.recipevault.BACKUP_SIZE
import com.moliverac8.recipevault.CLOUD_BACKUP_TIME
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
import kotlinx.coroutines.withContext
import javax.inject.Inject

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
    private val _doingBackup = MutableLiveData(false)
    val doingBackup: LiveData<Boolean>
        get() = _doingBackup

    private val _doingRestore = MutableLiveData<Boolean>()
    val doingRestore: LiveData<Boolean>
        get() = _doingRestore

    private val _isFinished = MutableLiveData<Long>()
    val isFinished: LiveData<Long>
        get() = _isFinished

    private val _firstTimeSetup = MutableLiveData<Boolean>()
    val firstTimeSetup: LiveData<Boolean>
        get() = _firstTimeSetup

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BackupEntryPoint {
        fun dropboxManager(): DropboxManager
    }

    fun loginToDropbox() = viewModelScope.launch { dropboxManager.startOAuth2Authentication() }

    fun makeBackup(coroutineExceptionHandler: CoroutineExceptionHandler) =
        CoroutineScope(IO + coroutineExceptionHandler).launch {
            _doingBackup.postValue(true)
            backupUserData.doBackup()
            _doingBackup.postValue(false)
            _isFinished.postValue(backupUserData.getBackupSize())
        }

    fun restoreBackup(coroutineExceptionHandler: CoroutineExceptionHandler) =
        CoroutineScope(IO + coroutineExceptionHandler).launch {
            _doingRestore.postValue(true)
            backupUserData.restoreBackup()
            _doingRestore.postValue(false)
        }

    fun saveSizeOfCloudBackup(prefs: SharedPreferences) {
        viewModelScope.launch {
            withContext(IO) {
                prefs.edit().putLong(BACKUP_SIZE, dropboxManager.getSizeOfLastBackup()).apply()
            }
        }
    }

    fun saveDateOfLastBackup(prefs: SharedPreferences) {
        viewModelScope.launch {
            withContext(IO) {
                dropboxManager.getDateOfLastBackup()?.let {
                    prefs.edit().putLong(CLOUD_BACKUP_TIME, it.time).apply()
                }
            }
            _firstTimeSetup.postValue(true)
        }
    }
}