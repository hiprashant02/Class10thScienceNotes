package com.cbse.class10thsciencenotes.ui.pages

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import com.cbse.class10thsciencenotes.data.Chapter
import kotlin.math.roundToInt

//@SuppressLint("SetJavaScriptEnabled")
//@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
//@Composable
//fun TopicViewerScreen(
//    chapter: Chapter,
//    onBack: () -> Unit
//) {
//    BackHandler {
//        onBack()
//    }
//
//    // Generate topic filenames for this chapter
//    val topicFiles = List(chapter.topicCount) { i ->
//        "ch${chapter.number}/ch${chapter.number}t${i + 1}.html"
//    }
//    val pagerState = rememberPagerState(pageCount = { topicFiles.size })
//    val webViewRef = remember { mutableStateOf<WebView?>(null) }
//
//    val isSystemDark = isSystemInDarkTheme()
//    var currentTopicIndex by remember { mutableStateOf(0) }
//    var isSeeking by remember { mutableStateOf(false) }
//    var seekTopicIndex by remember { mutableStateOf(0) }
//
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("${chapter.number}. ${chapter.name}") },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
//                    }
//                }
//            )
//        }
//    ) { innerPadding ->
//
//
//        val context = LocalContext.current
//        Box(Modifier.fillMaxSize().padding(innerPadding)) {
//            AndroidView(
//                modifier = Modifier
//                    .fillMaxSize(),
//                factory = { context ->
//                    WebView(context).apply {
//                        settings.javaScriptEnabled = true
//                        webViewClient = object : WebViewClient() {
//                            override fun onPageFinished(view: WebView?, url: String?) {
//                                Toast.makeText(context, url.toString(), Toast.LENGTH_SHORT).show()
//                                // Inject dark CSS if dark mode is enabled
//                                if (isSystemDark) {
//                                    view?.evaluateJavascript(
//                                        """
//             document.documentElement.classList.add('dark');
//             localStorage.setItem('night','1');
//             """.trimIndent(),
//                                        null
//                                    )
//                                } else {
//                                    view?.evaluateJavascript(
//                                        """
//             document.documentElement.classList.remove('dark');
//             localStorage.setItem('night','0');
//             """.trimIndent(),
//                                        null
//                                    )
//                                }
//                            }
//                        }
//                        loadUrl("file:///android_asset/${topicFiles[0]}")
//                        webViewRef.value = this
//                        settings.setSupportZoom(false)             // Disable zoom controls (pinch)
//                        settings.builtInZoomControls = false       // No zoom controls shown
//                        settings.displayZoomControls = false       // No zoom controls overlay
//
//                    }
//                },update = { webView ->
//                    webView.loadUrl("file:///android_asset/${topicFiles[currentTopicIndex]}")
//                })
//
//            IconButton(
//                onClick = { if (currentTopicIndex > 0) currentTopicIndex-- },
//                enabled = currentTopicIndex > 0,
//                modifier = Modifier
//                    .align(Alignment.CenterStart)
//                    .padding(12.dp)
//                    .background(
//                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
//                        shape = CircleShape
//                    )
//                    .shadow(8.dp, shape = CircleShape)
//            ) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
//                    contentDescription = "Previous",
//                    tint = MaterialTheme.colorScheme.onSurface,
//                    modifier = Modifier.size(32.dp)
//                )
//            }
//
//            // Next Button (Floating, right)
//            IconButton(
//                onClick = { if (currentTopicIndex < topicFiles.lastIndex) currentTopicIndex++ },
//                enabled = currentTopicIndex < topicFiles.lastIndex,
//                modifier = Modifier
//                    .align(Alignment.CenterEnd)
//                    .padding(12.dp)
//                    .background(
//                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
//                        shape = CircleShape
//                    )
//                    .shadow(8.dp, shape = CircleShape)
//            ) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
//                    contentDescription = "Next",
//                    tint = MaterialTheme.colorScheme.onSurface,
//                    modifier = Modifier.size(32.dp)
//                )
//            }
//
//            // Modern Progress Bar and Page Label at Bottom
//
//                val progressBarWidth = with(LocalDensity.current) { 220.dp.toPx() }
//
//                Box(
//                    modifier = Modifier
//                        .align(Alignment.BottomCenter)
//                        .padding(bottom = 24.dp)
//                        .width(220.dp)
//                        .height(40.dp)
//                        .pointerInput(topicFiles.size) {
//                            detectTapGestures(
//                                onPress = { offset ->
//                                    isSeeking = true
//                                    val x = offset.x
//                                    seekTopicIndex = ((x / progressBarWidth) * (topicFiles.lastIndex)).roundToInt()
//                                    tryAwaitRelease()
//                                    // On release
//                                    currentTopicIndex = seekTopicIndex.coerceIn(0, topicFiles.lastIndex)
//                                    isSeeking = false
//                                }
//                            )
//                        }
//                        .pointerInput(topicFiles.size) {
//                            detectDragGestures(
//                                onDragStart = { offset ->
//                                    isSeeking = true
//                                },
//                                onDragEnd = {
//                                    currentTopicIndex = seekTopicIndex.coerceIn(0, topicFiles.lastIndex)
//                                    isSeeking = false
//                                },
//                                onDragCancel = {
//                                    isSeeking = false
//                                },
//                                onDrag = { change, dragAmount ->
//                                    val localX = change.position.x
//                                    seekTopicIndex = ((localX / progressBarWidth) * (topicFiles.lastIndex)).roundToInt()
//                                }
//                            )
//                        }
//                ) {
//                    // Track
//                    LinearProgressIndicator(
//                        progress = {if (isSeeking) (seekTopicIndex + 1f) / topicFiles.size else (currentTopicIndex + 1f) / topicFiles.size},
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(6.dp)
//                            .clip(RoundedCornerShape(8.dp))
//                    )
//                    // Floating preview
//                    if (isSeeking) {
//                        val indicatorOffset =
//                            (seekTopicIndex.toFloat() / topicFiles.lastIndex).coerceIn(0f, 1f) * progressBarWidth
//                        Box(
//                            modifier = Modifier
//                                .offset { IntOffset(indicatorOffset.toInt() - 24, -28) }
//                                .background(
//                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
//                                    shape = RoundedCornerShape(12.dp)
//                                )
//                                .border(
//                                    1.dp,
//                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
//                                    RoundedCornerShape(12.dp)
//                                )
//                                .padding(horizontal = 12.dp, vertical = 4.dp)
//                        ) {
//                            Text(
//                                "Topic ${seekTopicIndex + 1}",
//                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
//                            )
//                        }
//                    }
//                    // Always show current page label below
//                    Box(
//                        modifier = Modifier
//                            .align(Alignment.BottomCenter)
//                            .padding(top = 16.dp)
//                    ) {
//                        Text(
//                            "Topic ${currentTopicIndex + 1} / ${topicFiles.size}",
//                            style = MaterialTheme.typography.labelMedium,
//                            color = MaterialTheme.colorScheme.onSurface,
//                            modifier = Modifier
//                                .background(
//                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
//                                    shape = RoundedCornerShape(8.dp)
//                                )
//                                .padding(horizontal = 12.dp, vertical = 4.dp)
//                                .shadow(2.dp, shape = RoundedCornerShape(8.dp))
//                        )
//                    }
//                }
//                Text(
//                    "Topic ${currentTopicIndex + 1} / ${topicFiles.size}",
//                    style = MaterialTheme.typography.labelMedium,
//                    color = MaterialTheme.colorScheme.onSurface,
//                    modifier = Modifier
//                        .background(
//                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
//                            shape = RoundedCornerShape(8.dp)
//                        )
//                        .padding(horizontal = 12.dp, vertical = 4.dp)
//                        .shadow(2.dp, shape = RoundedCornerShape(8.dp))
//                )
//            }
//
//
//
//    }
//
//}



//// work
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TopicViewerScreenWithSlider(
//    chapter: Chapter,
//    onBack: () -> Unit,
//    darkTheme: Boolean
//) {
//    var currentTopicIndex by remember { mutableStateOf(0) }
//    var tempTopicIndex by remember { mutableStateOf(0) }
//    val topicFiles = remember(chapter) {
//        List(chapter.topicCount) { i ->
//            "ch${chapter.number}/ch${chapter.number}t${i + 1}.html"
//        }
//    }
//    val webViewRef = remember { mutableStateOf<WebView?>(null) }
//    var shouldShowBottomNavBar = remember { mutableStateOf(true) }
//
//    // For showing slider preview label while sliding
//
//        Scaffold(
//            topBar = {
//                TopAppBar(
//                    title = { Text(chapter.name, style = MaterialTheme.typography.titleMedium) },
//                    navigationIcon = {
//                        IconButton(onClick = onBack) {
//                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                        }
//                    },
//                    actions = {
//                        IconButton(onClick = {
//                            webViewRef.value?.evaluateJavascript("toggleMode();", null)
//                        }) {
//                            Icon(Icons.Default.Person, contentDescription = "Toggle Night Mode")
//                        }
//                    }
//                )
//            },
//            containerColor = Color.Transparent,
//            content = { paddingValues ->
//
//                Column(modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues).background(MaterialTheme.colorScheme.surface), horizontalAlignment = Alignment.CenterHorizontally) {
//                    AndroidView(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .weight(1f),
//                        factory = { context ->
//                            WebView(context).apply {
//                                settings.javaScriptEnabled = true
//                                settings.setSupportZoom(false)
//                                settings.builtInZoomControls = false
//                                settings.displayZoomControls = false
//                                webViewClient = object : WebViewClient() {
//                                    override fun onPageFinished(view: WebView?, url: String?) {
//                                        if (darkTheme) {
//                                            view?.evaluateJavascript(
//                                                """
//                                                 document.documentElement.classList.add('dark');
//                                                 localStorage.setItem('night','1');
//                                                 """.trimIndent(),
//                                                null
//                                            )
//                                        } else {
//                                            view?.evaluateJavascript(
//                                                """
//                                                 document.documentElement.classList.remove('dark');
//                                                 localStorage.setItem('night','0');
//                                                 """.trimIndent(),
//                                                null
//                                            )
//                                        }
//                                    }
//                                }
//                                loadUrl("file:///android_asset/${topicFiles[currentTopicIndex]}")
//                                webViewRef.value = this
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                    setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
//
//                                        val atTop = scrollY == 0
//                                        var scrolledBackward = false
//                                        // Check scroll direction
//                                        if (scrollY < oldScrollY) {
//                                            // Scrolled up (backward)
//                                            scrolledBackward = true
//                                        } else if (scrollY > oldScrollY) {
//                                            // Scrolled down (forward)
//                                            scrolledBackward = false
//                                        }
//
//                                        Log.i("offset mine", scrollY.toString())
//                                        if (atTop || scrolledBackward) {
//                                            shouldShowBottomNavBar.value = true
//                                        } else {
//                                            shouldShowBottomNavBar.value = false
//                                        }
//                                    }
//                                }
//                            }
//                        },
//                        update = { webView ->
//                            webView.loadUrl("file:///android_asset/${topicFiles[currentTopicIndex]}")
//                        }
//                    )
//                    AnimatedVisibility(visible = shouldShowBottomNavBar.value) {
//                        BoxWithConstraints(modifier= Modifier) {
//                           val width = this.maxWidth;
//                            Row(
//                                horizontalArrangement = Arrangement.SpaceEvenly,
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                            ) {
//                                // Floating Prev/Next Buttons (left/right)
//
//                                IconButton(
//                                    onClick = {
//                                        if (currentTopicIndex > 0) {
//                                            currentTopicIndex--
//                                            tempTopicIndex--
//                                        }
//                                    },
//                                    enabled = currentTopicIndex > 0,
//                                    modifier = Modifier
//                                        .padding(12.dp)
//                                        .size(42.dp)
//                                        .clip(CircleShape)
//                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 1f)),
//                                ) {
//                                    Icon(
//                                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
//                                        contentDescription = "Previous",
//                                        tint = MaterialTheme.colorScheme.onSurface,
//                                    )
//                                }
//
//
//                                //                            IconButton(
//                                //                                onClick = {
//                                //                                    if (currentTopicIndex > 0) {
//                                //                                        currentTopicIndex--
//                                //                                        tempTopicIndex--
//                                //                                    }
//                                //                                },
//                                //                                enabled = currentTopicIndex > 0,
//                                //                                modifier = Modifier
//                                //                                    .padding(12.dp)
//                                //                                    .background(
//                                //                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
//                                //                                        shape = CircleShape
//                                //                                    )
//                                //                            ) {
//                                //                                Icon(
//                                //                                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
//                                //                                    contentDescription = "Previous",
//                                //                                    tint = MaterialTheme.colorScheme.onSurface,
//                                //                                    modifier = Modifier.size(32.dp)
//                                //                                )
//                                //                            }
//                                TopicSliderWithCircleLabel(
//                                    currentIndex = tempTopicIndex.toFloat(),
//                                    onSlideChange = { value ->
//                                        tempTopicIndex =
//                                            value.roundToInt().coerceIn(0, topicFiles.lastIndex)
//                                        //                    webViewRef.value?.loadUrl("file:///android_asset/${topicFiles[currentTopicIndex]}")
//                                    },
//                                    onSlideFinished = {
//                                        currentTopicIndex = tempTopicIndex
//                                        //                    webViewRef.value?.loadUrl("file:///android_asset/${topicFiles[currentTopicIndex]}")
//                                    },
//                                    topicCount = chapter.topicCount, modifier = Modifier.width(width * 0.6f)
//                                        .padding(horizontal = 16.dp, vertical = 8.dp)
//                                )
//                                //                            IconButton(
//                                //                                onClick = {
//                                //                                    if (currentTopicIndex < topicFiles.lastIndex) {
//                                //                                        currentTopicIndex++
//                                //                                        tempTopicIndex++
//                                //                                    }
//                                //                                },
//                                //                                enabled = currentTopicIndex < topicFiles.lastIndex,
//                                //                                modifier = Modifier
//                                //                                    .padding(12.dp)
//                                //                                    .background(
//                                //                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
//                                //                                        shape = CircleShape
//                                //                                    )
//                                //                            ) {
//                                //                                Icon(
//                                //                                    imageVector = Icons.Default.KeyboardArrowRight,
//                                //                                    contentDescription = "Next",
//                                //                                    tint = MaterialTheme.colorScheme.onSurface,
//                                //                                    modifier = Modifier.size(32.dp)
//                                //                                )
//                                //                            }
//
//                                IconButton(
//                                    onClick = {
//                                        if (currentTopicIndex < topicFiles.lastIndex) {
//                                            currentTopicIndex++
//                                            tempTopicIndex++
//                                        }
//                                    },
//                                    enabled = currentTopicIndex < topicFiles.lastIndex,
//                                    modifier = Modifier
//                                        .padding(12.dp)
//                                        .size(42.dp)
//                                        .clip(CircleShape)
//                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 1f)),
//                                ) {
//                                    Icon(
//                                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
//                                        contentDescription = "Previous",
//                                        tint = MaterialTheme.colorScheme.onSurface,
//                                    )
//                                }
//                            }
//                        }
//                    }
//                            Text(
//                                "Topic ${currentTopicIndex + 1} / ${topicFiles.size}",
//                                style = MaterialTheme.typography.labelMedium,
//                                color = MaterialTheme.colorScheme.onSurface,
//                                modifier = Modifier
//                                    .background(
//                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
//                                        shape = CircleShape
//                                    )
//                                    .padding(horizontal = 14.dp, vertical = 4.dp)
//                            )
//
//
//                }
//
//
//            }
//        )
//




        // Topic Slider with floating preview
//        Box(
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(bottom = 30.dp)
//                .fillMaxWidth(),
//            contentAlignment = Alignment.Center
//        ) {
//            val sliderWidth = with(LocalDensity.current) { 220.dp.toPx() }
//            // Preview label above slider thumb
//            if (isSliding) {
//                val offset = (sliderPosition / (topicFiles.lastIndex).coerceAtLeast(1)) * sliderWidth
//                Box(
//                    modifier = Modifier
//                        .offset { IntOffset(offset.toInt() - 24, -36) }
//                        .background(
//                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
//                            shape = CircleShape
//                        )
//                        .padding(horizontal = 16.dp, vertical = 6.dp)
//                        .shadow(2.dp, CircleShape)
//                ) {
//                    Text(
//                        "Topic ${sliderPosition.roundToInt() + 1}",
//                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
//                    )
//                }
//            }
//
//            // The Slider itself
//            Slider(
//                value = sliderPosition,
//                onValueChange = { value ->
//                    isSliding = true
//                    sliderPosition = value
//                },
//                onValueChangeFinished = {
//                    currentTopicIndex = sliderPosition.roundToInt().coerceIn(0, topicFiles.lastIndex)
//                    sliderPosition = currentTopicIndex.toFloat()
//                    isSliding = false
//                },
//                valueRange = 0f..topicFiles.lastIndex.toFloat(),
//                steps = (topicFiles.size - 2).coerceAtLeast(0),
//                modifier = Modifier,
//            )
//
//            // Page indicator under slider
//            Box(
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(top = 38.dp)
//            ) {
//                Text(
//                    "Topic ${currentTopicIndex + 1} / ${topicFiles.size}",
//                    style = MaterialTheme.typography.labelMedium,
//                    color = MaterialTheme.colorScheme.onSurface,
//                    modifier = Modifier
//                        .background(
//                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
//                            shape = CircleShape
//                        )
//                        .padding(horizontal = 14.dp, vertical = 6.dp)
//                )
//            }
//        }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TopicSliderWithCircleLabel(
//    currentIndex: Float,
//    onSlideChange: (Float) -> Unit,
//    onSlideFinished: () -> Unit,
//    topicCount: Int,modifier: Modifier= Modifier
//) {
//    val range = 0f..(topicCount - 1).toFloat()
//    val steps = maxOf(topicCount - 2, 0)
//
//    val interaction = remember { MutableInteractionSource() }
//
//    Slider(
//        value = currentIndex,
//        onValueChange = onSlideChange,
//        onValueChangeFinished = onSlideFinished,
//        valueRange = range,
//        interactionSource = interaction,
//        modifier = modifier
//            .wrapContentHeight(),
//        colors = SliderDefaults.colors(
////            thumbColor = MaterialTheme.colorScheme.primary,
////            activeTrackColor = MaterialTheme.colorScheme.primary,
////            inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
//        ),
//        thumb = {
//            Label(
//                label = {
//                    Box(
//                        Modifier
//                            .size(32.dp)
//                            .background(Color(0xFF20242EC4), shape = CircleShape),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            (currentIndex.roundToInt() + 1).toString(),
//                            color = MaterialTheme.colorScheme.onPrimary,
//                            style = MaterialTheme.typography.labelMedium
//                        )
//                    }
//                },
//                isPersistent = true,
//                interactionSource = interaction
//            ) {
//                SliderDefaults.Thumb(interactionSource = interaction)
//            }
//        }
//    )
//}
//
