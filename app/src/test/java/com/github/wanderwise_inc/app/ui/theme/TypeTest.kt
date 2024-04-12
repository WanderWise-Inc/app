package com.github.wanderwise_inc.app.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Test

class TypographyTest {

  @Test
  fun testTypography() {
    val typography = Typography

    // Assert bodyLarge properties
    assertEquals(FontFamily.Default, typography.bodyLarge.fontFamily)
    assertEquals(FontWeight.Normal, typography.bodyLarge.fontWeight)
    assertEquals(16.sp, typography.bodyLarge.fontSize)
    assertEquals(24.sp, typography.bodyLarge.lineHeight)
    assertEquals(0.5.sp, typography.bodyLarge.letterSpacing)

    // Add more assertions for other typography styles if needed
  }
}
