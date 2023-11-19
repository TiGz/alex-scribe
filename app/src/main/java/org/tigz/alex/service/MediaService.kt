package org.tigz.alex.service

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaService @Inject constructor(private val context: Context) {

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val TAG = "MediaService"
    }

    fun getOutputDirectory(): File {
        return context.getExternalFilesDir(null)!!
    }

    fun createImageFile(): File {
        val newFile = File(
            getOutputDirectory(),
            SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        // Log the file location
        Log.d(TAG, "Created image file at: ${newFile.absolutePath}")

        return newFile
    }

    // Add other media-related functionalities here, like processing the photo
}
