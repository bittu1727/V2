package com.badamchhakka.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.Black,
    secondary = GoldLight,
    onSecondary = Color.Black,
    tertiary = GemCyan,
    background = Color(0xFF020B07),
    surface = Color(0xFF0F172A),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun BadamChhakkaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
