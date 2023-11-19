package org.tigz.alex.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.tigz.alex.service.MediaService

/**
 * Setup services for DI
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideMediaService(@ApplicationContext context: Context): MediaService =
        MediaService(context)
}
