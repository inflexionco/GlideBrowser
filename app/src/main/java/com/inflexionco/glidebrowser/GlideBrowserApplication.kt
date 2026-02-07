package com.inflexionco.glidebrowser

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GlideBrowserApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize any necessary components here
    }
}