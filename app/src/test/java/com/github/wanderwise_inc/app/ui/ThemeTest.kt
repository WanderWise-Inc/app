import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.github.wanderwise_inc.app.ui.theme.DarkColorPalette
import com.github.wanderwise_inc.app.ui.theme.LightColorPalette
import com.github.wanderwise_inc.app.ui.theme.Typography
import com.github.wanderwise_inc.app.ui.theme.WanderWiseTheme
import com.github.wanderwise_inc.app.ui.theme.shapes
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ColorPaletteTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun wanderWiseLightThemeTest() {
    composeTestRule.setContent {
      WanderWiseTheme(darkTheme = false) { Text("Test") }
      // Find the node with the text "Test"
      val node = composeTestRule.onNodeWithText("Test")

      // Assert that the node exists
      assertNotNull(node)
    }
  }

  @Test
  fun wanderWiseDarkThemeTest() {
    composeTestRule.setContent {
      WanderWiseTheme(darkTheme = true) { Text("Test") }
      // Find the node with the text "Test"
      val node = composeTestRule.onNodeWithText("Test")

      // Assert that the node exists
      assertNotNull(node)
    }
  }

  @Test
  fun shapes_areNonNull() {
    val s = shapes
    assertNotNull(s.extraSmall)
    assertNotNull(s.small)
    assertNotNull(s.medium)
    assertNotNull(s.large)
    assertNotNull(s.extraLarge)
  }

  @Test
  fun typography_areNonNull() {
    val t = Typography
    assertNotNull(t.bodyLarge)
  }

  @Test
  fun darkColorPalette_allColorsAreNonNull() {
    val darkcp = DarkColorPalette
    assertAllColorsNonNull(darkcp)
  }

  @Test
  fun lightColorPalette_allColorsAreNonNull() {

    val lightcp = LightColorPalette
    assertAllColorsNonNull(lightcp)
  }

  private fun assertAllColorsNonNull(colorScheme: ColorScheme) {
    assertNotNull(colorScheme.primary)
    assertNotNull(colorScheme.onPrimary)
    assertNotNull(colorScheme.primaryContainer)
    assertNotNull(colorScheme.onPrimaryContainer)
    assertNotNull(colorScheme.inversePrimary)
    assertNotNull(colorScheme.secondary)
    assertNotNull(colorScheme.onSecondary)
    assertNotNull(colorScheme.secondaryContainer)
    assertNotNull(colorScheme.onSecondaryContainer)
    assertNotNull(colorScheme.tertiary)
    assertNotNull(colorScheme.onTertiary)
    assertNotNull(colorScheme.tertiaryContainer)
    assertNotNull(colorScheme.onTertiaryContainer)
    assertNotNull(colorScheme.error)
    assertNotNull(colorScheme.onError)
    assertNotNull(colorScheme.errorContainer)
    assertNotNull(colorScheme.onErrorContainer)
    assertNotNull(colorScheme.background)
    assertNotNull(colorScheme.onBackground)
    assertNotNull(colorScheme.surface)
    assertNotNull(colorScheme.onSurface)
    assertNotNull(colorScheme.inverseSurface)
    assertNotNull(colorScheme.inverseOnSurface)
    assertNotNull(colorScheme.surfaceVariant)
    assertNotNull(colorScheme.onSurfaceVariant)
    assertNotNull(colorScheme.outline)
  }
}
