package org.tigz.alex.service

import android.content.Context
import android.util.Log
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.model.ModelId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.Okio
import okio.Source
import okio.source
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechToTextService @Inject constructor(private val context: Context, private val openAIFactory: OpenAIFactory) {

    companion object {
        private const val TAG = "SpeechToTextService"
    }

    fun transcribeAudio(audioFile: File, onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val openAI = openAIFactory.ensureOpenAI()

                val request = TranscriptionRequest(
                    audio = FileSource(name = audioFile.name, source = audioFile.source()),
                    model = ModelId("whisper-1")
                )
                val transcription = openAI.transcription(request)

                withContext(Dispatchers.Main) {
                    Log.d(TAG, "transcribeAudio: ${transcription.text}")
                    onSuccess(transcription.text)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }
}