package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AndroidGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = AndroidGreenLight,
    onPrimaryContainer = AndroidGreenDark,
    secondary = NeonPurple,
    onSecondary = Color.White,
    tertiary = NeonGreen,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = TextPrimary,
    surface = LightSurface,
    onSurface = TextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = LightBorder,
    error = NeonRed
)

private val DarkColorScheme = darkColorScheme(
    primary = AndroidGreenAccent,
    onPrimary = Color.Black,
    primaryContainer = CyberSurfaceVariant,
    onPrimaryContainer = AndroidGreenAccent,
    secondary = NeonPurple,
    onSecondary = Color.White,
    tertiary = NeonGreen,
    onTertiary = Color.Black,
    background = CyberBackground,
    onBackground = DarkTextPrimary,
    surface = CyberSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = CyberBorder,
    error = NeonRed
)

@Composable
fun KonekVpnTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
