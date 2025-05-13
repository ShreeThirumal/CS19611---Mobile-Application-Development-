package com.shree.madappfinal.data

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class TranslationViewModel : ViewModel() {
    private var translator: Translator? = null
    private var textToSpeech: TextToSpeech? = null

    private val _translationState = MutableStateFlow<TranslationState>(TranslationState.Idle)
    val translationState: StateFlow<TranslationState> = _translationState

    fun initializeTextToSpeech(context: Context) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.US
            }
        }
    }

    fun translateText(text: String, targetLanguage: String) {
        viewModelScope.launch {
            try {
                _translationState.value = TranslationState.Loading

                val options = TranslatorOptions.Builder()
                    .setSourceLanguage("en")
                    .setTargetLanguage(getLanguageCode(targetLanguage))
                    .build()

                translator = Translation.getClient(options)

                // Download model if needed
                translator?.downloadModelIfNeeded()
                    ?.addOnSuccessListener {
                        translator?.translate(text)
                            ?.addOnSuccessListener { translatedText ->
                                _translationState.value = TranslationState.Success(translatedText)
                            }
                            ?.addOnFailureListener { exception ->
                                _translationState.value = TranslationState.Error(exception.message ?: "Translation failed")
                            }
                    }
                    ?.addOnFailureListener { exception ->
                        _translationState.value = TranslationState.Error("Model download failed: ${exception.message}")
                    }
            } catch (e: Exception) {
                _translationState.value = TranslationState.Error(e.message ?: "Translation failed")
            }
        }
    }

    fun speakText(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun getLanguageCode(language: String): String {
        return when (language.lowercase()) {
            "spanish" -> "es"
            "french" -> "fr"
            "german" -> "de"
            "italian" -> "it"
            "portuguese" -> "pt"
            "russian" -> "ru"
            "japanese" -> "ja"
            "chinese" -> "zh"
            else -> "en"
        }
    }

    override fun onCleared() {
        super.onCleared()
        translator?.close()
        textToSpeech?.shutdown()
    }
}

sealed class TranslationState {
    object Idle : TranslationState()
    object Loading : TranslationState()
    data class Success(val translatedText: String) : TranslationState()
    data class Error(val message: String) : TranslationState()
} 