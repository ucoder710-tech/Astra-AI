package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val DarkMaterialColorScheme = darkColorScheme(
    primary = AuroraIndigo,
    onPrimary = AuroraDarkTextPrimary,
    primaryContainer = AuroraDarkSurfaceRaised,
    onPrimaryContainer = AuroraCyan,
    secondary = AuroraViolet,
    onSecondary = AuroraDarkTextPrimary,
    background = AuroraDarkCanvas,
    onBackground = AuroraDarkTextPrimary,
    surface = AuroraDarkSurface,
    onSurface = AuroraDarkTextPrimary,
    surfaceVariant = AuroraDarkSurfaceHover,
    onSurfaceVariant = AuroraDarkTextSecondary,
    outline = AuroraDarkBorderSubtle,
    outlineVariant = AuroraDarkBorderStrong,
    error = AuroraDarkError,
    onError = AuroraDarkTextPrimary
)

private val LightMaterialColorScheme = lightColorScheme(
    primary = AuroraLightIndigo,
    onPrimary = AuroraLightSurface,
    primaryContainer = AuroraLightSurfaceHover,
    onPrimaryContainer = AuroraLightIndigo,
    secondary = AuroraLightViolet,
    onSecondary = AuroraLightSurface,
    background = AuroraLightCanvas,
    onBackground = AuroraLightTextPrimary,
    surface = AuroraLightSurface,
    onSurface = AuroraLightTextPrimary,
    surfaceVariant = AuroraLightSurfaceHover,
    onSurfaceVariant = AuroraLightTextSecondary,
    outline = AuroraLightBorderSubtle,
    outlineVariant = AuroraLightBorderStrong,
    error = AuroraLightError,
    onError = AuroraLightSurface
)

object AstraTheme {
    val colors: AstraColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAstraColors.current
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val astraColors = if (darkTheme) DarkAstraColors else LightAstraColors
    val materialColors = if (darkTheme) DarkMaterialColorScheme else LightMaterialColorScheme

    CompositionLocalProvider(LocalAstraColors provides astraColors) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = Typography,
            content = content
        )
    }
}
