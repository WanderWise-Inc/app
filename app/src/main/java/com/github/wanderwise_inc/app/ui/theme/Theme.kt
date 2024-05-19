package com.github.wanderwise_inc.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightColorPalette =
    lightColorScheme(
        primary = Blue40,
        onPrimary = Color.White,
        primaryContainer = Blue90,
        onPrimaryContainer = Blue10,
        inversePrimary = Blue80,
        secondary = DarkBlue40,
        onSecondary = Color.White,
        secondaryContainer = DarkBlue90,
        onSecondaryContainer = DarkBlue10,
        tertiary = Violet40,
        onTertiary = Color.White,
        tertiaryContainer = Violet90,
        onTertiaryContainer = Violet10,
        error = Red40,
        onError = Color.White,
        errorContainer = Red90,
        onErrorContainer = Red10,
        background = Grey99,
        onBackground = Grey10,
        surface = BlueGrey90,
        onSurface = BlueGrey30,
        inverseSurface = Grey20,
        inverseOnSurface = Grey95,
        surfaceVariant = BlueGrey90,
        onSurfaceVariant = BlueGrey30,
        outline = BlueGrey50)

val DarkColorPalette =
    darkColorScheme(
        primary = Blue80,
        onPrimary = Blue20,
        primaryContainer = Blue30,
        onPrimaryContainer = Blue90,
        inversePrimary = Blue40,
        secondary = DarkBlue80,
        onSecondary = DarkBlue20,
        secondaryContainer = DarkBlue30,
        onSecondaryContainer = DarkBlue90,
        tertiary = Violet80,
        onTertiary = Violet20,
        tertiaryContainer = Violet30,
        onTertiaryContainer = Violet90,
        error = Red80,
        onError = Red20,
        errorContainer = Red30,
        onErrorContainer = Red90,
        background = Grey10,
        onBackground = Grey90,
        surface = BlueGrey30,
        onSurface = BlueGrey80,
        inverseSurface = Grey90,
        inverseOnSurface = Grey10,
        surfaceVariant = BlueGrey30,
        onSurfaceVariant = BlueGrey80,
        outline = BlueGrey80)
/*MaterialTheme(
colorScheme = lightColorScheme(
primary = Color(0xFFB0E0E6),  // seafoam Green
secondary = Color(0xFFF88379),  // sunset Coral
onPrimary = Color.Black,
onSecondary = Color.White,
background = Color(0xFFE6E6FA),  // pastel Lavender
surface = Color(0xFFE6CEA8),  // warm Sand
onBackground = Color.DarkGray,
onSurface = Color.Black,
)
)*/

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
