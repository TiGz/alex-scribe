package org.tigz.alex.service

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranscriptionService @Inject constructor(private val context: Context, private val speechToTextService: SpeechToTextService) {

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentAudioFile: File? = null
    private var isRecording: Boolean = false

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val TAG = "MediaService"
    }

    fun startRecording() {
        currentAudioFile = createAudioFile()

        mediaRecorder = MediaRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(currentAudioFile?.absolutePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                // Handle exceptions
            }

            start()
            isRecording = true
        }
    }

    fun stopRecordingAndTranscribe(onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        if ( isRecording ){
            mediaRecorder?.apply {
                stop()
                reset()
                release()
            }
            mediaRecorder = null

            isRecording = false

            Log.d(TAG, "Recorded ${currentAudioFile!!.length() } bytes of audit to : ${currentAudioFile!!.absolutePath}")

            // hand off to speech to text service
            speechToTextService.transcribeAudio(currentAudioFile!!, onSuccess, onError)
        }
    }

    fun playCurrentAudio() {
        currentAudioFile?.let { file ->
            mediaPlayer?.release()  // Release any previous MediaPlayer
            mediaPlayer = MediaPlayer().apply {
                try {
                    setDataSource(file.absolutePath)
                    prepare()
                    start()
                } catch (e: IOException) {
                    // Handle exceptions
                }
            }
        }
    }

    fun cleanUp(){
        if ( isRecording ){
            stopRecordingAndTranscribe(
                onSuccess = { transcription ->
                    Log.d(TAG, "cleanUp: $transcription")
                },
                onError = { exception ->
                    Log.d(TAG, "cleanUp: ${exception.message}")
                }
            )
        }
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun createAudioFile(): File {
        val newFile = File(
            getOutputDirectory(),
            SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis()) + ".mp4"
        )

        // Log the file location
        Log.d(TAG, "Created audio file at: ${newFile.absolutePath}")

        return newFile
    }

    fun getOutputDirectory(): File {
        return context.getExternalFilesDir(null)!!
    }


    // Add methods for uploading the file and handling the response
}
