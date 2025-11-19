package com.cbse.class10thsciencenotes.data

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbse.class10thsciencenotes.R
import com.cbse.class10thsciencenotes.ui.pages.Screen
import com.cbse.class10thsciencenotes.ui.pages.StudyOption
import com.cbse.class10thsciencenotes.ui.pages.Study_Option
import com.cbse.class10thsciencenotes.ui.pages.studyOptions
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState = _uiState.asStateFlow()
    val _studyOption = mutableStateOf(studyOptions[0])
    val _currentChapter = mutableStateOf(chapters[0])

    fun setStudyOption(studyOption: StudyOption) {
        _studyOption.value = studyOption
    }

    fun setCurrentChapter(chapter: Chapter) {
        _currentChapter.value = chapter
    }

    fun navigator(): String {
        val chapterNumber = _currentChapter.value.id

        return when (_studyOption.value.type) {
            Study_Option.Notes -> "notes/chapter_${chapterNumber}.json"
            Study_Option.Revision_Notes -> "notes/chapter_${chapterNumber}.json"
            Study_Option.Intext_Questions -> Screen.Questions.route
            Study_Option.BackExercise_Questions -> Screen.Questions.route
            Study_Option.Exemplar -> "notes/chapter_${chapterNumber}.json"
            Study_Option.Practise_MCQs -> Screen.Notes.route
        }
    }

    var allTopics: List<TopicDto> = emptyList()
    var allTopicNames: List<String> = listOf("")
    fun loadChapter(context: Context, jsonFileName: String) {
        viewModelScope.launch {
            try {
                val jsonString =
                    context.assets.open(jsonFileName).bufferedReader().use { it.readText() }
                val chapterDto = Gson().fromJson(jsonString, ChapterNotesDto::class.java)

                allTopics = chapterDto.topics
                _uiState.update {
                    it.copy(
                        chapterTitle = chapterDto.chapterTitle,
                        totalTopics = allTopics.size,
                        currentTopicIndex = 0,
                    )
                }

                allTopics.map { topic -> topic.topicTitle }.also { allTopicNames = it }
                // Display the first topic initially
                displayTopic(0)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(chapterTitle = "Error Loading Notes") }
            }
        }
    }

    fun changeTopic(newIndex: Int) {
        val safeIndex = newIndex.coerceIn(0, allTopics.size - 1)
        _uiState.update { it.copy(currentTopicIndex = safeIndex) }
        displayTopic(safeIndex)
    }

    private fun displayTopic(index: Int) {
        if (allTopics.isNotEmpty() && index < allTopics.size) {
            val topicContent = allTopics[index].content.mapNotNull { dto ->
                // Map DTO to UI model
                when (dto.type) {
                    "topic_name" -> dto.text?.let { NoteItem.TopicName(it) }
                    "heading" -> dto.text?.let { NoteItem.Heading(it) }
                    "paragraph" -> dto.text?.let { NoteItem.Paragraph(it) }
                    "bullet" -> dto.text?.let { NoteItem.Bullet(it, dto.subBullets ?: emptyList()) }
                    "image" -> {
                        val resId = ImageResourceMapper.imageMap[dto.resName]

                        // If the lookup is successful, create the NoteItem.Image with the Int ID.
                        if (resId != null) {
                            NoteItem.Image(resId, dto.height ?: 140)
                        } else {
                            // If the name isn't in our map, ignore this item to prevent crashes.
                            null
                        }
                    }

                    else -> null
                }
            }
            _uiState.update {
                it.copy(
                    currentNotes = topicContent,
                    currentTopicName = allTopics[index].topicTitle
                )
            }
        }
    }
}

// A state class to hold all UI-related data for the NotesScreen
data class NotesUiState(
    val chapterTitle: String = "Loading...",
    val currentNotes: List<NoteItem> = emptyList(),
    val totalTopics: Int = 15,
    val currentTopicIndex: Int = 0,
    val currentTopicName: String = ""
)

object ImageResourceMapper {
    val imageMap: Map<String, Int> = mapOf(
        "photosynthesis_diagram" to R.drawable.photosynthesis_diagram,
        "cross_section_of_a_leaf" to R.drawable.cross_section_of_leaf,
        "amoeba" to R.drawable.amoeba,
        "alimentary_canal" to R.drawable.alimentary_canal,
        "human_respiratory_system" to R.drawable.human_respiratory_system,
        "heart" to R.drawable.heart,
        "lung" to R.drawable.lung,
        "blood_pressure" to R.drawable.blood_pressure,
        "transpiration" to R.drawable.transpiration,
        "excretory_system_human" to R.drawable.excretory_sysytem_human,
        "nephron" to R.drawable.nephron,
        "articial_kidney" to R.drawable.artificial_kindney,

        )
}