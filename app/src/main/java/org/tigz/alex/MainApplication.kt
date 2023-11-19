package org.tigz.alex

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main Application class that uses Hilt for
 * IOC dependency injection etc
 */
@HiltAndroidApp
class MainApplication : Application() {
}