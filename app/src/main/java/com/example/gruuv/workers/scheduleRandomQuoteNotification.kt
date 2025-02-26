package com.example.gruuv.workers

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

fun scheduleRandomQuoteNotification(context: Context) {
    val workManager = WorkManager.getInstance(context)
    val workName = "RandomQuoteNotification"

    // Cancel any existing work to prevent duplicates
    workManager.cancelUniqueWork(workName)

    // Calculate the delay until 11 AM EST
    val initialDelay = calculateInitialDelayFor11AM()

    // Create constraints for the worker
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    // Schedule a one-time work request to trigger the periodic worker at 11 AM
    val oneTimeWorkRequest = OneTimeWorkRequestBuilder<SetupPeriodicQuoteWorker>()
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        .setConstraints(constraints)
        .build()

    workManager.enqueueUniqueWork(
        workName,
        ExistingWorkPolicy.REPLACE,
        oneTimeWorkRequest
    )
}

private fun calculateInitialDelayFor11AM(): Long {
    val currentTime = Calendar.getInstance()
    val targetTime = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 11) // 11 AM
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        // If the target time is in the past for today, schedule for tomorrow
        if (before(currentTime)) {
            add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    return targetTime.timeInMillis - currentTime.timeInMillis
}

class SetupPeriodicQuoteWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val workManager = WorkManager.getInstance(applicationContext)

        // Define constraints for periodic work
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Schedule periodic work to run every 24 hours at 11 AM
        val periodicWorkRequest = PeriodicWorkRequestBuilder<RandomQuoteNotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "PeriodicRandomQuoteNotification",
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWorkRequest
        )

        return Result.success()
    }
}
