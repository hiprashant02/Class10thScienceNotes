package com.cbse.class10thsciencenotes.ui.pages

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.cbse.class10thsciencenotes.ui.components.ZoomIn
import com.cbse.class10thsciencenotes.ui.components.ZoomableImageDialog


val LocalBaseFontSize = compositionLocalOf { 12f }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    jsonFileName: String,
    navController: NavController,
    viewModel: NotesViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Load the chapter's notes when the screen is first composed

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val orientation = LocalConfiguration.current.orientation
    val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var viewingImageResId by rememberSaveable { mutableStateOf<Int?>(null) }
    var baseFontSize by rememberSaveable { mutableStateOf(15f) }
    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(jsonFileName) {
        viewModel.loadChapter(context, jsonFileName)
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
    CompositionLocalProvider(LocalBaseFontSize provides baseFontSize) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFD7D7D7)),
            bottomBar = {
                if (!isLandscape) {
                    BottomPageNavigator(
                        totalTopics = uiState.totalTopics,
                        currentIndex = currentIndex,
                        onValueChange = { currentIndex = it },
                        onFinalValue = {
                            viewModel.changeTopic(currentIndex)
                        }, currentTopicName = viewModel.allTopicNames[currentIndex],
                    )
                }
            },
            topBar = {
                if (!isLandscape) {
                    TopAppBar(title = {
                        Text(
                            text = "Chapter 5: Life Processes",
                            style = TextStyle(
                                fontFamily = kalamFont,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                    }, actions = {
                        IconButton(onClick = { showSettingsDialog = true }) {
                            Icon(ZoomIn, contentDescription = "Settings")
                        }
                    })
                }
            }
        ) {
            Row(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {


                PaginatedNotes(
                    notes = uiState.currentNotes,
                    background = R.drawable.page_bg,
                    screenHeight = screenHeight,
                    modifier = Modifier.weight(1f), onImageClick = {
                        viewingImageResId = it
                    }
                )
                if (isLandscape) {

                    VerticalSidebar(
                        totalTopics = 32,
                        currentIndex = currentIndex,
                        onValueChange = { currentIndex = it },
                        onFinalValue = {
                            viewModel.changeTopic(currentIndex)
                        }, modifier = Modifier.fillMaxHeight(), onSettingsClick = {
                            showSettingsDialog = true
                        }
                    )
                }
            }
        }
    }
}

// Default base font size is 12sp


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
            .width(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = {
                val newIndex = (currentIndex - 1).coerceAtLeast(0)
                onValueChange(newIndex)
                onFinalValue()
            },
            enabled = currentIndex > 0
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Previous Topic",
                modifier = Modifier.graphicsLayer(rotationZ = 270f)

            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Slider(
                value = currentIndex.toFloat(),
                onValueChange = {
                    onValueChange(it.toInt())
                },
                onValueChangeFinished = {
                    onFinalValue()
                },
                valueRange = 0f..(totalTopics - 1).toFloat(),
                steps = totalTopics - 2,
                modifier = Modifier
                    .graphicsLayer {
                        rotationZ = 90f
                        transformOrigin = TransformOrigin(0f, 0f)
                    }
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(
                            Constraints(
                                minWidth = constraints.minHeight,
                                maxWidth = constraints.maxHeight,
                                minHeight = constraints.minWidth,
                                maxHeight = constraints.maxWidth
                            )
                        )
                        layout(placeable.height, placeable.width) {
                            placeable.place(0, -placeable.height)
                        }
                    }
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF1F1F1F),
                    activeTrackColor = Color(0xFFAAAAAA),
                    inactiveTrackColor = Color(0xFFE0E0E0)
                ),
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFF1F1F1F), shape = CircleShape)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${currentIndex + 1}",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .graphicsLayer(rotationZ = 270f)
                                .animateContentSize()
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
        Spacer(Modifier.height(8.dp))

        IconButton(onClick = onSettingsClick) {
            Icon(ZoomIn, contentDescription = "Settings")
        }
        Spacer(Modifier.height(8.dp))

        IconButton(
            onClick = {
                val newIndex = (currentIndex + 1).coerceAtMost(totalTopics - 1)
                onValueChange(newIndex)
                onFinalValue()
            },
            enabled = currentIndex < totalTopics - 1
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Next Topic",
                modifier = Modifier.graphicsLayer(rotationZ = 270f)

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
val headingColor = Color(0xFF0D47A1)
//val headingColor = Color(0xFF4A6C8C)
//val bodyColor = Color(0xFF504F4F)

val bodyColor = headingColor

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
            .background(Color(0xFFFFFFFF))
    ) {

        LabeledSlider(
            currentIndex = currentIndex,
            totalTopics = totalTopics,
            onValueChange = { newIndex ->
                onValueChange.invoke(newIndex.coerceIn(0, totalTopics - 1))
            }, onFinalValue = {
                onFinalValue.invoke()
            }
        )

        // Bottom row with navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = {
                onValueChange.invoke((currentIndex - 1).coerceIn(0, totalTopics - 1))
                onFinalValue.invoke()
            }, enabled = currentIndex > 0) {
                Text("Back", color = Color(0xFF1B5E20)) // greenish
            }

            Text(
                text = currentTopicName,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                fontSize = 14.sp
            )

            TextButton(onClick = {
                onValueChange.invoke((currentIndex + 1).coerceIn(0, totalTopics - 1))
                onFinalValue.invoke()
            }, enabled = currentIndex < totalTopics - 1) {
                Text("Next topic", color = Color(0xFF1B5E20))
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
    val baseSize = LocalBaseFontSize.current // 👈 Get font size
    Column(modifier = Modifier.padding(start = 19.dp, bottom = 0.dp)) {
            Text(
                "•  $text",
                style = TextStyle(
                    fontFamily = kalamFont,
                    fontSize = (baseSize + 1).sp, // 👈 Make relative
                    lineHeight = (baseSize + 2).sp,
                    color = bodyColor
                ), modifier = Modifier.padding(vertical = 1.dp)
            )

        if (subBullets.isNotEmpty()) {
            Column(modifier = Modifier.padding(start = 34.dp, top = 2.dp)) {
                subBullets.forEach { sub ->
                    Row(modifier = Modifier.padding(bottom = 0.dp)) {
                        Text(
                            "◦ $sub",
                            style = TextStyle(
                                fontFamily = kalamFont,
                                fontSize = baseSize.sp, // 👈 Make relative
                                lineHeight = (baseSize + 1).sp,
                                color = bodyColor
                            ), modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun NoteImage(resId: Int, height: Dp = 140.dp, onImageClick: () -> Unit = {}) {
    Image(
        painter = painterResource(id = resId),
        contentDescription = "Note Image",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(vertical = 8.dp)
            .clickable(true) {
                onImageClick.invoke()
            }
    )
}


@Composable
fun PaginatedNotes(
    notes: List<NoteItem>,
    background: Int,
    screenHeight: Dp,
    horizontalPadding: Dp = 12.dp,
    verticalPadding: Dp = 16.dp,
    modifier: Modifier = Modifier, onImageClick: (Int) -> Unit = { _ -> } // 👈 handle image click
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(notes) {
        scrollState.animateScrollTo(0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        PaginatedColumn(
            pageHeight = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) screenHeight.times(
                0.92f
            ) else screenHeight.times(0.72f),
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
                if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) screenHeight.times(
                    0.92f
                ) else screenHeight.times(0.72f)
            )
//            .border(1.dp, Color.Gray, RectangleShape)
            .padding(bottom = 0.dp)
    ) {
        Image(
            painter = painterResource(id = background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Row(
            Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.BottomCenter)
        ) {
            Spacer(Modifier.width(3.dp)) // small space from image edge
            Box(
                modifier = Modifier
                    .widthIn(min = 0.dp, max = Dp.Infinity)
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.Gray) // creates the small left gap
            )
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(4.dp)
                .padding(start = 3.dp) // small space from image edge
                .background(Color(0xFFB0B0B0)) // thin grey
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            content()
        }
    }
}

@Composable
fun NoteText(text: String, isHeading: Boolean = false) {
    val baseSize = LocalBaseFontSize.current
    Text(
        text = text,
        style = TextStyle(
            fontFamily = kalamFont,
            fontSize = if (isHeading) (baseSize + 2).sp else baseSize.sp,
            fontWeight = if (isHeading) FontWeight.Light else FontWeight.Light,
            lineHeight = (baseSize + if (isHeading) 4 else 2).sp, // Make line height relative too
            color = if (isHeading) headingColor else bodyColor
        ),
        modifier = Modifier.padding(
            top = if (isHeading) 8.dp else 4.dp,
            bottom = if (isHeading) 8.dp else 4.dp,
            start = if (isHeading) 0.dp else 12.dp
        )
    )
}

@Composable
fun NoteTitleText(text: String) {
    val baseSize = LocalBaseFontSize.current
    Text(
        text = text,
        style = TextStyle(
            fontFamily = kalamFont,
            fontSize = (baseSize + 9).sp, // 👈 Make relative
            fontWeight = FontWeight.Light,
            lineHeight = (baseSize + 17).sp, // 👈 Make relative
//            color = Color(0xFF2A2A2A),
            color = bodyColor,
            textAlign = TextAlign.Center
        ),

        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(0.9f)
    )
}

