package com.cbse.class10thsciencenotes.ui.pages

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Ballot
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cbse.class10thsciencenotes.ui.theme.AppDimensions
import com.cbse.class10thsciencenotes.ui.theme.AppTheme
import com.cbse.class10thsciencenotes.ui.theme.withCardAlpha

// --- 1. Data Model (KEPT YOUR ORIGINAL DATA) ---
enum class Study_Option {
    Notes, Revision_Notes, Intext_Questions, BackExercise_Questions, Exemplar, Practise_MCQs
}

data class StudyOption(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val type: Study_Option
)

// --- 2. Dummy Data (KEPT YOUR ORIGINAL DATA) ---
val studyOptions = listOf(
    StudyOption(
        "Notes",
        "Full Detailed chapter notes.",
        Icons.AutoMirrored.Outlined.Notes,
        Color(0xFF03A9F4),
        Study_Option.Notes
    ),
    StudyOption(
        "Revision Notes",
        "Quick revision Notes.",
        Icons.Outlined.AutoStories,
        Color(0xFF4CAF50),
        Study_Option.Revision_Notes
    ),
    StudyOption(
        "InText Questions",
        "in-chapter questions.",
        Icons.Outlined.Quiz,
        Color(0xFFF44336),
        Study_Option.Intext_Questions
    ),
    StudyOption(
        "Back Exercise Questions",
        "All exercise solutions.",
        Icons.AutoMirrored.Filled.Assignment,
        Color(0xFFFF9800),
        Study_Option.BackExercise_Questions
    ),
    StudyOption(
        "Exemplar",
        "Advanced & tricky problems.",
        Icons.Outlined.WorkspacePremium,
        Color(0xFF9C27B0),
        Study_Option.Exemplar
    ),
    StudyOption(
        "Practise MCQs",
        "Test your Knowledge.",
        Icons.Outlined.Ballot,
        Color(0xFF009688),
        Study_Option.Practise_MCQs
    )
)

// --- 3. Main Screen Composable (Using Theme System) ---
@Composable
fun StudyDashboardScreen(onClick: (StudyOption) -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Beautiful Header
            StudyDashboardHeader()

            // Scrolling Grid with cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.CardSpacing.dp),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.CardSpacing.dp),
                contentPadding = PaddingValues(
                    top = AppDimensions.ContentPaddingVertical.dp,
                    start = AppDimensions.ContentPaddingHorizontal.dp,
                    end = AppDimensions.ContentPaddingHorizontal.dp,
                    bottom = AppDimensions.SpacingLarge.dp
                )
            ) {
                items(studyOptions) { option ->
                    ElegantStudyCard(item = option) {
                        onClick.invoke(it)
                    }
                }
            }
        }
    }
}

// --- 4. Beautiful Header (Using Theme System) ---
@Composable
fun StudyDashboardHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(AppDimensions.HeaderHeight.dp)
            .clip(RoundedCornerShape(bottomStart = AppDimensions.HeaderCornerRadius.dp, bottomEnd = AppDimensions.HeaderCornerRadius.dp))
            .background(brush = AppTheme.primaryGradientBrush)
    ) {
        // Decorative Circles for aesthetics
        Box(
            modifier = Modifier
                .offset(x = (-40).dp, y = (-40).dp)
                .size(AppDimensions.DecorativeCircleSmall.dp)
                .clip(CircleShape)
                .background(AppTheme.decorativeColor)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 20.dp, y = 40.dp)
                .size(AppDimensions.DecorativeCirlceLarge.dp)
                .clip(CircleShape)
                .background(AppTheme.decorativeColor)
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(AppDimensions.SpacingLarge.dp)
        ) {
            Text(
                text = "CLASS 10TH SCIENCE",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(AppDimensions.SpacingSmall.dp))
            Text(
                text = "Study Hub",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "Choose your learning path",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
            )
        }
    }
}

// --- 5. Elegant Study Card (Using Theme System) ---
@Composable
fun ElegantStudyCard(item: StudyOption, onClick: (StudyOption) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Smooth scale animation
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "scale"
    )

    ElevatedCard(
        onClick = { onClick.invoke(item) },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(AppDimensions.CardCornerRadius.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AppDimensions.CardElevationDefault.dp,
            pressedElevation = AppDimensions.CardElevationPressed.dp
        ),
        interactionSource = interactionSource
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppDimensions.CardPadding.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon with subtle colored background
            Box(
                modifier = Modifier
                    .size(AppDimensions.IconCircleSize.dp)
                    .clip(CircleShape)
                    .background(item.color.withCardAlpha()),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.color,
                    modifier = Modifier.size(AppDimensions.IconSizeMedium.dp)
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.SpacingMedium.dp))

            // Text content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )
                Text(
                    text = item.subtitle,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

// --- 6. Modern Subtle Background ---
@Composable
fun ModernBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F9FA),
                        Color(0xFFE9ECEF)
                    )
                )
            )
    )
}

// --- 7. Aurora Background (Kept for reference) ---
@Composable
fun AuroraBackground() {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }

    val screenHeightDp = configuration.screenHeightDp.dp
    val screenWidthDp = configuration.screenWidthDp.dp

    val infiniteTransition = rememberInfiniteTransition(label = "aurora")

    val xOffset by infiniteTransition.animateFloat(
        initialValue = -screenWidthPx * 0.5f,
        targetValue = screenWidthPx * 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "xOffset"
    )

    val yOffset by infiniteTransition.animateFloat(
        initialValue = -screenHeightPx * 0.5f,
        targetValue = screenHeightPx * 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "yOffset"
    )

    val xOffsetDp = with(density) { xOffset.toDp() }
    val yOffsetDp = with(density) { yOffset.toDp() }

    // --- BASE GRADIENT ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
                    )
                )
            )
    )

    // --- BLOBS ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(alpha = 0.7f)
    ) {
        // Blob 1 (Gold)
        Box(
            modifier = Modifier
                .size(screenWidthDp * 0.9f)
                .offset(x = xOffsetDp * 0.5f, y = yOffsetDp * 0.6f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x99FFD700),
                            Color.Transparent
                        )
                    )
                )
        )

        // Blob 2 (Blue)
        Box(
            modifier = Modifier
                .size(screenWidthDp * 0.7f)
                .align(Alignment.BottomEnd)
                .offset(x = xOffsetDp * -1.0f, y = yOffsetDp * -0.5f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x9900BFFF),
                            Color.Transparent
                        )
                    )
                )
        )

        // Blob 3 (Purple)
        Box(
            modifier = Modifier
                .size(screenWidthDp * 0.75f)
                .align(Alignment.TopEnd)
                .offset(x = xOffsetDp * 0.8f, y = yOffsetDp * -0.4f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x999370DB),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}



