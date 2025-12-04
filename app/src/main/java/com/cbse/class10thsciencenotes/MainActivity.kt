package com.cbse.class10thsciencenotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cbse.class10thsciencenotes.data.NotesViewModel
import com.cbse.class10thsciencenotes.ui.pages.ChapterListScreen
import com.cbse.class10thsciencenotes.ui.pages.ChapterQAScreen
import com.cbse.class10thsciencenotes.ui.pages.NotesScreen
import com.cbse.class10thsciencenotes.ui.pages.Screen
import com.cbse.class10thsciencenotes.ui.pages.StudyDashboardScreen
import com.cbse.class10thsciencenotes.ui.pages.Study_Option
import com.cbse.class10thsciencenotes.ui.theme.Class10thScienceNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Class10thScienceNotesTheme() {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {

    val navController = rememberNavController()
    val myViewModel: NotesViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {

        // ---------------------------
        // HOME
        // ---------------------------
        composable(Screen.Home.route) {
            StudyDashboardScreen { studyOption ->
                // Navigate like: chapters/NOTES
                myViewModel.setStudyOption(studyOption)
                navController.navigate(Screen.Chapters.route)
            }
        }

        composable(
            route = Screen.Chapters.route
        ) { entry ->
            ChapterListScreen(
                navController = navController,
                onClick = { chapter ->
                    myViewModel.setCurrentChapter(chapter)
                    navController.navigate(myViewModel.navigator())
                }
            )
        }


        // ---------------------------
        // NOTES (argument: jsonFileName)
        // ---------------------------
        composable(
            route = "notes/{jsonFileName}"
        ) { entry ->

            val fileName = entry.arguments?.getString("jsonFileName")
            requireNotNull(fileName) { "Developer forgot to pass the JSON file" }

            NotesScreen(
                jsonFileName = fileName,
                navController = navController
            )
        }

        composable(route = Screen.Questions.route) {
            ChapterQAScreen()
        }


        // ---------------------------
        // STATIC SCREENS
        // ---------------------------
//        composable(Screen.Questions.route) {
//            QuestionsScreen(navController)
//        }
//
//        composable(Screen.MCQs.route) {
//            MCQScreen(navController)
//        }
//
//        composable(Screen.Settings.route) {
//            SettingsScreen(navController)
//        }
    }
}


//
//@Composable
//fun AppNavGraph(darkTheme: Boolean) {
//    val navController = rememberNavController()
//    NavHost(
//        navController = navController,
//        startDestination = "chapter_list"
//    ) {
//        composable("chapter_list") {
//            ChapterListScreen { chapterIndex ->
//                navController.navigate("topic_pager/$chapterIndex")
//            }
//        }
//        composable(
//            "topic_pager/{chapterIndex}",
//            arguments = listOf(navArgument("chapterIndex") { type = NavType.IntType })
//        ) { backStackEntry ->
//            val chapterIndex = backStackEntry.arguments?.getInt("chapterIndex") ?: 0
//            TopicViewerScreenWithSlider(
//                chapter = chapters[chapterIndex],
//                onBack = { navController.popBackStack() },darkTheme=isSystemInDarkTheme()
//            )
//        }
//    }
//}
//
////
////@Composable
////fun MainScreen() {
////    var selectedChapter by remember { mutableStateOf<Chapter?>(null) }
////
////    if (selectedChapter == null) {
////        ChapterListScreen(
////            onChapterClick = { index ->
////                selectedChapter = chapters[index]
////            }
////        )
////    } else {
//////        TopicViewerScreen(
//////            chapter = selectedChapter!!,
//////            onBack = { selectedChapter = null }
//////        )
////    }
////}
//
//
//
//
//
//@SuppressLint("SetJavaScriptEnabled")
//@Composable
//fun WebViewFromAssets(assetPath: String) {
//
//    AndroidView(factory = { context ->
//        WebView(context).apply {
//            webViewClient = WebViewClient()
//            // Enable JavaScript if needed
//            settings.javaScriptEnabled = true
//            // Load HTML file from assets
//            loadUrl("file:///android_asset/$assetPath")
//        }
//    })
//}
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    Class10thScienceNotesTheme {
//        Greeting("Android")
//    }
//}