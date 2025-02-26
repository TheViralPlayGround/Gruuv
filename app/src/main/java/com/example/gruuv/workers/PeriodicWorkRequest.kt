package com.example.gruuv.workers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun scheduleDailyReset(context: Context) {
    val initialDelay = calculateDelayToTime(0, 0)
    val resetRequest = PeriodicWorkRequestBuilder<DailyResetWorker>(
        1, TimeUnit.DAYS
    )
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "DailyResetWorker",
        ExistingPeriodicWorkPolicy.REPLACE,
        resetRequest
    )
}
