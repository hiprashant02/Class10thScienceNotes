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

/**
 * Material 3 Color Scheme Mapping
 * Maps our custom design colors to Material 3 semantic color roles
 */

private val LightColorScheme = lightColorScheme(
    // Primary - Main brand color (Purple)
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryEnd,
    onPrimaryContainer = LightTextPrimary,

    // Secondary - Accent color (Coral)
    secondary = LightAccentCoral,
    onSecondary = Color.White,
    secondaryContainer = LightAccentCoral.copy(alpha = 0.2f),
    onSecondaryContainer = LightTextPrimary,

    // Tertiary - Additional accent
    tertiary = LightPrimaryEnd,
    onTertiary = Color.White,

    // Background
    background = LightBackground,
    onBackground = LightTextPrimary,

    // Surface (Cards, etc.)
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,

    // Outline (Borders, dividers)
    outline = LightTextTertiary.copy(alpha = 0.3f),
    outlineVariant = LightTextTertiary.copy(alpha = 0.15f),

    // Error
    error = Color(0xFFE53935),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    // Primary - Main brand color (Lighter Purple for dark mode)
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryEnd,
    onPrimaryContainer = DarkTextPrimary,

    // Secondary - Accent color (Lighter Coral for dark mode)
    secondary = DarkAccentCoral,
    onSecondary = Color.White,
    secondaryContainer = DarkAccentCoral.copy(alpha = 0.2f),
    onSecondaryContainer = DarkTextPrimary,

    // Tertiary - Additional accent
    tertiary = DarkPrimaryEnd,
    onTertiary = Color.White,

    // Background
    background = DarkBackground,
    onBackground = DarkTextPrimary,

    // Surface (Cards, etc.)
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,

    // Outline (Borders, dividers)
    outline = DarkTextTertiary.copy(alpha = 0.4f),
    outlineVariant = DarkTextTertiary.copy(alpha = 0.2f),

    // Error
    error = Color(0xFFEF5350),
    onError = Color.White
)

@Composable
fun Class10thScienceNotesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled by default to use our custom theme
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}