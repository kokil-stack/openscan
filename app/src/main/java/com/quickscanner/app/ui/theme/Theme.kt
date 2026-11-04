package com.quickscanner.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ScannerPrimary,
    secondary = ScannerSecondary,
    background = ScannerBackground,
    surface = ScannerSurface,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = ScannerTextPrimary,
    onSurface = ScannerTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = ScannerPrimary,
    secondary = ScannerSecondary,
    background = Color(0xFFF8F9FA),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF212529),
    onSurface = Color(0xFF212529)
)

@Composable
fun QuickScannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
