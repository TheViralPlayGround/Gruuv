package com.example.gruuv

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.gruuv.di.appModule
import com.example.gruuv.workers.scheduleDailyReminder
import com.example.gruuv.workers.scheduleDailyReset
import com.example.gruuv.workers.scheduleRandomQuoteNotification
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GruuvApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        scheduleDailyReminder(this)
        scheduleDailyReset(this)
        scheduleRandomQuoteNotification(this)
        startKoin {
            androidContext(this@GruuvApplication)
            modules(appModule)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val randomQuoteChannel = NotificationChannel(
                "random_quote_channel",
                "Random Quotes",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for random motivational quotes"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(randomQuoteChannel)
        }


    }
}
