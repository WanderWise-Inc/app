package com.github.wanderwise_inc.app.ui.home
/*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.uiautomator.Direction
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.home.SearchBar
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)

class SearchBarTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun testDropdownMenuVisibility() {
    val sliderPositionPriceState = mutableStateOf(0f..100f)
    val sliderPositionTimeState = mutableStateOf(0f..24f)

    composeTestRule.setContent {
      SearchBar(
        onSearchChange = {},
        onPriceChange = {},
        sliderPositionPriceState = sliderPositionPriceState,
        sliderPositionTimeState = sliderPositionTimeState
      )
    }
    // Act
    composeTestRule.onNode(hasTestTag(TestTags.SEARCH_ICON)).performClick()
    // Assert
    composeTestRule.onNode(hasTestTag(TestTags.SEARCH_DROPDOWN)).assertExists()

  }

  @Test
  fun testSliderPositionChange() {
    // Define initial slider positions
    val sliderPositionPriceState = mutableStateOf(0f..100f)
    val sliderPositionTimeState = mutableStateOf(0f..24f)
    val initialPriceRange = 0f..100f
    val initialTimeRange = 0f..24f

    // Define new slider positions
    val newPriceRange = 20f..80f
    val newTimeRange = 4f..20f

    composeTestRule.setContent {
      SearchBar(
        onSearchChange = {},
        onPriceChange = {},
        sliderPositionPriceState = mutableStateOf(initialPriceRange),
        sliderPositionTimeState = mutableStateOf(initialTimeRange)
      )
    }

    // Open dropdown menu
    composeTestRule.onNodeWithContentDescription("les_controles").performClick()


    // Change time range using slider
    composeTestRule.onNodeWithTag(TestTags.TIME_SEARCH).performTouchInput {

    }
    // Change time range using slider
    composeTestRule.onNodeWithTag(TestTags.PRICE_SEARCH).performTouchInput {

    }

    // Check if slider positions have been updated correctly
    composeTestRule.runOnUiThread {
      assert(sliderPositionPriceState.value == newPriceRange)
      assert(sliderPositionTimeState.value == newTimeRange)
    }
  }

  // Add more tests as needed for string formatting and range validation
}
*/
