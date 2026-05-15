package com.example.raithavarta

import android.app.Application
import com.example.raithavarta.data.local.DatabaseProvider

/**
 * Application entry: ensures Room database is initialised once.
 */
class RaithavartaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseProvider.get(this)
    }
}
