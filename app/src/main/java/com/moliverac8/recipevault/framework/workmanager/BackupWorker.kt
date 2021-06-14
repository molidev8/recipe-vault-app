package com.moliverac8.recipevault.framework.workmanager

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * Creates a [CoroutineWorker] for the automated backup
 * @param context A [Context] for the worker creation
 * @param workerParams [WorkerParameters] to apply restrictions to the automated task
 * @return A [CoroutineWorker] with the work and restrictions associated
 */
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

/**
 * Launches a worker by adding it to work queue
 * @param context A [Context] for the worker manager creation
 */
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