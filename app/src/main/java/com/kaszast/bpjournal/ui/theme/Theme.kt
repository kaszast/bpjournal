package com.kaszast.bpjournal.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AccentTeal,
    onPrimary = LightCardBg,
    primaryContainer = LightTargetZone,
    onPrimaryContainer = LightTextPrimary,
    secondary = AccentMint,
    onSecondary = LightCardBg,
    secondaryContainer = LightTargetZone,
    onSecondaryContainer = LightTextPrimary,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightCardBg,
    onSurface = LightTextPrimary,
    surfaceVariant = LightBg,
    onSurfaceVariant = LightTextSecondary,
    outline = LightCardBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPulseTeal,
    onPrimary = DarkBg,
    primaryContainer = DarkTargetZone,
    onPrimaryContainer = DarkTextPrimary,
    secondary = DarkDiastolic,
    onSecondary = DarkBg,
    secondaryContainer = DarkCardBg,
    onSecondaryContainer = DarkTextPrimary,
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkCardBg,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCardBg,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkCardBorder
)

@Composable
fun BPJournalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
