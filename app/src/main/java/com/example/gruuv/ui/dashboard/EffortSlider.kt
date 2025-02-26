package com.example.gruuv.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gruuv.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EffortSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 1f..10f,
    steps: Int = 9
) {
    val tooltipValue = remember { mutableStateOf(value.toInt()) }
    val haptic = LocalHapticFeedback.current

    // Determine the Gruuvy icon based on slider value
    val gruvvyIcon = when (value.toInt()) {
        in 1..4 -> R.drawable.sad_gruuv
        in 5..7 -> R.drawable.gruokay
        else -> R.drawable.happygruuv
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        Slider(
            value = value,
            onValueChange = {
                tooltipValue.value = it.toInt()
                onValueChange(it)
                if (it.toInt() == 1) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            },
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(72.dp)
                .padding(horizontal = 24.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color.Transparent,
                activeTrackColor = Color.Blue,
                inactiveTrackColor = Color.Gray
            ),
            thumb = {
                // Use Gruuvy icon as the thumb
                Icon(
                    painter = painterResource(id = gruvvyIcon),
                    contentDescription = "Gruuvy Icon",
                    modifier = Modifier.size(48.dp), // Adjust size as needed
                    tint = Color.Unspecified
                )
            }
        )
    }
}
