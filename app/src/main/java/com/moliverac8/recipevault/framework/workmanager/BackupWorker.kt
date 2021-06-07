package com.moliverac8.recipevault.framework.workmanager

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class BackupWorker(
    context: Context,
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
        manager.enqueue(
            PeriodicWorkRequestBuilder<BackupWorker>(interval, TimeUnit.DAYS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
        )

}