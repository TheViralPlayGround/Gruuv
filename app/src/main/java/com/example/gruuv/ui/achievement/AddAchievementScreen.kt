package com.example.gruuv.ui.achievement

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gruuv.ui.dashboard.AchievementViewModel
import org.koin.androidx.compose.getViewModel
import java.util.UUID

@Composable
fun AddAchievementScreen(
    navController: NavController,
    viewModel: AchievementViewModel = getViewModel()
) {
    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TextField(
            value = title.value, // Access the value property
            onValueChange = { title.value = it }, // Update the value property
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = description.value, // Access the value property
            onValueChange = { description.value = it }, // Update the value property
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                if (title.value.isNotBlank() && description.value.isNotBlank()) {
                    val id = UUID.randomUUID().toString()
                    viewModel.addAchievement(id, title.value, description.value)
                    navController.navigate("dashboard")
                    Toast.makeText(context, "Achievement added successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Achievement")
        }
    }
}
