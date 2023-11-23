package org.tigz.alex.service.openai


import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import org.tigz.alex.di.KtorHttpClientFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAIClient @Inject constructor(private val context: Context) {

    val httpClient = KtorHttpClientFactory.buildKtorHttpClient()
    val baseUrl = "https://api.openai.com/v1"

    companion object {
        private const val TAG = "OpenAIClient"
    }

    suspend fun imageSearch(imageBytes: String, questionText: String = "Extract all of the text from this image and clean it up"): String {
        val apiKey = getApiKey()
        val url = Url("$baseUrl/chat/completions")

        val requestBody = ChatCompletionRequest(
            model = "gpt-4-vision-preview",
            messages = listOf(
                Message(
                    role = "user",
                    content = listOf(
                        Content(type = "text", text = questionText),
                        Content(type = "image_url", image_url = ImageUrl("data:image/jpeg;base64,$imageBytes"))
                    )
                )
            ),
            max_tokens = 300
        )

        val response = httpClient.use {
            it.post(url) {
                contentType(ContentType.Application.Json)
                bearerAuth(apiKey)
                setBody(requestBody)
            }
        }

        val imageText = response.body<ChatCompletionResponse>().choices[0].message.content
        Log.d(TAG, "imageSearch: $imageText")

        return imageText
    }

    fun getApiKey(): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString("api_key", "no-value-set")!!
    }
}