package org.tigz.alex.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import org.tigz.alex.service.ImageToTextService
import org.tigz.alex.service.OpenAIFactory
import org.tigz.alex.service.MediaService
import org.tigz.alex.service.SpeechRecognizerService
import org.tigz.alex.service.SpeechToTextService
import org.tigz.alex.service.TextToSpeechService
import org.tigz.alex.service.TranscriptionService
import org.tigz.alex.service.openai.OpenAIClient

/**
 * Setup services for DI
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideOpenAIClient(@ApplicationContext context: Context): OpenAIClient =
        OpenAIClient(context)

    @Provides
    fun provideOpenAIFactory(@ApplicationContext context: Context): OpenAIFactory =
        OpenAIFactory(context)

    @Provides
    fun provideTextToSpeechService(@ApplicationContext context: Context): TextToSpeechService =
        TextToSpeechService(context)

    @Provides
    fun provideSpeechRecognizerService(@ApplicationContext context: Context): SpeechRecognizerService =
        SpeechRecognizerService(context)

    @Provides
    fun provideMediaService(@ApplicationContext context: Context): MediaService =
        MediaService(context)

    @Provides
    fun provideSpeechToTextService(@ApplicationContext context: Context, openAIFactory: OpenAIFactory): SpeechToTextService =
        SpeechToTextService(context, openAIFactory)

    @Provides
    fun provideTranscriptionService(@ApplicationContext context: Context, speechToTextService: SpeechToTextService): TranscriptionService =
        TranscriptionService(context, speechToTextService)

    @Provides
    fun provideImageToTextService(@ApplicationContext context: Context, openAIClient: OpenAIClient): ImageToTextService =
        ImageToTextService(context, openAIClient)

}
