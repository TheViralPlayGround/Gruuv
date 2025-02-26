package com.example.gruuv.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BigTextStyle
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gruuv.MainActivity
import com.example.gruuv.R
import com.example.gruuv.repository.QuoteRepository
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class RandomQuoteNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    private val quoteRepository = QuoteRepository()

    override suspend fun doWork(): Result {
        return suspendCoroutine { continuation ->
            quoteRepository.fetchRandomQuote(
                onSuccess = { quote, author, _ ->
                    sendNotification(quote, author)
                    continuation.resume(Result.success())
                },
                onFailure = { exception ->
                    Log.e("QuoteNotificationWorker", "Failed to fetch quote", exception)
                    continuation.resume(Result.failure())
                }
            )
        }
    }

    private fun sendNotification(quote: String, author: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigateTo", "dashboard")
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, "random_quote_channel")
            .setSmallIcon(R.drawable.ic_dunk_foreground) // Replace with your app's icon
            .setContentTitle("Keep your Gruuv!")
            .setContentText(quote)
            .setStyle(NotificationCompat.BigTextStyle().bigText("\"$quote\" - $author"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify((0..1000).random(), notification)
    }
}


