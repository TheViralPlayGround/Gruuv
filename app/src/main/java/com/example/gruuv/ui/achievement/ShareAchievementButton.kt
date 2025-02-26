package com.example.gruuv.ui.achievement

import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.gruuv.model.Achievement
import com.example.gruuv.utils.saveBitmapToCache

@Composable
fun SnapAndShareAchievementButton(achievement: Achievement) {
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val imageUri = saveBitmapToCache(context, bitmap)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(
                    Intent.EXTRA_TEXT,
                    "🎉 Woohoo! I just gave an effort of ${achievement.effort} for '${achievement.title}'!" +
                            "#StayGruuvy 🚀"
                )
                putExtra(Intent.EXTRA_SUBJECT, "I just completed an achievement on Gruuv!")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share Achievement"))
        } else {
            val fallbackShareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "I just completed an achievement on Gruuv!")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "🎉 Woohoo! I just gave an effort of ${achievement.effort} for '${achievement.title}'!" +
                            "#StayGruuvy 🚀"
                )
            }
            context.startActivity(Intent.createChooser(fallbackShareIntent, "Share Achievement"))
        }
    }

    Button(
        onClick = { cameraLauncher.launch(null) },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64F5C6))
    ) {
        Text("Share achievement")
    }
}
