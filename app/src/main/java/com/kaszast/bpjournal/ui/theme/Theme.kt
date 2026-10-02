package com.kaszast.bpjournal.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = SlatePrimary,
    onPrimary = SlateOnPrimary,
    primaryContainer = SlatePrimaryLight,
    onPrimaryContainer = SlatePrimary,
    secondary = TealSecondary,
    onSecondary = TealOnSecondary,
    secondaryContainer = TealSecondaryLight,
    onSecondaryContainer = TealSecondary,
    background = SlateBackground,
    onBackground = SlateTextPrimary,
    surface = SlateSurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = SlateTextSecondary,
    outline = SlateOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = SlateDarkPrimary,
    onPrimary = SlateDarkBackground,
    primaryContainer = SlatePrimary,
    onPrimaryContainer = SlateDarkPrimary,
    secondary = TealSecondaryLight,
    onSecondary = SlateDarkBackground,
    secondaryContainer = TealSecondary,
    onSecondaryContainer = TealSecondaryLight,
    background = SlateDarkBackground,
    onBackground = SlateDarkTextPrimary,
    surface = SlateDarkSurface,
    onSurface = SlateDarkTextPrimary,
    surfaceVariant = SlateDarkSurfaceVariant,
    onSurfaceVariant = SlateDarkTextSecondary,
    outline = SlateDarkSurfaceVariant
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
