package com.example.gruuv.workers

import android.content.Context
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun scheduleDailyReminder(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<ReminderNotificationWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(calculateDelayToTime(20, 1), TimeUnit.MILLISECONDS) // 7:13 PM
        .build()
    WorkManager.getInstance(context).enqueue(workRequest)
}
