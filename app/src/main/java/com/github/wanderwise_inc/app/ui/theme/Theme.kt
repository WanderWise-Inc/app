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
        primaryContainer = LightTaupe,
        secondary = Color.Black,
        onSecondary = Color.Black,
        background = White,
        onBackground = Color.Black,
        surface = OffWhite,
        onSurface = Color.Black,
        )

val DarkColorPalette =
    darkColorScheme(
        primary = Maroon,
        onPrimary = Color.White,
        primaryContainer = Taupe,
        secondary = LightMaroon,
        onSecondary = Color.White,
        background = DarkGray,
        onBackground = Color.White,
        surface = LightGray,
        onSurface = Color.White,
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

