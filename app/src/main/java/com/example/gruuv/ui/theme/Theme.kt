package com.example.gruuv.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Example custom colors
private val SlateBlue = Color(0xFF6A5ACD)
private val MediumAquamarine = Color(0xFF66CDAA)
private val Orchid = Color(0xFFDA70D6)

// Neutral backgrounds & surfaces
private val LightBackground = Color(0xFFF9F8FD) // soft off-white
private val DarkBackground = Color(0xFF1C1B1F)  // near-black
private val LightSurface = Color(0xFFF9F8FD)
private val DarkSurface = Color(0xFF2A2930)

// Example for “on” colors
private val OnLightBackground = Color(0xFF1C1B1F) // black-ish for light BG
private val OnDarkBackground = Color(0xFFE6E1E5) // near-white for dark BG

// Light color scheme
val LightColorScheme = lightColorScheme(
    primary = SlateBlue,
    onPrimary = Color.White,
    secondary = MediumAquamarine,
    onSecondary = Color.Black,
    tertiary = Orchid,
    onTertiary = Color.White,

    background = LightBackground,
    onBackground = OnLightBackground,
    surface = LightSurface,
    onSurface = OnLightBackground,

    // Optional container roles:
    primaryContainer = SlateBlue.copy(alpha = 0.85f), // Slightly lighter/darker
    onPrimaryContainer = Color.White,
    secondaryContainer = MediumAquamarine.copy(alpha = 0.85f),
    onSecondaryContainer = Color.Black,

    error = Color(0xFFD76A03), // Rust Orange
    onError = Color.White
)

// Dark color scheme
val DarkColorScheme = darkColorScheme(
    primary = SlateBlue.copy(alpha = 0.7f), // A bit muted
    onPrimary = Color(0xFF201A37),         // Very dark purple or near-black
    secondary = MediumAquamarine.copy(alpha = 0.6f),
    onSecondary = Color.Black,
    tertiary = Orchid.copy(alpha = 0.75f),
    onTertiary = Color.Black,

    background = DarkBackground,
    onBackground = OnDarkBackground,
    surface = DarkSurface,
    onSurface = OnDarkBackground,

    primaryContainer = SlateBlue.copy(alpha = 0.4f),
    onPrimaryContainer = Color(0xFFD1D1D1),
    secondaryContainer = MediumAquamarine.copy(alpha = 0.4f),
    onSecondaryContainer = Color.Black,

    error = Color(0xFFD76A03),
    onError = Color.Black
)

@Composable
fun GruuvTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
