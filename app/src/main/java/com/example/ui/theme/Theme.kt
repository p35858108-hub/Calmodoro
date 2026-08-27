package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CozyAnimalCrossingColorScheme = lightColorScheme(
    primary = CozyLeafGreen,
    onPrimary = OnPrimary,
    primaryContainer = CozyLeafGreenContainer,
    onPrimaryContainer = CozyForestDark,
    secondary = CozyPeach,
    onSecondary = OnPrimary,
    secondaryContainer = CozyPeachContainer,
    onSecondaryContainer = CozyCocoaText,
    tertiary = CozyHoney,
    onTertiary = CozyCocoaText,
    background = CozyCreamBg,
    onBackground = CozyCocoaText,
    surface = CozyCardBg,
    onSurface = CozyCocoaText,
    surfaceVariant = CozyCardAlt,
    onSurfaceVariant = CozyCocoaMuted,
    surfaceContainer = CozyCardBg,
    surfaceContainerHigh = CozyCardAlt,
    surfaceContainerHighest = CozyCardSubtle,
    surfaceContainerLow = CozyCardBg,
    surfaceContainerLowest = CozyCreamBg,
    outline = CozyBorder,
    outlineVariant = CozyBorderSubtle,
    error = ErrorColor,
    errorContainer = ErrorContainer,
    onError = OnError
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CozyAnimalCrossingColorScheme,
        typography = Typography,
        content = content
    )
}

