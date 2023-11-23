package org.tigz.alex.service

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.file.FileUpload
import com.aallam.openai.api.file.Purpose
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.source
import org.tigz.alex.service.openai.OpenAIClient
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use Open AI file service to upload the image file and then use chat completion to get the contents of the image.
 */
@Singleton
class ImageToTextService @Inject constructor(
    private val context: Context,
    private val openAIClient: OpenAIClient
) {

    companion object {
        private const val TAG = "ImageToTextService"
    }

    fun uploadImageAndFetchContents(imageUri: Uri, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val imageBytes = encodeImageToBase64(context, imageUri)
            if (imageBytes == null) {
                onError("Failed to encode image to base64")
                return@launch
            }

            try{
                val fileText = openAIClient.imageSearch(imageBytes)
                onSuccess(fileText)
            }
            catch (e: Exception){
                Log.w(TAG, "uploadImageAndFetchContents",e)
                onError("Failed to upload image and fetch contents")
            }
        }
    }

    fun encodeImageToBase64(context: Context, imageUri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            inputStream?.let { stream ->
                val bytes = stream.readBytes()
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            Log.w(TAG, "encodeImageToBase64",e)
            null
        }
    }







}