package com.cbse.class10thsciencenotes.ui.pages

import android.content.res.Configuration
import android.util.Log
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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

// NEW: Data class to represent text segments with precise positions
data class TextSegment(
    val originalText: String,
    val cleanedText: String,
    val startPos: Int,
    val endPos: Int,
    val noteItem: NoteItem // Keep original item for rendering
)

// Helper to remove emojis but keep all text content
fun cleanTextFromEmojis(text: String): String {
    // Remove emojis while preserving all regular text
    return text
        // Remove emoji ranges (comprehensive Unicode emoji blocks)
        .replace(Regex("[\uD83C-\uDBFF\uDC00-\uDFFF]"), "")
        .replace(Regex("[\u2600-\u27BF]"), "") // Misc symbols
        .replace(Regex("[\uE000-\uF8FF]"), "") // Private use area
        .replace(Regex("[\u2011-\u26FF]"), "") // Dingbats, arrows, etc.
        .replace(Regex("[\uFE0E-\uFE0F]"), "") // Variation selectors
        // Keep everything else: letters, numbers, punctuation, spaces
        .replace(Regex("\\s+"), " ") // Normalize multiple spaces
        .trim()
}

// NEW: Extracts text and creates a map of segments with their exact positions
fun extractTextFromNotesWithPositions(notes: List<NoteItem>): Pair<String, List<TextSegment>> {
    val segments = mutableListOf<TextSegment>()
    var currentPos = 0
    val fullText = buildString {
        notes.forEach { item ->
            val textProvider: String
            when (item) {
                is NoteItem.TopicName -> textProvider = item.text
                is NoteItem.Heading -> textProvider = item.text
                is NoteItem.Paragraph -> textProvider = item.text
                is NoteItem.Bullet -> {
                    textProvider = item.text
                    // Handle sub-bullets as separate segments
                    item.subBullets.forEach { subBullet ->
                        val cleanedSub = cleanTextFromEmojis(subBullet)
                        if (cleanedSub.isNotEmpty()) {
                            var textToAppend = cleanedSub
                            if (!textToAppend.endsWith(".") && !textToAppend.endsWith("?") && !textToAppend.endsWith("!")) {
                                textToAppend += "."
                            }
                            textToAppend += " "
                            // Create a new NoteItem for the sub-bullet to render it correctly
                            val subBulletItem = NoteItem.Bullet(text = subBullet, subBullets = emptyList())
                            segments.add(TextSegment(subBullet, cleanedSub, currentPos, currentPos + textToAppend.length, subBulletItem))
                            append(textToAppend)
                            currentPos += textToAppend.length
                        }
                    }
                }
                is NoteItem.Image -> {
                    // Add a segment for the image so it's included in the layout
                    segments.add(TextSegment("", "", currentPos, currentPos, item))
                    textProvider = ""
                }
            }

            if (textProvider.isNotEmpty()) {
                val cleaned = cleanTextFromEmojis(textProvider)
                if (cleaned.isNotEmpty()) {
                    var textToAppend = cleaned
                    if (!textToAppend.endsWith(".") && !textToAppend.endsWith("?") && !textToAppend.endsWith("!")) {
                        textToAppend += "."
                    }
                    textToAppend += " "
                    segments.add(TextSegment(textProvider, cleaned, currentPos, currentPos + textToAppend.length, item))
                    append(textToAppend)
                    currentPos += textToAppend.length
                }
            }
        }
    }
    return fullText to segments
}

// Highlighted Text Composable for TTS word highlighting
//@Composable
//fun HighlightedText(
//    text: String,
//    highlightRange: Pair<Int, Int>?,
//    style: TextStyle,
//    modifier: Modifier = Modifier
//) {
//    val annotatedString = remember(text, highlightRange) {
//        buildAnnotatedString {
//            if (highlightRange != null &&
//                highlightRange.first >= 0 &&
//                highlightRange.first < text.length &&
//                highlightRange.second > highlightRange.first &&
//                highlightRange.second <= text.length) {
//                // Before highlight
//                append(text.substring(0, highlightRange.first))
//
//                // Highlighted portion
//                withStyle(
//                    SpanStyle(
//                        background = Color(0xFFFFEB3B), // Yellow highlight
//                        color = Color(0xFF2D3436)
//                    )
//                ) {
//                    append(text.substring(highlightRange.first, highlightRange.second))
//                }
//
//                // After highlight
//                if (highlightRange.second < text.length) {
//                    append(text.substring(highlightRange.second))
//                }
//            } else {
//                append(text)
//            }
//        }
//    }
//
//    Text(
//        text = annotatedString,
//        style = style,
//        modifier = modifier
//    )
//}

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

    // --- TTS State Refactor ---
    var currentHighlightRange by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var isTTSPlaying by remember { mutableStateOf(false) }
    var isTTSPaused by remember { mutableStateOf(false) }
    var speechRate by remember { mutableStateOf(0.8f) }
    var fullNoteText by remember { mutableStateOf("") }
    var textSegments by remember { mutableStateOf<List<TextSegment>>(emptyList()) }

    // Anchor and Offset state
    var playbackChunkOffset by remember { mutableIntStateOf(0) }
    var lastWordStartIndex by remember { mutableIntStateOf(0) }

    var showTTSControls by remember { mutableStateOf(false) }

    // TTS Manager with position tracking
    val ttsManager = rememberTextToSpeechManager(
        onWordSpoken = { _, start, end ->
            // Use the offset to calculate the global position
            val globalStart = start + playbackChunkOffset
            val globalEnd = end + playbackChunkOffset
            currentHighlightRange = Pair(globalStart, globalEnd)
            // Track the start of the last spoken word for accurate resume
            lastWordStartIndex = globalStart
            Log.d("NotesScreen", "Word spoken at global range: [$globalStart, $globalEnd]")
        },
        onComplete = {
            isTTSPlaying = false
            isTTSPaused = false
            currentHighlightRange = null
            lastWordStartIndex = 0
            playbackChunkOffset = 0
            Log.d("NotesScreen", "TTS completed")
        }
    )

    // Unified function to play, resume, or change speed
    fun playOrResumeTTS(fromIndex: Int = 0, rate: Float = speechRate) {
        ttsManager.stop() // Always stop before starting
        ttsManager.setSpeechRate(rate)

        if (fromIndex < fullNoteText.length) {
            val textToSpeak = fullNoteText.substring(fromIndex)
            playbackChunkOffset = fromIndex // Set the anchor
            ttsManager.speak(textToSpeak)
            isTTSPlaying = true
            isTTSPaused = false
            showTTSControls = true
        } else {
            // Reached the end of the text
            isTTSPlaying = false
            isTTSPaused = false
            showTTSControls = false
        }
    }

    // Reset TTS when topic changes
    LaunchedEffect(currentIndex) {
        if (isTTSPlaying || isTTSPaused) {
            Log.d("NotesScreen", "Topic changed - resetting TTS")
            ttsManager.stop()
            isTTSPlaying = false
            isTTSPaused = false
            currentHighlightRange = null
            lastWordStartIndex = 0
            playbackChunkOffset = 0
            showTTSControls = false
        }
    }

    LaunchedEffect(jsonFileName) {
        viewModel.loadChapter(context, jsonFileName)
        // Reset TTS on chapter change
        Log.d("NotesScreen", "Chapter changed - resetting TTS")
        ttsManager.stop()
        isTTSPlaying = false
        isTTSPaused = false
        currentHighlightRange = null
        lastWordStartIndex = 0
        playbackChunkOffset = 0
        showTTSControls = false
    }

    // Extract full text and segments when notes load
    LaunchedEffect(uiState.currentNotes) {
        val (text, segments) = extractTextFromNotesWithPositions(uiState.currentNotes)
        fullNoteText = text
        textSegments = segments
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
                // Enhanced Play/Pause/Resume FAB
                if (!isTTSPlaying &&   !isTTSPaused) {
                    androidx.compose.material3.FloatingActionButton(
                        onClick = {
                            playOrResumeTTS(fromIndex = 0)
                        },
                        containerColor = Color(0xFF6C63FF), // Purple for start
                        modifier = Modifier.padding(bottom = if (isNavigationVisible) 80.dp else 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start Reading",
                            tint = Color.White
                        )
                    }
                }
            },
            bottomBar = {
                Column {
                    // TTS Controls (show when playing OR paused)
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showTTSControls,
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
                            isPaused = isTTSPaused,
                            onPlayPause = {
                                when {
                                    isTTSPlaying -> { // User wants to PAUSE
                                        Log.d("NotesScreen", "Pausing at position: $lastWordStartIndex")
                                        ttsManager.stop()
                                        isTTSPlaying = false
                                        isTTSPaused = true
                                    }
                                    isTTSPaused -> { // User wants to RESUME
                                        Log.d("NotesScreen", "Resuming from position: $lastWordStartIndex")
                                        playOrResumeTTS(fromIndex = lastWordStartIndex)
                                    }
                                    else -> { // User wants to PLAY from start
                                        Log.d("NotesScreen", "Starting TTS from beginning")
                                        playOrResumeTTS(fromIndex = 0)
                                    }
                                }
                            },
                            onStop = {
                                // STOP - reset everything
                                Log.d("NotesScreen", "Stop button clicked")
                                ttsManager.stop()
                                isTTSPlaying = false
                                isTTSPaused = false
                                currentHighlightRange = null
                                lastWordStartIndex = 0
                                playbackChunkOffset = 0
                                showTTSControls = false
                            },
                            speechRate = speechRate,
                            onSpeechRateChange = { rate ->
                                speechRate = rate
                                Log.d("NotesScreen", "Speech rate changed to: $rate")

                                // If playing, restart from the last word with the new speed
                                if (isTTSPlaying) {
                                    playOrResumeTTS(fromIndex = lastWordStartIndex, rate = rate)
                                } else {
                                    // If paused, just update the rate for the next play action
                                    ttsManager.setSpeechRate(rate)
                                }
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
                // No TopAppBar for landscape, handled by custom header
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
                        onSettingsClick = { showSettingsDialog = true },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    PaginatedNotes(
                        segments = textSegments,
                        background = R.drawable.page_bg,
                        screenHeight = screenHeight,
                        modifier = Modifier.weight(1f),
                        onImageClick = { viewingImageResId = it },
                        scrollState = scrollState
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
fun NotesScreenHeader(chapterTitle: String, onSettingsClick: () -> Unit, onBackClick: () -> Unit) {
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
            // Back button (LEFT)
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

            // Title (CENTER)
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
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

            // Settings button (RIGHT)
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

val kalamFont = FontFamily(Font(R.font.hand_written))

// Handwritten ink colors - multiple shades for authenticity
val headingColor = Color(0xFF0D47A1)  // Slightly darker blue for headings
val bodyColor = Color(0xFF1565C0)     // Main ink blue
val bodyColorVariant1 = Color(0xFF1976D2)  // Slightly lighter for variation
val bodyColorVariant2 = Color(0xFF0D47A1)  // Slightly darker for variation

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
        // Topic name on LEFT, Topic indicator on RIGHT
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Topic name (LEFT side)
            Text(
                text = currentTopicName,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2D3436),
                fontSize = 13.sp,
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                maxLines = 1
            )

            // Topic indicator (RIGHT side)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
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
                        text = "${currentIndex + 1}/$totalTopics",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

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
                            })
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


@Composable
fun Bullet(segment: TextSegment) {
    val baseSize = LocalBaseFontSize.current
    val highlightRange = LocalHighlightRange.current
    val rotation = textRotations[segment.originalText.hashCode().absoluteValue % textRotations.size]
    val inkColor = if (segment.originalText.hashCode() % 3 == 0) bodyColorVariant1 else bodyColor

    // Check if this text contains highlighted portion
    val shouldHighlight = highlightRange != null &&
            highlightRange.first >= segment.startPos &&
            highlightRange.first < segment.endPos

    if (shouldHighlight) {
        val localStart = (highlightRange.first - segment.startPos).coerceAtLeast(0)
        val localEnd = (highlightRange.second - segment.startPos).coerceAtMost(segment.cleanedText.length)

        if (localStart >= 0 && localStart < localEnd && localEnd <= segment.cleanedText.length) {
            val annotatedText = buildAnnotatedString {
                append("•  ")
                if (localStart > 0) {
                    append(segment.cleanedText.substring(0, localStart))
                }
                withStyle(SpanStyle(background = Color(0xFFFF9800), color = Color.Black)) {
                    append(segment.cleanedText.substring(localStart, localEnd))
                }
                if (localEnd < segment.cleanedText.length) {
                    append(segment.cleanedText.substring(localEnd))
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
                    .padding(start = 19.dp, top = 1.dp, bottom = 1.dp)
                    .graphicsLayer { rotationZ = rotation * 0.3f }
            )
            return
        }
    }

    // Normal bullet without highlighting
    Text(
        "•  ${segment.cleanedText}",
        style = TextStyle(
            fontFamily = kalamFont,
            fontSize = (baseSize + 1).sp,
            lineHeight = (baseSize + 2).sp,
            color = inkColor
        ),
        modifier = Modifier
            .padding(start = 19.dp, top = 1.dp, bottom = 1.dp)
            .graphicsLayer {
                rotationZ = rotation * 0.3f
            }
    )
}


@Composable
fun SubBullet(segment: TextSegment) {
    val baseSize = LocalBaseFontSize.current
    val highlightRange = LocalHighlightRange.current
    val rotation =
        textRotations[segment.originalText.hashCode().absoluteValue % textRotations.size]
    val inkColor =
        if (segment.originalText.hashCode() % 2 == 0) bodyColorVariant2 else bodyColorVariant1

    val shouldHighlight = highlightRange != null &&
            highlightRange.first >= segment.startPos &&
            highlightRange.first < segment.endPos

    if (shouldHighlight) {
        val localStart = (highlightRange.first - segment.startPos).coerceAtLeast(0)
        val localEnd =
            (highlightRange.second - segment.startPos).coerceAtMost(segment.cleanedText.length)

        if (localStart >= 0 && localStart < localEnd && localEnd <= segment.cleanedText.length) {
            val annotatedText = buildAnnotatedString {
                append("◦ ")
                if (localStart > 0) {
                    append(segment.cleanedText.substring(0, localStart))
                }
                withStyle(
                    SpanStyle(
                        background = Color(0xFFFF9800), // bright orange
                        color = Color.Black
                    )
                ) {
                    append(segment.cleanedText.substring(localStart, localEnd))
                }
                if (localEnd < segment.cleanedText.length) {
                    append(segment.cleanedText.substring(localEnd))
                }
            }
            Text(
                text = annotatedText,
                style = TextStyle(
                    fontFamily = kalamFont,
                    fontSize = baseSize.sp,
                    lineHeight = (baseSize + 1).sp,
                    color = inkColor
                ),
                modifier = Modifier
                    .padding(start = 34.dp, bottom = 0.dp)
                    .graphicsLayer { rotationZ = rotation * 0.25f }
            )
            return
        }
    }

    Text(
        text = "◦ ${segment.cleanedText}",
        style = TextStyle(
            fontFamily = kalamFont,
            fontSize = baseSize.sp,
            lineHeight = (baseSize + 1).sp,
            color = inkColor
        ),
        modifier = Modifier
            .padding(start = 34.dp, bottom = 0.dp)
            .graphicsLayer { rotationZ = rotation * 0.25f }
    )
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
                })
    }
}


@Composable
fun PaginatedNotes(
    segments: List<TextSegment>, // Changed from notes
    background: Int,
    screenHeight: Dp,
    horizontalPadding: Dp = 12.dp,
    verticalPadding: Dp = 16.dp,
    modifier: Modifier = Modifier,
    onImageClick: (Int) -> Unit = { _ -> },
    scrollState: androidx.compose.foundation.ScrollState
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

    LaunchedEffect(segments) { // Changed from notes
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
            // Render notes from segments
            segments.forEach { segment ->
                when (val item = segment.noteItem) {
                    is NoteItem.TopicName -> NoteTitleText(item.text)
                    is NoteItem.Heading -> NoteText(segment, isHeading = true)
                    is NoteItem.Paragraph -> NoteText(segment)
                    is NoteItem.Bullet -> {
                        // A segment is a sub-bullet if its originalText is one of the sub-bullets of the NoteItem
                        if (item.subBullets.contains(segment.originalText)) {
                             SubBullet(segment)
                        } else {
                             Bullet(segment)
                        }
                    }
                    is NoteItem.Image -> NoteImage(resId = item.resId, onImageClick = { onImageClick(item.resId) })
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
fun NoteText(segment: TextSegment, isHeading: Boolean = false) {
    val baseSize = LocalBaseFontSize.current
    val highlightRange = LocalHighlightRange.current
    val rotation = textRotations[segment.originalText.hashCode().absoluteValue % textRotations.size]

    // Ink color variation for authenticity
    val textColor = if (isHeading) {
        headingColor
    } else {
        when (segment.originalText.hashCode() % 3) {
            0 -> bodyColorVariant1
            1 -> bodyColorVariant2
            else -> bodyColor
        }
    }

    // Check if this text contains highlighted portion
    val shouldHighlight = highlightRange != null &&
            highlightRange.first >= segment.startPos &&
            highlightRange.first < segment.endPos

    if (shouldHighlight) {
        // Calculate local highlight position
        val localStart = (highlightRange.first - segment.startPos).coerceAtLeast(0)
        val localEnd = (highlightRange.second - segment.startPos).coerceAtMost(segment.cleanedText.length)

        if (localStart >= 0 && localStart < localEnd && localEnd <= segment.cleanedText.length) {
            // Use AnnotatedString for highlighting
            val annotatedText = buildAnnotatedString {
                if (localStart > 0) {
                    append(segment.cleanedText.substring(0, localStart))
                }
                withStyle(SpanStyle(background = Color(0xFFFF9800), color = Color.Black)) {
                    append(segment.cleanedText.substring(localStart, localEnd))
                }
                if (localEnd < segment.cleanedText.length) {
                    append(segment.cleanedText.substring(localEnd))
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
        text = segment.cleanedText,
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
