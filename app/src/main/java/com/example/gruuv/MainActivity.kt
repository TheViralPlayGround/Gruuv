package com.example.gruuv

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.gruuv.ui.GruuvApp
import com.example.gruuv.ui.dashboard.AchievementViewModel
import com.example.gruuv.utils.uploadQuotesToFirestore
import com.example.gruuv.workers.DailyResetWorker
import com.example.gruuv.workers.scheduleDailyReminder

class MainActivity : ComponentActivity() {
    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("MainActivity", "Notification permission granted")
                scheduleDailyReminder(applicationContext)
            } else {
                Log.e("MainActivity", "Notification permission denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uploadQuotesToFirestore(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            scheduleDailyReminder(applicationContext)
        }

        setContent {
            GruuvApp(mainActivity = this)
        }
    }

    fun triggerDailyResetWorker(viewModel: AchievementViewModel) {
        val workRequest = OneTimeWorkRequestBuilder<DailyResetWorker>().build()
        WorkManager.getInstance(this).enqueue(workRequest)

        // Add a listener to reload data after reset
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.id).observe(this) { workInfo ->
            if (workInfo != null && workInfo.state.isFinished) {
                // Reload achievements after reset is complete
                viewModel.loadAchievements()
                Log.d("MainActivity", "Achievements reloaded after reset")
            }
        }
    }
}
