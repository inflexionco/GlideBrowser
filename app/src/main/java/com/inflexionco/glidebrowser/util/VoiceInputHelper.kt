package com.inflexionco.glidebrowser.util

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import timber.log.Timber
import java.util.Locale

/**
 * Helper class for voice input functionality
 * Specifically designed for Android TV remote voice button
 */
class VoiceInputHelper(
    private val activity: ComponentActivity,
    private val onResult: (String) -> Unit
) {
    private var voiceLauncher: ActivityResultLauncher<Intent>? = null

    init {
        setupVoiceLauncher()
    }

    private fun setupVoiceLauncher() {
        voiceLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val matches = result.data?.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                val spokenText = matches?.firstOrNull()

                if (!spokenText.isNullOrBlank()) {
                    Timber.d("Voice input received: $spokenText")
                    onResult(spokenText)
                } else {
                    Timber.w("Voice input was empty")
                }
            } else {
                Timber.w("Voice input cancelled or failed: resultCode=${result.resultCode}")
            }
        }
    }

    /**
     * Start voice input recognition
     * @param prompt The prompt to show to the user
     */
    fun startVoiceInput(prompt: String = "Speak URL or search") {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
                // Optimize for TV - single result, no partial results
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false)
            }

            Timber.d("Starting voice input with prompt: $prompt")
            voiceLauncher?.launch(intent)
        } catch (e: Exception) {
            Timber.e(e, "Failed to start voice input")
        }
    }

    /**
     * Check if voice input is available on the device
     */
    fun isVoiceInputAvailable(): Boolean {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        val packageManager = activity.packageManager
        return intent.resolveActivity(packageManager) != null
    }

    fun cleanup() {
        voiceLauncher = null
    }
}