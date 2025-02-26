package com.example.gruuv.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gruuv.MainActivity
import com.example.gruuv.ui.dashboard.AchievementViewModel

@Composable
fun DebugButton(mainActivity: MainActivity, achievementViewModel: AchievementViewModel) {
    Button(
        onClick = { mainActivity.triggerDailyResetWorker(achievementViewModel) },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Trigger Daily Reset")
    }
}
