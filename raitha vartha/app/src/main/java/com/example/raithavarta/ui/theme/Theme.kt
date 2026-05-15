package com.example.raithavarta.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = LeafGreen,
    onPrimary = Color.White,
    primaryContainer = LeafGreenLight,
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFF558B2F),
    onSecondary = Color.White,
    tertiary = Color(0xFF6D4C41),
    background = MintBackground,
    surface = CardSurface,
    onSurface = Color(0xFF1A1C1E),
    onSurfaceVariant = MutedText,
    outline = Color(0xFFCCE4CF)
)

@Composable
fun RaithavartaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}
