package com.cbse.class10thsciencenotes.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- NEW COLOR PALETTE ---

// Dark Theme Colors
val DarkMidnightBlue = Color(0xFF0A0F1A) // Deep, rich background
val DarkTextPrimary = Color(0xFFF0F8FF)    // AliceBlue, very soft off-white
val DarkTextSecondary = Color(0xFFB0C4DE)  // LightSteelBlue, for subtitles
val DarkGlass = Color(0x401A203A)         // Translucent dark blue
val DarkAccent = Color(0xFF00BFFF)        // DeepSkyBlue, for icons/glow
val DarkBorder = Color(0x80FFFFFF)       // Translucent white border

// Light Theme Colors
val LightSkyBlue = Color(0xFFF0F8FF)     // Very light background
val LightTextPrimary = Color(0xFF0D253F)   // Dark, saturated blue
val LightTextSecondary = Color(0xFF4A5568) // Greyish blue
val LightGlass = Color(0x60FFFFFF)        // Frosted white glass
val LightAccent = Color(0xFF007BFF)       // Bright, clear blue
val LightBorder = Color(0x80D0E0FF)      // Translucent light blue border

// --- COLOR SCHEMES ---

private val DarkColorScheme = darkColorScheme(
    primary = DarkAccent,
    background = DarkMidnightBlue,
    onBackground = DarkTextPrimary,
    surface = DarkGlass, // This is your glass color
    onSurface = DarkTextPrimary,
    outline = DarkBorder // This is your glass border
)

private val LightColorScheme = lightColorScheme(
    primary = LightAccent,
    background = LightSkyBlue,
    onBackground = LightTextPrimary,
    surface = LightGlass,
    onSurface = LightTextPrimary,
    outline = LightBorder
)

// --- THEME COMPOSABLE ---

@Composable
fun Class10thScienceNotesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Set status bar color
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography, // You can define typography in ui/theme/Type.kt
        content = content
    )
}