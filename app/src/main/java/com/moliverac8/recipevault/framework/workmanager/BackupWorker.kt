package com.moliverac8.recipevault.framework.workmanager

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class BackupWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val backup: BackupUserData = BackupUserData(applicationContext)

    override suspend fun doWork(): Result =
        if (backup.doBackup(context)) Result.success()
        else Result.failure()
}

class BackupWorkerManager(context: Context) {

    private var manager: WorkManager = WorkManager.getInstance(context)

    fun launchWorker(interval: Long) =
        manager.enqueue(PeriodicWorkRequestBuilder<BackupWorker>(interval, TimeUnit.DAYS).build())

}