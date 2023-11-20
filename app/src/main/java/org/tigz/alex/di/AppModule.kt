package org.tigz.alex.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.tigz.alex.service.MediaService
import org.tigz.alex.service.SpeechToTextService
import org.tigz.alex.service.TranscriptionService

/**
 * Setup services for DI
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideMediaService(@ApplicationContext context: Context): MediaService =
        MediaService(context)

    @Provides
    fun provideSpeechToTextService(@ApplicationContext context: Context): SpeechToTextService =
        SpeechToTextService(context)

    @Provides
    fun provideTranscriptionService(@ApplicationContext context: Context, speechToTextService: SpeechToTextService): TranscriptionService =
        TranscriptionService(context, speechToTextService)
}
