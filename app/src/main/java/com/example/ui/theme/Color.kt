package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Aurora Palette Tokens
val AuroraDarkCanvas = Color(0xFF0B0D12)
val AuroraDarkSurface = Color(0xFF12151C)
val AuroraDarkSurfaceRaised = Color(0xFF171B24)
val AuroraDarkSurfaceHover = Color(0xFF1D2230)
val AuroraDarkBorderSubtle = Color(0xFF232838)
val AuroraDarkBorderStrong = Color(0xFF2E3547)

val AuroraDarkTextPrimary = Color(0xFFF4F5F7)
val AuroraDarkTextSecondary = Color(0xFFA7ADBD)
val AuroraDarkTextTertiary = Color(0xFF6B7280)
val AuroraDarkTextDisabled = Color(0xFF454B5C)

val AuroraIndigo = Color(0xFF6366F1)
val AuroraViolet = Color(0xFF8B5CF6)
val AuroraCyan = Color(0xFF22D3EE)

val AuroraDarkSuccess = Color(0xFF34D399)
val AuroraDarkSuccessBg = Color(0xFF0F2A22)
val AuroraDarkWarning = Color(0xFFFBBF24)
val AuroraDarkWarningBg = Color(0xFF2E2410)
val AuroraDarkError = Color(0xFFF87171)
val AuroraDarkErrorBg = Color(0xFF2E1416)
val AuroraDarkInfo = Color(0xFF38BDF8)
val AuroraDarkInfoBg = Color(0xFF10222E)

val AuroraPriorityLow = Color(0xFF60A5FA)
val AuroraPriorityMedium = Color(0xFFFBBF24)
val AuroraPriorityHigh = Color(0xFFF87171)

val AuroraDarkChatUser = Color(0xFF1D2233)

// Light Mode Tokens
val AuroraLightCanvas = Color(0xFFF7F8FA)
val AuroraLightSurface = Color(0xFFFFFFFF)
val AuroraLightSurfaceRaised = Color(0xFFFFFFFF)
val AuroraLightSurfaceHover = Color(0xFFF0F1F5)
val AuroraLightBorderSubtle = Color(0xFFE4E6EC)
val AuroraLightBorderStrong = Color(0xFFCBD0DC)

val AuroraLightTextPrimary = Color(0xFF12151C)
val AuroraLightTextSecondary = Color(0xFF4B5266)
val AuroraLightTextTertiary = Color(0xFF8A90A2)
val AuroraLightTextDisabled = Color(0xFFC2C6D1)

val AuroraLightIndigo = Color(0xFF4F46E5)
val AuroraLightViolet = Color(0xFF7C3AED)
val AuroraLightCyan = Color(0xFF0891B2)

val AuroraLightSuccess = Color(0xFF059669)
val AuroraLightSuccessBg = Color(0xFFECFDF5)
val AuroraLightWarning = Color(0xFFD97706)
val AuroraLightWarningBg = Color(0xFFFFFBEB)
val AuroraLightError = Color(0xFFDC2626)
val AuroraLightErrorBg = Color(0xFFFEF2F2)
val AuroraLightInfo = Color(0xFF0284C7)
val AuroraLightInfoBg = Color(0xFFF0F9FF)

val AuroraLightChatUser = Color(0xFFE9ECF5)

val AuroraAccentGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFF22D3EE))
)

@Immutable
data class AstraColors(
    val canvas: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val surfaceHover: Color,
    val borderSubtle: Color,
    val borderStrong: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textDisabled: Color,
    val accentIndigo: Color,
    val accentViolet: Color,
    val accentCyan: Color,
    val success: Color,
    val successBg: Color,
    val warning: Color,
    val warningBg: Color,
    val error: Color,
    val errorBg: Color,
    val info: Color,
    val infoBg: Color,
    val priorityLow: Color,
    val priorityMedium: Color,
    val priorityHigh: Color,
    val chatUserBubble: Color,
    val chatAssistantBubble: Color,
    val isDark: Boolean
)

val DarkAstraColors = AstraColors(
    canvas = AuroraDarkCanvas,
    surface = AuroraDarkSurface,
    surfaceRaised = AuroraDarkSurfaceRaised,
    surfaceHover = AuroraDarkSurfaceHover,
    borderSubtle = AuroraDarkBorderSubtle,
    borderStrong = AuroraDarkBorderStrong,
    textPrimary = AuroraDarkTextPrimary,
    textSecondary = AuroraDarkTextSecondary,
    textTertiary = AuroraDarkTextTertiary,
    textDisabled = AuroraDarkTextDisabled,
    accentIndigo = AuroraIndigo,
    accentViolet = AuroraViolet,
    accentCyan = AuroraCyan,
    success = AuroraDarkSuccess,
    successBg = AuroraDarkSuccessBg,
    warning = AuroraDarkWarning,
    warningBg = AuroraDarkWarningBg,
    error = AuroraDarkError,
    errorBg = AuroraDarkErrorBg,
    info = AuroraDarkInfo,
    infoBg = AuroraDarkInfoBg,
    priorityLow = AuroraPriorityLow,
    priorityMedium = AuroraPriorityMedium,
    priorityHigh = AuroraPriorityHigh,
    chatUserBubble = AuroraDarkChatUser,
    chatAssistantBubble = Color.Transparent,
    isDark = true
)

val LightAstraColors = AstraColors(
    canvas = AuroraLightCanvas,
    surface = AuroraLightSurface,
    surfaceRaised = AuroraLightSurfaceRaised,
    surfaceHover = AuroraLightSurfaceHover,
    borderSubtle = AuroraLightBorderSubtle,
    borderStrong = AuroraLightBorderStrong,
    textPrimary = AuroraLightTextPrimary,
    textSecondary = AuroraLightTextSecondary,
    textTertiary = AuroraLightTextTertiary,
    textDisabled = AuroraLightTextDisabled,
    accentIndigo = AuroraLightIndigo,
    accentViolet = AuroraLightViolet,
    accentCyan = AuroraLightCyan,
    success = AuroraLightSuccess,
    successBg = AuroraLightSuccessBg,
    warning = AuroraLightWarning,
    warningBg = AuroraLightWarningBg,
    error = AuroraLightError,
    errorBg = AuroraLightErrorBg,
    info = AuroraLightInfo,
    infoBg = AuroraLightInfoBg,
    priorityLow = AuroraPriorityLow,
    priorityMedium = AuroraPriorityMedium,
    priorityHigh = AuroraPriorityHigh,
    chatUserBubble = AuroraLightChatUser,
    chatAssistantBubble = AuroraLightSurface,
    isDark = false
)

val LocalAstraColors = staticCompositionLocalOf { DarkAstraColors }
