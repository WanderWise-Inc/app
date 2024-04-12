package com.github.wanderwise_inc.app.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorTest {

  @Test
  fun testColors() {
    // Test Purple80
    assertEquals(Color(0xFFD0BCFF), Purple80)

    // Test PurpleGrey80
    assertEquals(Color(0xFFCCC2DC), PurpleGrey80)

    // Test Pink80
    assertEquals(Color(0xFFEFB8C8), Pink80)

    // Test Purple40
    assertEquals(Color(0xFF6650a4), Purple40)

    // Test PurpleGrey40
    assertEquals(Color(0xFF625b71), PurpleGrey40)

    // Test Pink40
    assertEquals(Color(0xFF7D5260), Pink40)
  }
}
