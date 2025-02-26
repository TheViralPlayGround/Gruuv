package com.example.gruuv.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gruuv.utils.getCurrentDateKey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DailyResetWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun doWork(): Result {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId.isNullOrEmpty()) {
            Log.e("DailyResetWorker", "No logged-in user. Cannot reset achievements.")
            return Result.failure()
        }

        return try {
            resetAchievements(userId)
            Log.d("DailyResetWorker", "Daily reset completed successfully.")
            Result.success()
        } catch (e: Exception) {
            Log.e("DailyResetWorker", "Error during daily reset", e)
            Result.failure()
        }
    }

    private suspend fun resetAchievements(userId: String) {
        val achievementCollection = firestore.collection("users")
            .document(userId)
            .collection("achievements")

        try {
            val snapshot = achievementCollection.get().await()
            if (snapshot != null && !snapshot.isEmpty) {
                for (document in snapshot.documents) {
                    try {
                        val currentEffort = document.getLong("effort")?.toInt() ?: 0
                        val currentHistory = document.get("effortHistory") as? Map<String, Int> ?: emptyMap()

                        val currentDateKey = getCurrentDateKey()

                        val updatedHistory = currentHistory.toMutableMap().apply {
                            this[currentDateKey] = currentEffort
                        }
                        document.reference.update(
                            mapOf(
                                "completed" to false,
                                "effort" to 0,
                                "effortHistory" to updatedHistory
                            )
                        ).await()
                        Log.d("DailyResetWorker", "Achievement reset: ${document.id}")
                    } catch (e: Exception) {
                        Log.e("DailyResetWorker", "Failed to reset achievement: ${document.id}", e)
                    }
                }
            } else {
                Log.d("DailyResetWorker", "No achievements found to reset.")
            }

            // Ensure "Daily Effort" label is preserved
            preserveDailyEffortLabel(userId)

        } catch (e: Exception) {
            Log.e("DailyResetWorker", "Failed to fetch achievements for reset", e)
        }
    }

    private suspend fun preserveDailyEffortLabel(userId: String) {
        val userDocRef = firestore.collection("users").document(userId)
        try {
            userDocRef.update("dailyEffortLabel", "Daily Effort Trends 🚀").await()
            Log.d("DailyResetWorker", "Preserved Daily Effort label.")
        } catch (e: Exception) {
            Log.e("DailyResetWorker", "Failed to preserve Daily Effort label", e)
        }
    }
}
