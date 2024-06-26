package com.github.wanderwise_inc.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightColorPalette =
    lightColorScheme(
        primary = Maroon,
        onPrimary = Color.Black,
        primaryContainer = Taupe,
        secondaryContainer = OffWhite,
        secondary = Color.Black,
        onSecondary = Color.Black,
        background = White,
        onBackground = Color.Black,
        surface = LightTaupe,
        surfaceVariant = LightMaroon,
        onSurface = Color.Black,
        tertiary = LightGray,
        onTertiary = Color.Black,
        onTertiaryContainer = DarkGray,
        error = Color.Red,
    )

val DarkColorPalette =
    darkColorScheme(
        primary = Maroon,
        onPrimary = Color.White,
        primaryContainer = DarkGray,
        secondary = LightMaroon,
        onSecondary = Color.White,
        background = DarkGray,
        onBackground = Color.White,
        surface = LightGray,
        surfaceVariant = LightMaroon,
        onSurface = Color.White,
        error = Color.Red,
    )

@Composable
fun WanderWiseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
  val colors =
      when {
        darkTheme -> DarkColorPalette
        else -> LightColorPalette
      }
  MaterialTheme(colorScheme = colors, typography = Typography, content = content)
}
