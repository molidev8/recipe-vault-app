package com.moliverac8.recipevault.framework.workmanager

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.dropbox.core.android.Auth
import com.dropbox.core.oauth.DbxCredential
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class BackupWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val backup: BackupUserData = BackupUserData(applicationContext)


    override suspend fun doWork(): Result {
        return if (backup.doBackup()) Result.success()
        else Result.failure()
    }
}

class BackupWorkerManager(context: Context) {

    private var manager: WorkManager = WorkManager.getInstance(context)

    fun launchWorker(interval: Long) =
        manager.enqueue(PeriodicWorkRequestBuilder<BackupWorker>(interval, TimeUnit.DAYS).build())

}