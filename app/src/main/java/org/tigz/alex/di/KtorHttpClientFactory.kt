package org.tigz.alex.di

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging

import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
object KtorHttpClientFactory {

    private const val TAG = "KtorHttpClient"
    private const val TIME_OUT = 30_000

    fun buildKtorHttpClient(): HttpClient {
        return HttpClient(Android) {
            engine {
                connectTimeout = TIME_OUT
                socketTimeout = TIME_OUT
            }
            expectSuccess = false
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d(TAG, message)
                    }
                }
                level = LogLevel.ALL
            }
            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
    }
}