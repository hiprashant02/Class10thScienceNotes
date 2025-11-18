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
import androidx.compose.material.icons.filled.ArrowForwardIos
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

// 1. Data model for the Chapter list

// 3. The main screen composable
@Composable
fun ChapterListScreen(onClick: (Chapter) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Layer 1: The animated background (for consistency)
        AuroraBackground()

        // Layer 2: The content on top
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp, bottom = 24.dp)
        ) {
            Text(
                text = "Select a Chapter",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Choose a topic to start studying.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        // --- "WATERMARK CARD" LIST DESIGN ---
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            // Start the list below the header
            contentPadding = PaddingValues(
                top = 140.dp,
                start = 20.dp,
                end = 20.dp,
                bottom = 40.dp
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            items(chapters) { chapter ->
                // Use the new, clean list item
                ChapterFocusCard(item = chapter) {
                    onClick.invoke(it)
                }
            }
        }
    }
}

// 4. The NEW "Watermark Hero Card"
@Composable
fun ChapterFocusCard(item: Chapter, onClick: (Chapter) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val cardShape = RoundedCornerShape(20.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = cardShape,
        // This is NOT glass. It's 95% opaque, just to
        // slightly tint with the background aurora.
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ),
        onClick = {
            onClick.invoke(item)
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        // --- THIS IS THE NEW LAYOUT ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp), // Padding on the row
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- NEW: Number Circle (Left side, not big) ---
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(item.color.copy(alpha = 0.15f)), // Faint background
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.id.toString(),
                    color = item.color, // Strong color
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.width(16.dp))

            // --- Chapter Text ---
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
                Text(
                    text = "Chapter ${item.id}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 3
                )
            }

            // --- Chevron Icon ---
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "Go to chapter",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(16.dp)
            )
        }
    }
}
