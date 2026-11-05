package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val TajerLightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldLight,
    onPrimaryContainer = EmeraldDark,
    secondary = EmeraldSecondary,
    onSecondary = Color.White,
    secondaryContainer = EmeraldLight,
    onSecondaryContainer = EmeraldDark,
    tertiary = WarmGold,
    onTertiary = Color.White,
    tertiaryContainer = WarmGoldLight,
    onTertiaryContainer = WarmGoldDark,
    background = TajerBackground,
    onBackground = TajerTextPrimary,
    surface = TajerSurface,
    onSurface = TajerTextPrimary,
    surfaceVariant = TajerSurfaceVariant,
    onSurfaceVariant = TajerTextSecondary,
    outline = TajerBorder,
    error = StatusDanger,
    onError = Color.White
)

@Composable
fun TajerTheme(
    content: @Composable () -> Unit
) {
    // Force RTL layout by default for authentic Arabic SaaS experience
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = TajerLightColorScheme,
            typography = TajerTypography,
            content = content
        )
    }
}
