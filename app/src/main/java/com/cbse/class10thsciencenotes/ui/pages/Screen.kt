package com.cbse.class10thsciencenotes.ui.pages

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Chapters : Screen("chapters")        // FIXED
    object Notes : Screen("notes")      // FIXED
    object Questions : Screen("questions")
    object MCQs : Screen("mcqs")
    object Settings : Screen("settings")
}

