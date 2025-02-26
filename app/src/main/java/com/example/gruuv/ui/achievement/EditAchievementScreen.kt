package com.example.gruuv.ui.achievement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gruuv.ui.dashboard.AchievementViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun EditAchievementScreen(
    navController: NavController,
    achievementId: String,
    viewModel: AchievementViewModel = getViewModel()
) {
    // Fetch the achievement using the ID
    val achievement = viewModel.getAchievementById(achievementId)

    // Check if achievement is null (loading state)
    if (achievement == null) {
        // Show a loading message or spinner
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Loading achievement...", style = MaterialTheme.typography.bodyLarge)
        }
        return // Exit early while the data is loading
    }

    // Use rememberSaveable to initialize title and description
    val title = rememberSaveable { mutableStateOf(achievement?.title ?: "") }
    val description = rememberSaveable { mutableStateOf(achievement?.description ?: "") }
    val showDeleteDialog = remember { mutableStateOf(false) } // MutableState for dialog visibility

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Title Input
        TextField(
            value = title.value,
            onValueChange = { title.value = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Description Input
        TextField(
            value = description.value,
            onValueChange = { description.value = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Save Changes Button
        Button(
            onClick = {
                // Update the achievement with the modified title and description
                viewModel.updateAchievement(achievementId, title.value, description.value)
                navController.navigate("dashboard") // Navigate back to the dashboard
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Delete Button
        Button(
            onClick = { showDeleteDialog.value = true }, // Show delete confirmation dialog
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Delete Achievement")
        }
    }

    // Confirmation Dialog for Deletion
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            confirmButton = {
                TextButton(onClick = {
                    // Delete the achievement
                    viewModel.deleteAchievement(achievementId)
                    navController.navigate("dashboard") // Navigate back to the dashboard
                    showDeleteDialog.value = false // Close the dialog
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete Achievement") },
            text = { Text("Are you sure you want to delete this achievement?") }
        )
    }
}
