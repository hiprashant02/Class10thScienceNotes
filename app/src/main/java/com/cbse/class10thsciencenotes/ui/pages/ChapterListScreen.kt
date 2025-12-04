package com.cbse.class10thsciencenotes.ui.pages
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.ElevatedCard
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.cbse.class10thsciencenotes.data.Chapter
//import com.cbse.class10thsciencenotes.data.chapters
//
//@Composable
//fun ChapterListScreen(
//    onChapterClick: (Int) -> Unit
//) {
//    Scaffold {
//        Column(
//            modifier = Modifier
//                .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.02f))
//                .fillMaxSize()
//                .padding(paddingValues = it),
//        ) {
//            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
//                items(chapters.size) { index ->
//                    ElevatedCard(
//                        shape = RoundedCornerShape(16.dp),
//                        colors = CardDefaults.elevatedCardColors(
//                            containerColor = Color(0xC420242E)
//                        ),
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 3.dp)
//                            .clickable { onChapterClick(index) }
//                    ) {
//                        Box(
//                            modifier = Modifier.padding(20.dp),
//                            contentAlignment = Alignment.CenterStart
//                        ) {
//                            Text(
//                                text = "${index + 1}. ${chapters[index].name}",
//                                fontSize = 18.sp,
//                                fontWeight = FontWeight.Medium,
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}


// In MainActivity.kt (or wherever your screens are)
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ChapterListScreen(
//    navController: NavController,
//) {
//    // No more .collectAsState(). Just access the property directly.
//
//
//    Scaffold(
//        topBar = {
//            TopAppBar(title = { Text("Class 10 Science") })
//        }
//    ) { padding ->
//        LazyColumn(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxSize(),
//            contentPadding = PaddingValues(16.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            items(chapters) { chapter ->
//                ChapterCard(chapter = chapter) {
//                    navController.navigate("notes_screen/${chapter.jsonFileName}")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun ChapterCard(chapter: Chapter, onClick: () -> Unit) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(text = "Chapter ${chapter.id}", style = MaterialTheme.typography.titleMedium)
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(text = chapter.title, style = MaterialTheme.typography.bodyLarge)
//        }
//    }
//}

import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cbse.class10thsciencenotes.data.Chapter
import com.cbse.class10thsciencenotes.data.chapters
import com.cbse.class10thsciencenotes.ui.theme.AppDimensions
import com.cbse.class10thsciencenotes.ui.theme.AppTheme
import com.cbse.class10thsciencenotes.ui.theme.withCardAlpha
import com.cbse.class10thsciencenotes.ui.theme.withIconAlpha




// 1. Data model for the Chapter list

import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.navigation.NavController

// ...existing imports...

// 3. The main screen composable (Using Theme System)
@Composable
fun ChapterListScreen(navController: NavController, onClick: (Chapter) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Beautiful Header with Back Button
            ChapterListHeader(onBackClick = { navController.popBackStack() })

            // Chapter List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(AppDimensions.CardSpacing.dp),
                contentPadding = PaddingValues(
                    top = AppDimensions.ContentPaddingVertical.dp,
                    start = AppDimensions.ContentPaddingHorizontal.dp,
                    end = AppDimensions.ContentPaddingHorizontal.dp,
                    bottom = AppDimensions.SpacingLarge.dp
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                items(chapters) { chapter ->
                    ElegantChapterCard(item = chapter) {
                        onClick.invoke(it)
                    }
                }
            }
        }
    }
}

// 4. Beautiful Header with Back Button (Using Theme System)
@Composable
fun ChapterListHeader(onBackClick: () -> Unit = {}) {
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart)
                .padding(AppDimensions.SpacingLarge.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "CLASS 10TH SCIENCE",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(AppDimensions.SpacingSmall.dp))
                Text(
                    text = "All Chapters",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Select a chapter to begin",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
            }
        }
    }
}

// 5. Elegant Chapter Card (Using Theme System)
@Composable
fun ElegantChapterCard(item: Chapter, onClick: (Chapter) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        onClick = { onClick.invoke(item) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppDimensions.CardCornerRadius.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AppDimensions.CardElevationDefault.dp,
            pressedElevation = AppDimensions.CardElevationPressed.dp
        ),
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimensions.CardPadding.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Number Circle (Left side)
            Box(
                modifier = Modifier
                    .size(AppDimensions.ChapterCircleSize.dp)
                    .clip(CircleShape)
                    .background(item.color.withCardAlpha()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.id.toString(),
                    color = item.color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.width(AppDimensions.SpacingMedium.dp))

            // Chapter Text
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingXSmall.dp)
            ) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
                Text(
                    text = "Chapter ${item.id}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Chevron Icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Go to chapter",
                tint = AppTheme.iconTint.withIconAlpha(),
                modifier = Modifier.size(AppDimensions.IconSizeSmall.dp)
            )
        }
    }
}
