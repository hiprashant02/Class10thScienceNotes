package com.cbse.class10thsciencenotes.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// UI Model: This is what your Composables will use.
// It's Parcelable so it can be saved across configuration changes.


// UI Model: This is what your Composables will use. It remains the same.
@Parcelize
sealed class NoteItem : Parcelable {
    @Parcelize
    data class TopicName(val text: String) : NoteItem()
    @Parcelize
    data class Heading(val text: String) : NoteItem()
    @Parcelize
    data class Paragraph(val text: String) : NoteItem()
    @Parcelize
    data class Bullet(val text: String, val subBullets: List<String> = emptyList()) : NoteItem()
    @Parcelize
    data class Image(val resId: Int, val height: Int = 140) : NoteItem()
}

// Data Transfer Objects (DTOs): These classes are updated to match the new JSON.
data class ChapterNotesDto(
    val chapterId: Int,
    val chapterTitle: String,
    val topics: List<TopicDto> // Changed from 'content' to 'topics'
)

data class TopicDto(
    val topicId: String,
    val topicTitle: String,
    val content: List<NoteContentItemDto>
)

data class NoteContentItemDto(
    val type: String,
    val text: String?,
    val subBullets: List<String>? = null,
    val resName: String? = null,
    val height: Int? = null
)

// A simple class to represent a chapter in the main list