package com.cbse.class10thsciencenotes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Extended Theme Properties
 * Provides easy access to custom theme values like gradients, decorative colors, etc.
 */

object AppTheme {

    /**
     * Primary gradient brush for headers
     */
    val primaryGradientBrush: Brush
        @Composable
        get() = Brush.verticalGradient(
            colors = if (isSystemInDarkTheme()) {
                listOf(DarkPrimaryStart, DarkPrimaryEnd)
            } else {
                listOf(LightPrimaryStart, LightPrimaryEnd)
            }
        )

    /**
     * Decorative circle color (for header decorations)
     */
    val decorativeColor: Color
        @Composable
        get() = if (isSystemInDarkTheme()) DarkDecorative else LightDecorative

    /**
     * Icon tint color
     */
    val iconTint: Color
        @Composable
        get() = if (isSystemInDarkTheme()) DarkIconTint else LightIconTint

    /**
     * Card background with slight transparency/elevation
     */
    val cardBackground: Color
        @Composable
        get() = if (isSystemInDarkTheme()) DarkSurface else LightSurface
}

/**
 * Design Dimensions
 * Standardized spacing, sizes, and shape values
 */
object AppDimensions {
    // Header
    const val HeaderHeight = 220
    const val HeaderCornerRadius = 32
    const val DecorativeCircleSmall = 150
    const val DecorativeCirlceLarge = 200

    // Cards
    const val CardCornerRadius = 16
    const val CardElevationDefault = 2
    const val CardElevationPressed = 6
    const val CardPadding = 20
    const val CardSpacing = 12

    // Chapter Number Circle
    const val ChapterCircleSize = 50

    // Icon Sizes
    const val IconSizeSmall = 16
    const val IconSizeMedium = 32
    const val IconSizeLarge = 36
    const val IconCircleSize = 60

    // Spacing
    const val SpacingXSmall = 4
    const val SpacingSmall = 8
    const val SpacingMedium = 16
    const val SpacingLarge = 24
    const val SpacingXLarge = 32

    // Content Padding
    const val ContentPaddingHorizontal = 16
    const val ContentPaddingVertical = 16
}

/**
 * Helper to get chapter color with reduced alpha
 */
@Composable
fun Color.withCardAlpha(): Color = this.copy(alpha = 0.12f)

/**
 * Helper to get icon tint with reduced alpha
 */
@Composable
fun Color.withIconAlpha(): Color = this.copy(alpha = 0.5f)

