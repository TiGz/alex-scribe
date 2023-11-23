package org.tigz.alex.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextToSpeechService @Inject constructor(context: Context) : TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech = TextToSpeech(context, this)

    private var isInitialized = false

    companion object {
        private const val TAG = "TextToSpeechService"
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale.ENGLISH
            textToSpeech.setSpeechRate(0.8f)
            textToSpeech.setPitch(1.0f)
            isInitialized = true
        }
    }

    fun speak(text: String) {
        if (isInitialized) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun stop() {
        if (isInitialized) {
            textToSpeech.stop()
        }
    }

    fun shutdown() {
        if ( isInitialized ){
            textToSpeech.shutdown()
        }
    }

}
