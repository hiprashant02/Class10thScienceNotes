package com.cbse.class10thsciencenotes.ui.pages

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cbse.class10thsciencenotes.R
import com.cbse.class10thsciencenotes.data.NoteItem
import com.cbse.class10thsciencenotes.data.NotesViewModel
import com.cbse.class10thsciencenotes.ui.components.SettingsDialog
import com.cbse.class10thsciencenotes.ui.components.TTSFloatingControls
import com.cbse.class10thsciencenotes.ui.components.ZoomIn
import com.cbse.class10thsciencenotes.ui.components.ZoomableImageDialog
import com.cbse.class10thsciencenotes.ui.components.rememberTextToSpeechManager
import kotlin.math.absoluteValue


val LocalBaseFontSize = compositionLocalOf { 12f }
val LocalHighlightRange = compositionLocalOf<Pair<Int, Int>?> { null }
val LocalFullText = compositionLocalOf { "" }

// Helper function to extract plain text from notes for TTS
fun extractTextFromNotes(notes: List<NoteItem>): String {
    return buildString {
        notes.forEach { item ->
            when (item) {
                is NoteItem.TopicName -> append(item.text).append(". ")
                is NoteItem.Heading -> append(item.text).append(". ")
                is NoteItem.Paragraph -> append(item.text).append(". ")
                is NoteItem.Bullet -> {
                    append(item.text).append(". ")
                    item.subBullets.forEach { subBullet ->
                        append(subBullet).append(". ")
                    }
                }
                is NoteItem.Image -> {} // Skip images
            }
        }
    }
}

// Highlighted Text Composable for TTS word highlighting
@Composable
fun HighlightedText(
    text: String,
    highlightRange: Pair<Int, Int>?,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    val annotatedString = remember(text, highlightRange) {
        buildAnnotatedString {
            if (highlightRange != null &&
                highlightRange.first >= 0 &&
                highlightRange.first < text.length &&
                highlightRange.second > highlightRange.first &&
                highlightRange.second <= text.length) {
                // Before highlight
                append(text.substring(0, highlightRange.first))

                // Highlighted portion
                withStyle(
                    SpanStyle(
                        background = Color(0xFFFFEB3B), // Yellow highlight
                        color = Color(0xFF2D3436)
                    )
                ) {
                    append(text.substring(highlightRange.first, highlightRange.second))
                }

                // After highlight
                if (highlightRange.second < text.length) {
                    append(text.substring(highlightRange.second))
                }
            } else {
                append(text)
            }
        }
    }

    Text(
        text = annotatedString,
        style = style,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    jsonFileName: String,
    navController: NavController,
    viewModel: NotesViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val orientation = LocalConfiguration.current.orientation
    val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var viewingImageResId by rememberSaveable { mutableStateOf<Int?>(null) }
    var baseFontSize by rememberSaveable { mutableStateOf(15f) }
    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }

    // Scroll state for auto-hide navigation
    val scrollState = rememberScrollState()
    var isNavigationVisible by remember { mutableStateOf(true) }
    var lastScrollPosition by remember { mutableIntStateOf(0) }

    // TTS State
    var currentHighlightRange by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var isTTSPlaying by remember { mutableStateOf(false) }
    var speechRate by remember { mutableStateOf(0.9f) }
    var fullNoteText by remember { mutableStateOf("") }

    // TTS Manager
    val ttsManager = rememberTextToSpeechManager(
        onWordSpoken = { word, start, end ->
            currentHighlightRange = Pair(start, end)
            Log.d("NotesScreen", "Highlighting word: '$word' at [$start, $end]")
        },
        onComplete = {
            isTTSPlaying = false
            currentHighlightRange = null
            Log.d("NotesScreen", "TTS completed")
        }
    )

    LaunchedEffect(jsonFileName) {
        viewModel.loadChapter(context, jsonFileName)
    }

    // Extract full text from notes when loaded
    LaunchedEffect(uiState.currentNotes) {
        fullNoteText = extractTextFromNotes(uiState.currentNotes)
    }

    // Detect scroll direction
    LaunchedEffect(scrollState.value) {
        val currentPosition = scrollState.value
        isNavigationVisible = currentPosition < lastScrollPosition || currentPosition == 0
        lastScrollPosition = currentPosition
    }
    viewingImageResId?.let { resId ->
        ZoomableImageDialog(
            resId = resId,
            onDismiss = { viewingImageResId = null } // To dismiss, just set the state to null
        )
    }





    if (showSettingsDialog) {
        SettingsDialog(
            initialFontSize = baseFontSize,
            onDismiss = { showSettingsDialog = false },
            onFontSizeChange = { newSize ->
                baseFontSize = newSize
            }
        )
    }
    CompositionLocalProvider(
        LocalBaseFontSize provides baseFontSize,
        LocalHighlightRange provides currentHighlightRange,
        LocalFullText provides fullNoteText
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA)),
            floatingActionButton = {
                // Always visible Play/Pause FAB
                if (!isLandscape) {
                    androidx.compose.material3.FloatingActionButton(
                        onClick = {
                            if (isTTSPlaying) {
                                ttsManager.stop()
                                isTTSPlaying = false
                            } else {
                                ttsManager.speak(fullNoteText)
                                isTTSPlaying = true
                            }
                        },
                        containerColor = if (isTTSPlaying) Color(0xFFFF6B6B) else Color(0xFF6C63FF),
                        modifier = Modifier.padding(bottom = if (isNavigationVisible) 80.dp else 16.dp)
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = if (isTTSPlaying) androidx.compose.material.icons.Icons.Default.Stop else androidx.compose.material.icons.Icons.Default.PlayArrow,
                            contentDescription = if (isTTSPlaying) "Stop Reading" else "Start Reading",
                            tint = Color.White
                        )
                    }
                }
            },
            bottomBar = {
                Column {
                    // TTS Controls (show when playing for speed control)
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isTTSPlaying,
                        enter = androidx.compose.animation.slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = androidx.compose.animation.core.tween(300)
                        ),
                        exit = androidx.compose.animation.slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = androidx.compose.animation.core.tween(300)
                        )
                    ) {
                        TTSFloatingControls(
                            isPlaying = isTTSPlaying,
                            onPlayPause = {
                                if (isTTSPlaying) {
                                    ttsManager.stop()
                                    isTTSPlaying = false
                                } else {
                                    ttsManager.speak(fullNoteText)
                                    isTTSPlaying = true
                                }
                            },
                            onStop = {
                                ttsManager.stop()
                                isTTSPlaying = false
                                currentHighlightRange = null
                            },
                            speechRate = speechRate,
                            onSpeechRateChange = { rate ->
                                speechRate = rate
                                ttsManager.setSpeechRate(rate)
                            }
                        )
                    }

                    // Page Navigation
                    if (!isLandscape) {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = isNavigationVisible,
                            enter = androidx.compose.animation.slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = androidx.compose.animation.core.tween(300)
                            ),
                            exit = androidx.compose.animation.slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = androidx.compose.animation.core.tween(300)
                            )
                        ) {
                            BottomPageNavigator(
                                totalTopics = uiState.totalTopics,
                                currentIndex = currentIndex,
                                onValueChange = { currentIndex = it },
                                onFinalValue = {
                                    viewModel.changeTopic(currentIndex)
                                },
                                currentTopicName = if (viewModel.allTopicNames.isNotEmpty() && currentIndex < viewModel.allTopicNames.size)
                                    viewModel.allTopicNames[currentIndex]
                                else ""
                            )
                        }
                    }
                }
            },
            topBar = {
                if (!isLandscape) {
                    // No TopAppBar - we'll use a custom header instead
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Beautiful Header
                if (!isLandscape) {
                    NotesScreenHeader(
                        chapterTitle = uiState.chapterTitle,
                        onSettingsClick = { showSettingsDialog = true }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    PaginatedNotes(
                        notes = uiState.currentNotes,
                        background = R.drawable.page_bg,
                        screenHeight = screenHeight,
                        modifier = Modifier.weight(1f),
                        onImageClick = {
                            viewingImageResId = it
                        },
                        scrollState = scrollState,
                        highlightRange = currentHighlightRange,
                        fullNoteText = fullNoteText
                    )


                    if (isLandscape) {
                        VerticalSidebar(
                            totalTopics = uiState.totalTopics,
                            currentIndex = currentIndex,
                            onValueChange = { currentIndex = it },
                            onFinalValue = {
                                viewModel.changeTopic(currentIndex)
                            },
                            modifier = Modifier.fillMaxHeight(),
                            onSettingsClick = {
                                showSettingsDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

// Default base font size is 12sp

// Compact Header for Notes Screen (Shorter height)
@Composable
fun NotesScreenHeader(chapterTitle: String, onSettingsClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF6C63FF), Color(0xFF8E84FF))
                )
            )
    ) {
        // Decorative Circles for aesthetics (smaller)
        Box(
            modifier = Modifier
                .offset(x = (-30).dp, y = (-30).dp)
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 20.dp, y = 30.dp)
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "STUDY NOTES",
                    style = TextStyle(
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = chapterTitle,
                    style = TextStyle(
                        fontFamily = kalamFont,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    maxLines = 1
                )
            }
            
            // Settings button
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = ZoomIn,
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerticalSidebar(
    totalTopics: Int,
    currentIndex: Int,
    onValueChange: (Int) -> Unit,
    onFinalValue: () -> Unit,
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(70.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFF5F7FA), Color.White)
                )
            )
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Previous button
        IconButton(
            onClick = {
                val newIndex = (currentIndex - 1).coerceAtLeast(0)
                onValueChange(newIndex)
                onFinalValue()
            },
            enabled = currentIndex > 0,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (currentIndex > 0) Color(0xFF6C63FF).copy(alpha = 0.1f) else Color.Transparent,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Previous Topic",
                tint = if (currentIndex > 0) Color(0xFF6C63FF) else Color(0xFFB0B3B8),
                modifier = Modifier.size(28.dp)
            )
        }

        // Progress indicator with dots
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Topic counter chip
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF6C63FF), Color(0xFF8E84FF))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${currentIndex + 1}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Vertical progress dots
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(minOf(totalTopics, 7)) { index ->
                    val actualIndex = when {
                        totalTopics <= 7 -> index
                        currentIndex < 3 -> index
                        currentIndex > totalTopics - 4 -> totalTopics - 7 + index
                        else -> currentIndex - 3 + index
                    }

                    Box(
                        modifier = Modifier
                            .size(
                                width = 8.dp,
                                height = if (actualIndex == currentIndex) 20.dp else 8.dp
                            )
                            .background(
                                color = if (actualIndex == currentIndex)
                                    Color(0xFF6C63FF)
                                else
                                    Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable {
                                onValueChange(actualIndex)
                                onFinalValue()
                            }
                    )
                }
            }
        }

        // Settings button
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = Color(0xFF6C63FF).copy(alpha = 0.1f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = ZoomIn,
                contentDescription = "Settings",
                tint = Color(0xFF6C63FF),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        // Next button
        IconButton(
            onClick = {
                val newIndex = (currentIndex + 1).coerceAtMost(totalTopics - 1)
                onValueChange(newIndex)
                onFinalValue()
            },
            enabled = currentIndex < totalTopics - 1,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (currentIndex < totalTopics - 1) Color(0xFF6C63FF).copy(alpha = 0.1f) else Color.Transparent,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Next Topic",
                tint = if (currentIndex < totalTopics - 1) Color(0xFF6C63FF) else Color(0xFFB0B3B8),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}


//fun loadNotes(index: Int): List<NoteItem> {
//    return if (index == 1) listOf(
//        NoteItem.TopicName("🌱 Topic: 5.2.1 Autotrophic Nutrition"),
//        NoteItem.Heading("🌞 What is Photosynthesis?"),
//        NoteItem.Paragraph("Photosynthesis is the process where autotrophs take simple things from the outside world and turn them into stored energy (food!)."),
//        NoteItem.Heading("🧪 The Ingredients for Photosynthesis"),
//        NoteItem.Paragraph("Plants take in two main things from the environment:"),
//        NoteItem.Bullet("Carbon Dioxide (CO₂) – from the air we breathe out"),
//        NoteItem.Bullet("Water (H₂O) – from the soil"),
//        NoteItem.Paragraph("In the presence of Sunlight ☀️ and Chlorophyll (the green stuff in leaves), these ingredients are converted into carbohydrates."),
//        NoteItem.Paragraph("🍞 Carbohydrates: This is the plant’s food! Like roti or rice for the plant."),
//        NoteItem.Heading("🍱 What Happens to the Food (Carbohydrates)?"),
//        NoteItem.Bullet("Used by the plant for energy"),
//        NoteItem.Bullet("Extra food is stored as starch"),
//        NoteItem.Bullet("🌟 Starch: Backup food supply; internal energy reserve"),
//        NoteItem.Heading("💡 Did you know?"),
//        NoteItem.Bullet("Humans also store energy as glycogen"),
//        NoteItem.Bullet("Plant’s stored food = Starch"),
//        NoteItem.Bullet("Human’s stored food = Glycogen"),
//        NoteItem.Heading("🧬 The Photosynthesis Chemical Equation"),
//        NoteItem.Paragraph("6CO₂ + 12H₂O → C₆H₁₂O₆ + 6O₂ + 6H₂O (In presence of Chlorophyll and Sunlight)"),
//        NoteItem.Paragraph("Let’s break this down:"),
//        NoteItem.Bullet("6CO₂: Six Carbon Dioxide molecules"),
//        NoteItem.Bullet("12H₂O: Twelve Water molecules"),
//        NoteItem.Bullet("C₆H₁₂O₆: Glucose (plant’s food!), a type of carbohydrate 🍽️"),
//        NoteItem.Bullet("6O₂: Oxygen released as a by-product 🌬️"),
//        NoteItem.Bullet("6H₂O: Additional Water formed"),
//        NoteItem.Heading("🔁 The 3 Main Events of Photosynthesis"),
//        NoteItem.Bullet(
//            "Absorption of Light Energy by Chlorophyll",
//            listOf("Chlorophyll absorbs sunlight 🌞 like a tiny solar panel 🔋")
//        ),
//        NoteItem.Bullet(
//            "Conversion of Light Energy to Chemical Energy & Splitting of Water",
//            listOf(
//                "Light energy turns into chemical energy",
//                "Water (H₂O) is split into Hydrogen (H) and Oxygen (O) 💧 ➡️ H + O"
//            )
//        ),
//        NoteItem.Bullet(
//            "Reduction of Carbon Dioxide to Carbohydrates",
//            listOf("CO₂ combines with Hydrogen to form glucose (a carbohydrate)")
//        ),
//        NoteItem.Heading("🌵 Do These Steps Always Happen One After Another?"),
//        NoteItem.Bullet("Not always!"),
//        NoteItem.Bullet(
//            "Desert plants are smart:",
//            listOf(
//                "Take in CO₂ at night to save water 🌙",
//                "Make an intermediate compound",
//                "Use sunlight during the day to make food ☀️"
//            )
//        ),
//        NoteItem.Heading("🔬 Where Does Photosynthesis Happen?"),
//        NoteItem.Image(R.drawable.photosynthesis_diagram),
//        NoteItem.Bullet("Look at a leaf cross-section under a microscope 🔍"),
//        NoteItem.Bullet("You’ll see green dots = chloroplasts"),
//        NoteItem.Bullet("Chloroplasts contain chlorophyll"),
//        NoteItem.Bullet("So, chloroplast = kitchen of the cell 👩‍🍳")
//    )
//    else listOf(
//        NoteItem.TopicName("🌿 Topic: How Plants Get Their Raw Materials 🚚"),
//
//        NoteItem.Heading("💨 Getting Carbon Dioxide (CO₂)"),
//        NoteItem.Bullet("Plants get carbon dioxide from the air through tiny pores on the surface of leaves called stomata."),
//        NoteItem.Bullet("🕳️ Stomata (singular: stoma): Like tiny \"mouths\" on the leaf for gas exchange."),
//        NoteItem.Bullet("🌬️ A massive amount of gas exchange takes place through these stomata during photosynthesis."),
//        NoteItem.Paragraph("🔍 Note:\nGas exchange also happens across the surface of stems and roots."),
//
//        NoteItem.Heading("💧 The Problem of Water Loss"),
//        NoteItem.Bullet("While stomata allow CO₂ in, they can also cause water loss."),
//        NoteItem.Bullet("🌵 To avoid losing too much water, the plant closes the stomata when CO₂ isn't needed."),
//
//        NoteItem.Heading("💂 The Gatekeepers: Guard Cells"),
//        NoteItem.Bullet("Opening and closing of stomata is regulated by guard cells."),
//        NoteItem.Bullet("🫘 Guard Cells: Two bean-shaped cells around each stoma that act like gatekeepers."),
//        NoteItem.Paragraph("🔄 How It Works:"),
//        NoteItem.Bullet(
//            "When water enters the guard cells:",
//            listOf("They swell and curve outward ➡️ Stomata opens")
//        ),
//        NoteItem.Bullet(
//            "When water is lost from the guard cells:",
//            listOf("They shrink and straighten ➡️ Stomata closes")
//        ),
//
//        NoteItem.Heading("🌱 Getting Water and Other Raw Materials from the Soil"),
//        NoteItem.Paragraph("Plants need more than just CO₂—they also need water and minerals."),
//        NoteItem.Bullet("🚿 Water (H₂O):"),
//        NoteItem.Bullet("Terrestrial plants absorb water from the soil through their roots"),
//
//        NoteItem.Bullet("🧪 Other Essential Minerals:"),
//        NoteItem.Bullet("📌 Nitrogen"),
//        NoteItem.Bullet("📌 Phosphorus"),
//        NoteItem.Bullet("📌 Iron"),
//        NoteItem.Bullet("📌 Magnesium"),
//        NoteItem.Paragraph("These are also taken up through the roots."),
//
//        NoteItem.Heading("⭐ The Importance of Nitrogen"),
//        NoteItem.Bullet("Nitrogen is an essential nutrient used to build proteins and important plant compounds."),
//        NoteItem.Paragraph("🧬 How Plants Get Nitrogen:"),
//        NoteItem.Bullet("From inorganic forms in soil (like nitrates or nitrites)"),
//        NoteItem.Bullet(
//            "From organic compounds made by special bacteria 🦠",
//            listOf("These bacteria fix nitrogen from the atmosphere into a usable form for plants")
//        )
//    )
//}

val kalamFont = FontFamily(Font(R.font.hand_written))

// Handwritten ink colors - multiple shades for authenticity
val headingColor = Color(0xFF0D47A1)  // Slightly darker blue for headings
val bodyColor = Color(0xFF1565C0)     // Main ink blue
val bodyColorVariant1 = Color(0xFF1976D2)  // Slightly lighter for variation
val bodyColorVariant2 = Color(0xFF0D47A1)  // Slightly darker for variation
val bulletColor = Color(0xFF1565C0)   // Bullet point color

// Random-ish rotation values to make text look handwritten (very subtle)
val textRotations = listOf(0f, 0.3f, -0.2f, 0.4f, -0.3f, 0.2f, -0.4f, 0.5f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomPageNavigator(
    totalTopics: Int,
    currentIndex: Int,
    onValueChange: (Int) -> Unit,
    onFinalValue: () -> Unit,
    modifier: Modifier = Modifier,
    currentTopicName: String
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF5F7FA),
                        Color.White
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Topic indicator with beautiful chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Topic",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6C63FF).copy(alpha = 0.7f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.width(6.dp))

            // Current topic chip
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF6C63FF), Color(0xFF8E84FF))
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${currentIndex + 1} / $totalTopics",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Topic name with elegant styling
        Text(
            text = currentTopicName,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D3436),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            maxLines = 1
        )

        // Beautiful navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous button
            IconButton(
                onClick = {
                    onValueChange.invoke((currentIndex - 1).coerceIn(0, totalTopics - 1))
                    onFinalValue.invoke()
                },
                enabled = currentIndex > 0,
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = if (currentIndex > 0) Color(0xFF6C63FF).copy(alpha = 0.1f) else Color.Transparent,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Previous Topic",
                    tint = if (currentIndex > 0) Color(0xFF6C63FF) else Color(0xFFB0B3B8),
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer(rotationZ = 270f)
                )
            }

            // Progress dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(minOf(totalTopics, 5)) { index ->
                    val actualIndex = when {
                        totalTopics <= 5 -> index
                        currentIndex < 2 -> index
                        currentIndex > totalTopics - 3 -> totalTopics - 5 + index
                        else -> currentIndex - 2 + index
                    }

                    Box(
                        modifier = Modifier
                            .size(
                                width = if (actualIndex == currentIndex) 20.dp else 7.dp,
                                height = 7.dp
                            )
                            .background(
                                color = if (actualIndex == currentIndex)
                                    Color(0xFF6C63FF)
                                else
                                    Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(3.5.dp)
                            )
                            .clickable {
                                onValueChange(actualIndex)
                                onFinalValue()
                            }
                    )
                }
            }

            // Next button
            IconButton(
                onClick = {
                    onValueChange.invoke((currentIndex + 1).coerceIn(0, totalTopics - 1))
                    onFinalValue.invoke()
                },
                enabled = currentIndex < totalTopics - 1,
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = if (currentIndex < totalTopics - 1) Color(0xFF6C63FF).copy(alpha = 0.1f) else Color.Transparent,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Next Topic",
                    tint = if (currentIndex < totalTopics - 1) Color(0xFF6C63FF) else Color(0xFFB0B3B8),
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer(rotationZ = 270f)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabeledSlider(
    currentIndex: Int,
    totalTopics: Int,
    onValueChange: (Int) -> Unit,
    onFinalValue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Slider(
                value = currentIndex.toFloat(),
                onValueChange = {
                    onValueChange(it.toInt())
                },
                onValueChangeFinished = {
                    onFinalValue.invoke()
                },
                valueRange = 0f..(totalTopics - 1).toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF1F1F1F),
                    activeTrackColor = Color(0xFFAAAAAA),
                    inactiveTrackColor = Color(0xFFE0E0E0)
                ),
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(28.dp) // Increased size to fit the number
                            .background(Color(0xFF1F1F1F), shape = CircleShape)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${currentIndex + 1}",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                track = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(50))
                    )
                }
            )
        }
    }
}


@Composable
fun Bullet(text: String, subBullets: List<String> = emptyList()) {
    val baseSize = LocalBaseFontSize.current
    val highlightRange = LocalHighlightRange.current
    val fullText = LocalFullText.current
    val rotation = textRotations[text.hashCode().absoluteValue % textRotations.size]
    val inkColor = if (text.hashCode() % 3 == 0) bodyColorVariant1 else bodyColor

    // Check if this text contains highlighted portion
    val startInFull = fullText.indexOf(text)
    val shouldHighlight = highlightRange != null && startInFull >= 0 &&
                         highlightRange.first >= startInFull &&
                         highlightRange.first < startInFull + text.length

    Column(modifier = Modifier.padding(start = 19.dp, bottom = 0.dp)) {
        if (shouldHighlight && highlightRange != null) {
            val localStart = (highlightRange.first - startInFull).coerceAtLeast(0)
            val localEnd = (highlightRange.second - startInFull).coerceAtMost(text.length)

            if (localStart >= 0 && localStart < localEnd && localEnd <= text.length) {
                val annotatedText = buildAnnotatedString {
                    append("•  ")
                    if (localStart > 0) {
                        append(text.substring(0, localStart))
                    }
                    withStyle(SpanStyle(background = Color(0xFFFFEB3B), color = Color(0xFF2D3436))) {
                        append(text.substring(localStart, localEnd))
                    }
                    if (localEnd < text.length) {
                        append(text.substring(localEnd))
                    }
                }

                Text(
                    text = annotatedText,
                    style = TextStyle(
                        fontFamily = kalamFont,
                        fontSize = (baseSize + 1).sp,
                        lineHeight = (baseSize + 2).sp,
                        color = inkColor
                    ),
                    modifier = Modifier
                        .padding(vertical = 1.dp)
                        .graphicsLayer { rotationZ = rotation * 0.3f }
                )

                // Sub-bullets
                if (subBullets.isNotEmpty()) {
                    Column(modifier = Modifier.padding(start = 34.dp, top = 2.dp)) {
                        subBullets.forEachIndexed { index, sub ->
                            val subRotation = textRotations[(sub.hashCode() + index).absoluteValue % textRotations.size]
                            val subInkColor = if (index % 2 == 0) bodyColorVariant2 else bodyColorVariant1
                            Row(modifier = Modifier.padding(bottom = 0.dp)) {
                                Text(
                                    "◦ $sub",
                                    style = TextStyle(
                                        fontFamily = kalamFont,
                                        fontSize = baseSize.sp,
                                        lineHeight = (baseSize + 1).sp,
                                        color = subInkColor
                                    ),
                                    modifier = Modifier.graphicsLayer { rotationZ = subRotation * 0.25f }
                                )
                            }
                        }
                    }
                }
                return
            }
        }

        // Normal bullet without highlighting
        Text(
            "•  $text",
            style = TextStyle(
                fontFamily = kalamFont,
                fontSize = (baseSize + 1).sp,
                lineHeight = (baseSize + 2).sp,
                color = inkColor
            ),
            modifier = Modifier
                .padding(vertical = 1.dp)
                .graphicsLayer {
                    rotationZ = rotation * 0.3f
                }
        )

        if (subBullets.isNotEmpty()) {
            Column(modifier = Modifier.padding(start = 34.dp, top = 2.dp)) {
                subBullets.forEachIndexed { index, sub ->
                    val subRotation = textRotations[(sub.hashCode() + index).absoluteValue % textRotations.size]
                    val subInkColor = if (index % 2 == 0) bodyColorVariant2 else bodyColorVariant1

                    Row(modifier = Modifier.padding(bottom = 0.dp)) {
                        Text(
                            "◦ $sub",
                            style = TextStyle(
                                fontFamily = kalamFont,
                                fontSize = baseSize.sp,
                                lineHeight = (baseSize + 1).sp,
                                color = subInkColor
                            ),
                            modifier = Modifier
                                .graphicsLayer {
                                    rotationZ = subRotation * 0.25f
                                }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun NoteImage(resId: Int, height: Dp = 140.dp, onImageClick: () -> Unit = {}) {
    // Image with slight rotation and shadow for "pasted photo" effect
    val rotation = textRotations[resId % textRotations.size]

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        // Shadow layer
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(height)
                .align(Alignment.Center)
                .graphicsLayer {
                    rotationZ = rotation * 0.4f
                    translationY = 4f
                    translationX = 4f
                }
                .background(Color.Black.copy(alpha = 0.15f))
        )

        // Actual image
        Image(
            painter = painterResource(id = resId),
            contentDescription = "Note Image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(height)
                .align(Alignment.Center)
                .graphicsLayer {
                    rotationZ = rotation * 0.4f // Slight rotation like taped photo
                }
                .background(Color.White) // White border like printed photo
                .padding(4.dp) // Create white border effect
                .clickable(true) {
                    onImageClick.invoke()
                }
        )
    }
}


@Composable
fun PaginatedNotes(
    notes: List<NoteItem>,
    background: Int,
    screenHeight: Dp,
    horizontalPadding: Dp = 12.dp,
    verticalPadding: Dp = 16.dp,
    modifier: Modifier = Modifier,
    onImageClick: (Int) -> Unit = { _ -> },
    scrollState: androidx.compose.foundation.ScrollState,
    highlightRange: Pair<Int, Int>? = null,
    fullNoteText: String = ""
) {

    // Zoom state
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 3f) // Min 1x (original), Max 3x zoom

        // Only allow panning when zoomed in
        if (scale > 1f) {
            offsetX += panChange.x
            offsetY += panChange.y
        } else {
            offsetX = 0f
            offsetY = 0f
        }
    }

    LaunchedEffect(notes) {
        scrollState.animateScrollTo(0)
        // Reset zoom when notes change
        scale = 1f
        offsetX = 0f
        offsetY = 0f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .transformable(state = transformableState)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = offsetX
                translationY = offsetY
            }
            .verticalScroll(scrollState)
    ) {
        PaginatedColumn(
            pageHeight = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE)
                screenHeight.times(0.92f)
            else
                screenHeight.times(0.72f),
            horizontalPadding = horizontalPadding,
            verticalPadding = verticalPadding,
            pageContent = { _, content ->
                NotePage(
                    screenHeight = screenHeight,
                    background = background,
                    content = content
                )
            }
        ) {
            notes.forEach { item ->
                when (item) {
                    is NoteItem.TopicName -> NoteTitleText(item.text)
                    is NoteItem.Image -> NoteImage(
                        item.resId, item.height.dp, onImageClick = {
                            onImageClick(item.resId)
                        })

                    is NoteItem.Heading -> NoteText(
                        item.text,
                        isHeading = true,
                    )

                    is NoteItem.Paragraph -> NoteText(item.text)

                    is NoteItem.Bullet -> Bullet(item.text, item.subBullets)
                }
            }
        }
    }
}

@Composable
fun PaginatedColumn(
    modifier: Modifier = Modifier,
    pageHeight: Dp,
    horizontalPadding: Dp,
    verticalPadding: Dp,
    pageContent: @Composable (page: Int, @Composable () -> Unit) -> Unit,
    content: @Composable () -> Unit
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val contentConstraints = constraints.copy(
            maxWidth = constraints.maxWidth - (horizontalPadding * 2).roundToPx()
        )

        val contentMeasurables = subcompose("content", content).map {
            it.measure(contentConstraints.copy(maxHeight = Dp.Infinity.roundToPx()))
        }

        val pages = mutableListOf<List<Placeable>>()
        var currentPage = mutableListOf<Placeable>()
        var currentHeight = 0
        val maxHeight = pageHeight.toPx().toInt() - (verticalPadding * 2).roundToPx()

        contentMeasurables.forEach { placeable ->
            if (currentHeight + placeable.height > maxHeight) {
                if (currentPage.isNotEmpty()) pages.add(currentPage)
                currentPage = mutableListOf(placeable)
                currentHeight = placeable.height
            } else {
                currentPage.add(placeable)
                currentHeight += placeable.height
            }
        }
        if (currentPage.isNotEmpty()) pages.add(currentPage)

        val totalHeight = pages.size * pageHeight.roundToPx()

        layout(constraints.maxWidth, totalHeight) {
            var yOffset = 0
            val xOffset = horizontalPadding.roundToPx()
            val yPad = verticalPadding.roundToPx()

            pages.forEachIndexed { pageIndex, placeables ->
                val pageMeasurables = subcompose("page_$pageIndex") {
                    pageContent(pageIndex) {}
                }.map { it.measure(constraints) }

                pageMeasurables.forEach { it.placeRelative(0, yOffset) }

                var itemY = yPad
                placeables.forEach { placeable ->
                    placeable.placeRelative(xOffset, yOffset + itemY)
                    itemY += placeable.height
                }

                yOffset += pageHeight.roundToPx()
            }
        }
    }
}

@Composable
fun NotePage(screenHeight: Dp, background: Int, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE)
                    screenHeight.times(0.92f)
                else
                    screenHeight.times(0.72f)
            )
            .padding(bottom = 0.dp)
    ) {
        // Paper background
        Image(
            painter = painterResource(id = background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Bottom margin line (like real notebook paper)
        Row(
            Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.BottomCenter)
        ) {
            Spacer(Modifier.width(3.dp))
            Box(
                modifier = Modifier
                    .widthIn(min = 0.dp, max = Dp.Infinity)
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color(0xFFB0B0B0))
            )
        }

        // Left red margin line (like real notebook paper)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
                .padding(start = 38.dp)
                .background(Color(0xFFE57373).copy(alpha = 0.7f)) // Red margin line
        )

        // Content area with proper margins
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 48.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)
        ) {
            content()
        }
    }
}

@Composable
fun NoteText(text: String, isHeading: Boolean = false) {
    val baseSize = LocalBaseFontSize.current
    val highlightRange = LocalHighlightRange.current
    val fullText = LocalFullText.current
    val rotation = textRotations[text.hashCode().absoluteValue % textRotations.size]

    // Ink color variation for authenticity
    val textColor = if (isHeading) {
        headingColor
    } else {
        when (text.hashCode() % 3) {
            0 -> bodyColorVariant1
            1 -> bodyColorVariant2
            else -> bodyColor
        }
    }

    // Check if this text contains highlighted portion
    val startInFull = fullText.indexOf(text)
    val shouldHighlight = highlightRange != null && startInFull >= 0 &&
                         highlightRange.first >= startInFull &&
                         highlightRange.first < startInFull + text.length

    if (shouldHighlight && highlightRange != null) {
        // Calculate local highlight position
        val localStart = (highlightRange.first - startInFull).coerceAtLeast(0)
        val localEnd = (highlightRange.second - startInFull).coerceAtMost(text.length)

        if (localStart >= 0 && localStart < localEnd && localEnd <= text.length) {
            // Use AnnotatedString for highlighting
            val annotatedText = buildAnnotatedString {
                if (localStart > 0) {
                    append(text.substring(0, localStart))
                }
                withStyle(SpanStyle(background = Color(0xFFFFEB3B), color = Color(0xFF2D3436))) {
                    append(text.substring(localStart, localEnd))
                }
                if (localEnd < text.length) {
                    append(text.substring(localEnd))
                }
            }

            Text(
                text = annotatedText,
                style = TextStyle(
                    fontFamily = kalamFont,
                    fontSize = if (isHeading) (baseSize + 2).sp else baseSize.sp,
                    fontWeight = FontWeight.Light,
                    lineHeight = (baseSize + if (isHeading) 4 else 2).sp,
                    color = textColor
                ),
                modifier = Modifier
                    .padding(
                        top = if (isHeading) 8.dp else 4.dp,
                        bottom = if (isHeading) 8.dp else 4.dp,
                        start = if (isHeading) 0.dp else 12.dp
                    )
                    .graphicsLayer {
                        rotationZ = rotation * if (isHeading) 0.2f else 0.25f
                    }
            )
            return
        }
    }

    // Normal text without highlighting
    Text(
        text = text,
        style = TextStyle(
            fontFamily = kalamFont,
            fontSize = if (isHeading) (baseSize + 2).sp else baseSize.sp,
            fontWeight = FontWeight.Light,
            lineHeight = (baseSize + if (isHeading) 4 else 2).sp,
            color = textColor
        ),
        modifier = Modifier
            .padding(
                top = if (isHeading) 8.dp else 4.dp,
                bottom = if (isHeading) 8.dp else 4.dp,
                start = if (isHeading) 0.dp else 12.dp
            )
            .graphicsLayer {
                rotationZ = rotation * if (isHeading) 0.2f else 0.25f
            }
    )
}

@Composable
fun NoteTitleText(text: String) {
    val baseSize = LocalBaseFontSize.current
    // Slight rotation for handwritten authenticity
    val rotation = textRotations[text.hashCode().absoluteValue % textRotations.size]

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = kalamFont,
                fontSize = (baseSize + 9).sp,
                fontWeight = FontWeight.Light,
                lineHeight = (baseSize + 17).sp,
                color = headingColor,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth(0.9f)
                .graphicsLayer {
                    rotationZ = rotation * 0.15f // Very subtle rotation for titles
                }
        )

        // Hand-drawn underline effect
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(2.dp)
                .background(headingColor.copy(alpha = 0.6f))
                .graphicsLayer {
                    rotationZ = rotation * 0.1f // Slightly rotated underline
                }
        )
    }
}

