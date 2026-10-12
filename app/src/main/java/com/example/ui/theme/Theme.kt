package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = RacingOrange,
    onPrimary = DeepCarbon,
    primaryContainer = RacingOrange.copy(alpha = 0.2f),
    onPrimaryContainer = RacingOrangeLight,
    secondary = ElectricCyan,
    onSecondary = DeepCarbon,
    secondaryContainer = ElectricCyan.copy(alpha = 0.15f),
    onSecondaryContainer = ElectricCyan,
    tertiary = PhysicsLime,
    onTertiary = DeepCarbon,
    background = DeepCarbon,
    onBackground = TextPrimary,
    surface = SlateSurface,
    onSurface = TextPrimary,
    surfaceVariant = CardElevated,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = ThermalRed,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
