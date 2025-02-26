package com.example.gruuv.ui.graph

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gruuv.ui.dashboard.AchievementViewModel
import com.github.mikephil.charting.data.Entry
import org.koin.androidx.compose.getViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GraphScreen(viewModel: AchievementViewModel = getViewModel()) {
    val achievements by viewModel.achievements.collectAsState()
    val timeFilter = remember { mutableStateOf("Day") }

    // Generate chart data and labels based on selected time filter
    val (chartData, labels) = when (timeFilter.value) {
        "Day" -> {
            val aggregatedEfforts = achievements.flatMap { it.effortHistory.entries }
                .groupBy { it.key }
                .mapValues { (_, entries) -> entries.map { it.value }.average().toFloat() }

            val sortedData = aggregatedEfforts.toSortedMap()
            val formattedLabels = sortedData.keys.map { formatDate(it, "yyyy-MM-dd", "MMM dd") }
            sortedData.values.mapIndexed { index, value -> Entry(index.toFloat(), value) } to formattedLabels
        }
        else -> emptyList<Entry>() to emptyList()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Title
        Text(
            text = "Daily Effort Trends",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Drop-down menu for time filter
        var expanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { expanded = true }) {
                Text(timeFilter.value)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf("Day").forEach { filter ->
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            timeFilter.value = filter
                        },
                        text = { Text(filter) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display chart or loading indicator
        if (achievements.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LineChartMPAndroid(
                data = chartData,
                labels = labels,
                legendLabel = "Achievement Trends",
                tooltipText = { pointIndex -> "Effort: ${chartData[pointIndex].y}" }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Footer
        Text(
            text = "Analyze how you're doing across all your achievements.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

private fun formatDate(date: String, inputPattern: String, outputPattern: String): String {
    val inputFormat = SimpleDateFormat(inputPattern, Locale.getDefault())
    val outputFormat = SimpleDateFormat(outputPattern, Locale.getDefault())
    return try {
        val parsedDate = inputFormat.parse(date)
        outputFormat.format(parsedDate ?: date)
    } catch (e: Exception) {
        date // Return original if parsing fails
    }
}
