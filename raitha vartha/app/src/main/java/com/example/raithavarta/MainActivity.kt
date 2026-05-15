package com.example.raithavarta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.raithavarta.di.AppContainer
import com.example.raithavarta.ui.navigation.RaithavartaApp

/**
 * Single-activity Compose host for Raitha-Varta Premium.
 */
class MainActivity : ComponentActivity() {

    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        appContainer = AppContainer(this)
        setContent {
            RaithavartaApp(container = appContainer)
        }
    }
}
