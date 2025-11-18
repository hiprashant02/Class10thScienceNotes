package com.cbse.class10thsciencenotes.ui.pages//import android.os.Build
import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Note
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Ballot
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material3.ripple
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign


// 1. Data model for the grid items
enum class Study_Option{
    Notes,Revision_Notes,Intext_Questions,BackExercise_Questions, Exemplar, Practise_MCQs
}
data class StudyOption(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color, // Added a color for each item,
    val type: Study_Option
)

// 2. Dummy datax
val studyOptions = listOf(
    StudyOption(
        "Notes",
        "Full Detailed chapter notes.", // Was: "Lack concept clarity? w"
        Icons.AutoMirrored.Outlined.Notes,
        Color(0xFF03A9F4),
        Study_Option.Notes
    ),
    StudyOption(
        "Revision Notes",
        "Quick revision Notes.", // Was: "Studied chapter already? This is for you"
        Icons.Outlined.AutoStories,
        Color(0xFF4CAF50),
        Study_Option.Revision_Notes
    ),
    StudyOption(
        "InText Questions",
        "in-chapter questions.", // Was: "Questions"
        Icons.Outlined.Quiz,
        Color(0xFFF44336),
        Study_Option.Intext_Questions
    ),
    StudyOption(
        "Back Exercise Questions", // Corrected spelling
        "All exercise solutions.", // Was: "Practice"
        Icons.AutoMirrored.Filled.Assignment,
        Color(0xFFFF9800),
        Study_Option.BackExercise_Questions
    ),
    StudyOption(
        "Exemplar",
        "Advanced & tricky problems.", // Was: "Questions"
        Icons.Outlined.WorkspacePremium,
        Color(0xFF9C27B0),
        Study_Option.Exemplar
    ),
    StudyOption(
        "Practise MCQs",
        "Test your Knowledge.", // Was: "More"
        Icons.Outlined.Ballot,
        Color(0xFF009688),
        Study_Option.Practise_MCQs
    )
)

// 3. The main screen composable
@Composable
fun StudyDashboardScreen(onClick: (StudyOption) -> Unit) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Ensure background is set
    ) {
        // Layer 1: The animated background
        AuroraBackground()

        // Layer 2: The content on top
        // <-- IMPROVEMENT 1: Layout Hierarchy
        // The header is now in its own Column, separate from the grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 32.dp)
        ) {
            // Header Text
            Spacer(Modifier.height(36.dp))
            Text(
                text = "Study Hub",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Welcome back, let's get studying!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(0.dp))
        }

        // The Grid of glassmorphic cards now scrolls *under* the header
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            // <-- IMPROVEMENT 1: Padding is adjusted to fit under the static header
            contentPadding = PaddingValues(top = 160.dp, start = 16.dp, end = 16.dp, bottom = 40.dp)
        ) {
            items(studyOptions) { option ->
                GlassmorphicCard(item = option){
                    onClick.invoke(it)
                }
            }
        }
    }
}

// 4. The Glassmorphic Card composable
@Composable
fun GlassmorphicCard(item: StudyOption,onClick:(StudyOption)-> Unit) {

    // <-- IMPROVEMENT 2: Physical Interaction
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Makes the card a square
            .graphicsLayer { // Apply the scale animation
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple() // No ripple, we have our own animation
            ) { onClick.invoke(item) }
    ) {
        // --- LAYER 1: BACKGROUND (BLURRED) ---
        Box(
            modifier = Modifier
                .matchParentSize()
                .glassBackground() // Use the new modifier
        )

        // --- LAYER 2: FOREGROUND (NOT BLURRED) ---
        Column(
            modifier = Modifier
                .matchParentSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- ICON ---
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = item.color,
                modifier = Modifier
                    .background(Color.Transparent)
                    .size(48.dp)
                    .graphicsLayer(
                        shadowElevation = 15f,
                        spotShadowColor = item.color.copy(alpha = 0.5f)
                    )
            )

            // --- TEXT ---
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,

                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.subtitle,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// 5. The new glass background modifier
fun Modifier.glassBackground(
    shape: Shape = RoundedCornerShape(24.dp)
): Modifier = composed {

    val glassColor = MaterialTheme.colorScheme.surface
    // Use the outline color for the sheen, but make it very subtle
    val sheenColor = MaterialTheme.colorScheme.outline

    val blurModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        this.blur(
            radius = 32.dp,
            edgeTreatment = BlurredEdgeTreatment(shape)
        )
    } else {
        this.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
    }

    this
        .then(blurModifier)
        .background(glassColor, shape) // This adds the main glass tint
        // <-- IMPROVEMENT 3: Premium Material (Sheen)
        // We add a nested Box to draw a subtle gradient border *inside*
        // the main shape, which looks more premium than a simple outer border.
        .then(
            Modifier.border(
                BorderStroke(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            sheenColor.copy(alpha = 0.8f), // Faint highlight at the top
                            sheenColor.copy(alpha = 0.2f)  // Fades out at the bottom
                        )
                    )
                ),
                shape = shape
            )
        )
}

// 6. The animated Aurora Background (with base gradient)
@Composable
fun AuroraBackground() {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }

    val screenHeightDp = configuration.screenHeightDp.dp
    val screenWidthDp = configuration.screenWidthDp.dp

    val infiniteTransition = rememberInfiniteTransition(label = "aurora")

    // <-- IMPROVEMENT 4: Slower, more "ethereal" animation
    val xOffset by infiniteTransition.animateFloat(
        initialValue = -screenWidthPx * 0.5f,
        targetValue = screenWidthPx * 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing), // Was 5000
            repeatMode = RepeatMode.Reverse
        ), label = "xOffset"
    )

    val yOffset by infiniteTransition.animateFloat(
        initialValue = -screenHeightPx * 0.5f,
        targetValue = screenHeightPx * 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing), // Was 7000
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
            // <-- IMPROVEMENT 4: Softer, more subtle blobs
            .graphicsLayer(alpha = 0.7f) // Was 0.6f, let's try 0.7
    ) {
        // Blob 1 (Gold)
        Box(
            modifier = Modifier
                .size(screenWidthDp * 0.9f)
                .offset(x = xOffsetDp * 0.5f, y = yOffsetDp * 0.6f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x99FFD700), // Was CC (80%), now 60%
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
                            Color(0x9900BFFF), // Was CC (80%), now 60%
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
                            Color(0x999370DB), // Was CC (80%), now 60%
                            Color.Transparent
                        )
                    )
                )
        )
    }
}





