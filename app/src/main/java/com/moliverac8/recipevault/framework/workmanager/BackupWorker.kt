package com.moliverac8.recipevault.framework.workmanager

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class BackupWorker(private val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    private val backupObject = BackupUserData()

    override suspend fun doWork(): Result =
        if (backupObject.backupRoomDatabase(context))  Result.success()
        else Result.failure()
}

class BackupWorkerManager(private val context: Context) {

    private var manager: WorkManager = WorkManager.getInstance(context)

    fun launchWorker(interval: Long) = PeriodicWorkRequestBuilder<BackupWorker>(interval, TimeUnit.DAYS).build()

}