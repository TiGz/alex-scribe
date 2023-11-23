package org.tigz.alex.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use Android's speech recognition service to transcribe audio
 */
@Singleton
class SpeechRecognizerService @Inject constructor(private val context: Context) {
    private val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    private val recognizerIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }

    private var transcriptionResult: ((String) -> Unit)? = null
    private var transcriptionError: ((String) -> Unit)? = null

    companion object {
        private const val TAG = "SpeechRecognizerService"
    }

    init {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.let {
                    transcriptionResult?.invoke(it)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                Log.d(TAG, "onPartialResults")
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d(TAG, "onEvent")
            }

            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "onReadyForSpeech")
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech")
            }

            override fun onRmsChanged(rmsdB: Float) {
                Log.d(TAG, "onRmsChanged")
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d(TAG, "onBufferReceived")
            }

            override fun onEndOfSpeech() {
                Log.d(TAG, "onEndOfSpeech")
            }

            override fun onError(error: Int) {
                transcriptionError?.invoke("Speech recognition error: $error")
            }

            // Implement other listener methods as needed
        })
    }

    fun startTranscription(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        transcriptionResult = onSuccess
        transcriptionError = onError
        speechRecognizer.startListening(recognizerIntent)
    }

    fun stopTranscription() {
        speechRecognizer.stopListening()
    }

    fun cleanUp() {
        speechRecognizer.destroy()
    }
}
