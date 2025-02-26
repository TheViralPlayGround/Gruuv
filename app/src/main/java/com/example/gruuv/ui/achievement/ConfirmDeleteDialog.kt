package com.example.gruuv.ui.achievement

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.gruuv.model.Achievement

@Composable
fun ConfirmDeleteDialog(
    achievement: Achievement,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Delete Achievement") },
        text = { Text("Are you sure you want to delete '${achievement.title}'?") }
    )
}
