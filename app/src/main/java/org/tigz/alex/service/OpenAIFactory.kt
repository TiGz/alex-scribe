package org.tigz.alex.service

import android.content.Context
import androidx.preference.PreferenceManager
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class OpenAIFactory @Inject constructor(private val context: Context) {

    private var openAI: OpenAI? = null
    private var apiKey: String? = null

    fun ensureOpenAI() : OpenAI {
        if (openAI == null || apiKey != getApiKey()) {
            openAI = createOpenAI()
        }
        return openAI!!
    }

    fun createOpenAI(): OpenAI {
        val config = OpenAIConfig(
            token = getApiKey(),
            timeout = Timeout(socket = 30.seconds),
            // additional configurations...
        )
        return OpenAI(config)
    }

    fun getApiKey(): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString("api_key", "no-value-set")!!
    }

}