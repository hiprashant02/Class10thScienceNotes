package com.cbse.class10thsciencenotes.ui.components

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * TextToSpeech Manager using Hindi locale for Indian-sounding accent
 * Features: Indian accent via Hindi, word-by-word highlighting, offline support
 */
class TextToSpeechManager(
    private val context: Context,
    private val onWordSpoken: (String, Int, Int) -> Unit = { _, _, _ -> },
    private val onComplete: () -> Unit = {}
) {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var currentText = ""
    private val mainHandler = Handler(Looper.getMainLooper())

    // Initialize TTS with Hindi locale for Indian accent
    fun initialize(onReady: () -> Unit = {}) {
        Log.d("TTS", "🎙️ Initializing TTS with Hindi locale for Indian accent...")

        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.let { textToSpeech ->
                    // Use Hindi locale - this will give Indian-accented English
                    val hindiLocale = Locale("hin", "IND")
                    var result = textToSpeech.setLanguage(hindiLocale)

                    Log.d("TTS", "Hindi locale (hin_IND) result: $result")

                    // If Hindi locale not available, try standard Hindi
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.d("TTS", "Trying standard Hindi (hi_IN)...")
                        val standardHindi = Locale("hi", "IN")
                        result = textToSpeech.setLanguage(standardHindi)
                        Log.d("TTS", "Standard Hindi result: $result")
                    }

                    // If still not available, try Indian English as fallback
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.d("TTS", "Trying Indian English (en_IN)...")
                        val indianEnglish = Locale("en", "IN")
                        result = textToSpeech.setLanguage(indianEnglish)
                        Log.d("TTS", "Indian English result: $result")
                    }

                    Log.d("TTS", "Available voices: ${textToSpeech.voices?.size}")
                    textToSpeech.voices?.forEach { voice ->
                        Log.d("TTS", "Voice: ${voice.name}, Locale: ${voice.locale}")
                    }

                    // Try to find and force an Indian voice
                    val indianVoice = textToSpeech.voices?.find { voice ->
                        voice.locale.country == "IN" ||
                        voice.locale.language == "hi" ||
                        voice.name.contains("hindi", ignoreCase = true) ||
                        voice.name.contains("india", ignoreCase = true)
                    }

                    if (indianVoice != null) {
                        textToSpeech.voice = indianVoice
                        Log.d("TTS", "✅ Using Indian voice: ${indianVoice.name}")
                    } else {
                        Log.d("TTS", "⚠️ No specific Indian voice found, using locale setting")
                    }

                    // Optimize for Indian accent
                    textToSpeech.setSpeechRate(0.8f) // Slower for clarity
                    textToSpeech.setPitch(1.1f) // Slightly higher for Indian tone

                    isInitialized = true

                    // Word-level tracking listener
                    textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            Log.d("TTS", "▶️ Started speaking")
                        }

                        override fun onDone(utteranceId: String?) {
                            Log.d("TTS", "✅ Done speaking")
                            mainHandler.post {
                                onComplete()
                            }
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            Log.e("TTS", "❌ Error speaking")
                            mainHandler.post {
                                onComplete()
                            }
                        }

                        override fun onRangeStart(
                            utteranceId: String?,
                            start: Int,
                            end: Int,
                            frame: Int
                        ) {
                            // Word-by-word callback for highlighting
                            val word = if (start >= 0 && start < currentText.length &&
                                          end > start && end <= currentText.length) {
                                currentText.substring(start, end)
                            } else ""

                            if (word.isNotEmpty()) {
                                Log.d("TTS", "💡 Word: '$word' at [$start, $end]")

                                mainHandler.post {
                                    onWordSpoken(word, start, end)
                                }
                            }
                        }
                    })

                    onReady()
                }
            } else {
                Log.e("TTS", "❌ TTS initialization failed with status: $status")
            }
        }
    }

    fun speak(text: String) {
        if (!isInitialized || tts == null) {
            Log.e("TTS", "❌ TTS not initialized")
            return
        }

        currentText = text
        Log.d("TTS", "🗣️ Speaking (${text.length} chars): ${text.take(50)}...")

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "notes_tts")

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "notes_tts")
    }

    fun stop() {
        tts?.stop()
        Log.d("TTS", "⏹️ Speech stopped")
    }

    fun isSpeaking(): Boolean {
        return tts?.isSpeaking ?: false
    }

    fun setSpeechRate(rate: Float) {
        tts?.setSpeechRate(rate)
        Log.d("TTS", "🎚️ Speech rate set to: $rate")
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        Log.d("TTS", "🔌 TTS shutdown")
    }

    // Helper functions
    fun hasIndianVoice(): Boolean {
        return tts?.voices?.any { voice ->
            voice.locale.country == "IN" || voice.locale.language == "hi"
        } ?: false
    }

    fun getAvailableVoices(): List<String> {
        return tts?.voices?.map { "${it.name} (${it.locale})" } ?: emptyList()
    }
}

@Composable
fun rememberTextToSpeechManager(
    onWordSpoken: (String, Int, Int) -> Unit = { _, _, _ -> },
    onComplete: () -> Unit = {}
): TextToSpeechManager {
    val context = LocalContext.current
    val ttsManager = remember {
        TextToSpeechManager(context, onWordSpoken, onComplete)
    }

    DisposableEffect(Unit) {
        ttsManager.initialize()
        onDispose {
            ttsManager.shutdown()
        }
    }

    return ttsManager
}
