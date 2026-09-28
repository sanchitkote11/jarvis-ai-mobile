package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val JarvisColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = CyberBackground,
    primaryContainer = CyberSurfaceVariant,
    onPrimaryContainer = NeonCyanLight,
    secondary = NeonBlueLight,
    onSecondary = TextPrimary,
    secondaryContainer = CyberSurface,
    onSecondaryContainer = TextCyan,
    tertiary = NeonMagenta,
    onTertiary = TextPrimary,
    background = CyberBackground,
    onBackground = TextPrimary,
    surface = CyberBackgroundElevated,
    onSurface = TextPrimary,
    surfaceVariant = CyberSurface,
    onSurfaceVariant = TextSecondary,
    outline = CyberCardBorderGlow,
    outlineVariant = CyberCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Enforce our high-fidelity JARVIS cyberpunk aesthetic
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = JarvisColorScheme,
        typography = Typography,
        content = content
    )
}
