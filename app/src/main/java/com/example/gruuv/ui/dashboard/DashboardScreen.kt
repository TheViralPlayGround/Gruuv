package com.example.gruuv.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gruuv.MainActivity
import com.example.gruuv.ui.achievement.AchievementItem
import org.koin.androidx.compose.getViewModel

@Composable
fun DashboardScreen(viewModel: AchievementViewModel = getViewModel(), navController: NavController, mainActivity: MainActivity) {
    val achievements = viewModel.achievements.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val achievements = viewModel.achievements.collectAsState().value

            if (achievements.isEmpty()) {
                Text(
                    text = "No achievements yet. Add some!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(achievements) { achievement ->
                        AchievementItem(
                            achievement = achievement,
                            isCompleted = achievement.completed,
                            onEditClick = { selectedAchievement ->
                                navController.navigate("editAchievement/${selectedAchievement.id}")
                            },
                            onCheckedChange = { isChecked ->
                                viewModel.updateAchievementStatus(achievement.id, isChecked)
                            },
                            onEffortChange = { effort ->
                                viewModel.updateAchievementEffort(achievement.id, effort)
                            },
                            onViewInsightsClick = { selectedAchievement ->
                                navController.navigate("insights/${selectedAchievement.id}")
                            }
                        )
                    }
                }
            }
        }

        // FAB at the bottom-right corner
        FloatingActionButton(
            onClick = { navController.navigate("addAchievement") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Achievement")
        }
    }
}

@Composable
fun EffortLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LegendItem(color = Color(0xFFEF9A9A), label = "Low Effort (1-4)")
        LegendItem(color = Color(0xFFFFF59D), label = "Medium Effort (5-7)")
        LegendItem(color = Color(0xFFA5D6A7), label = "High Effort (8-10)")
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}
