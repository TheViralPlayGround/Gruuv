package com.example.gruuv.ui.insights

import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gruuv.model.Achievement
import com.example.gruuv.ui.dashboard.AchievementViewModel
import com.example.gruuv.ui.graph.LineChartMPAndroid
import com.github.mikephil.charting.data.Entry
import org.koin.androidx.compose.getViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun InsightsScreen(achievementId: String) {
    val viewModel: AchievementViewModel = getViewModel()
    val achievements = viewModel.achievements.collectAsState() // Observe achievements
    val achievement = achievements.value.find { it.id == achievementId }

    Column(modifier = Modifier.padding(16.dp)) {
        // Show loading state if achievements are not yet loaded
        if (achievements.value.isEmpty()) {
            Text(text = "Loading achievements...", style = MaterialTheme.typography.bodyMedium)
        } else {
            // Display the title of the Insights screen
            Text(
                text = "Daily Effort Trend 🚀",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color(0xFF389383),
                    fontWeight = FontWeight.Bold
                )
            )

            if (achievement != null) {
                // Display the effort graph header
                val effortHistory = achievement.effortHistory.toSortedMap()
                if (effortHistory.size <= 1) {
                    Text(
                        text = "As you add more dates you will see trends.",
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }


                // Convert effortHistory Map into a sorted list of entries
                val sortedEffortHistory = achievement.effortHistory
                    .toSortedMap()
                    .entries
                    .toList()
                    .mapIndexed { index, entry ->
                        Entry(index.toFloat(), entry.value.toFloat()) // Map to chart entries
                    }

                // Extract labels (dates) for the X-axis
                val labels = achievement.effortHistory
                    .toSortedMap()
                    .keys
                    .distinct()
                    .map { formatDate(it) } // Ensure unique formatted dates

                // Render the line chart
                LineChartMPAndroid(
                    data = sortedEffortHistory,
                    labels = labels,
                    legendLabel = "Achievement Trends",
                    tooltipText = { pointIndex -> "Effort: pointIndex" }
                    )
            } else {
                // Display error if achievement not found
                Text(text = "Achievement not found.")
            }
        }

        Text(
            text = "Track your daily effort consistency",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Gray
            ),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

// Function to format date strings for better readability
private fun formatDate(date: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    return try {
        val parsedDate = inputFormat.parse(date)
        outputFormat.format(parsedDate ?: date)
    } catch (e: Exception) {
        date // Return the original if parsing fails
    }
}

// Export progress to a CSV file
private fun exportProgress(context: android.content.Context, achievement: Achievement) {
    val fileName = "${achievement.title}_progress.csv"
    val csvContent = StringBuilder("Date,Effort\n")
    achievement.effortHistory.toSortedMap().forEach { (date, effort) ->
        csvContent.append("$date,$effort\n")
    }

    // Save CSV file to device storage (e.g., Downloads folder)
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
    file.writeText(csvContent.toString())

    Toast.makeText(context, "Exported to ${file.absolutePath}", Toast.LENGTH_LONG).show()
}

// Share progress as plain text
private fun shareProgress(context: android.content.Context, achievement: Achievement) {
    val progressSummary = StringBuilder("${achievement.title} Progress:\n")
    achievement.effortHistory.toSortedMap().forEach { (date, effort) ->
        progressSummary.append("$date: $effort points\n")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, progressSummary.toString())
    }

    context.startActivity(Intent.createChooser(intent, "Share Progress"))
}
