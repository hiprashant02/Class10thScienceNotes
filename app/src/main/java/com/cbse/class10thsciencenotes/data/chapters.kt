package com.cbse.class10thsciencenotes.data

import androidx.compose.ui.graphics.Color

data class Chapter(
    val id: Int,
    val title: String,
    val color: Color = Color.Transparent
)
val chapters: List<Chapter> = listOf(
    Chapter(1, "Chemical Reactions", Color(0xFF03A9F4)),
    Chapter( 2,"Acids, Bases & Salts", Color(0xFF4CAF50)),
    Chapter( 3,"Metals & Non-metals", Color(0xFFF44336)),
    Chapter( 4,"Carbon & its Compounds", Color(0xFFFF9800)),
    Chapter(id = 5, title = "Life Processes",Color(0xFF03A9F4) ),
    Chapter(id = 6, title = "Control and Coordination", Color(0xFF4CAF50)),
    Chapter(id = 7, title = "How do Organisms Reproduce?", Color(0xFFF44336))
    // Add more chapters here
)
