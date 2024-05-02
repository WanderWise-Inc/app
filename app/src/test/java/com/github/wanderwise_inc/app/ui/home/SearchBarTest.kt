import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.uiautomator.Direction
import com.github.wanderwise_inc.app.ui.home.SearchBar
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
//@LooperMode(LooperMode.Mode.PAUSED) // Use PAUSED LooperMode to speed up tests
//@Config(manifest=Config.NONE)
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
        sliderPositionTimeState = sliderPositionTimeState,
        searchIconTag = "search_icon", // Add testTag to your SearchBar component
        priceTextTag = "price_text", // Add testTag to your Text component
        timeTextTag = "time_text" // Add testTag to your Text component
      )
    }

    composeTestRule.onNodeWithTag("search_icon").performClick()

    // Check if the dropdown menu becomes visible
    composeTestRule.onNodeWithTag("price_text").assertExists()
    composeTestRule.onNodeWithTag("time_text").assertExists()


    // Check if the dropdown menu is initially not visible
    //composeTestRule.onNodeWithText("How much do I want to spend ?").assertDoesNotExist()

    // Click the search icon
    //composeTestRule.onNodeWithContentDescription("les_controles").performClick()


    // Check if the dropdown menu becomes visible
    //composeTestRule.onNodeWithText("How much do I want to spend ?").assertExists()
    //composeTestRule.onNodeWithText("How Long do I want to wander ?").assertExists()
  }

  /*@Test
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

    // Change price range using slider
    composeTestRule.onNodeWithTag("RangeSlider").performSwipe(Direction.RIGHT) {
      // Perform gestures to move the slider to new position
      // Adjust based on your actual implementation
    }

    // Change time range using slider
    composeTestRule.onNodeWithTag("RangeSlider").performGesture {
      // Perform gestures to move the slider to new position
      // Adjust based on your actual implementation
    }

    // Check if slider positions have been updated correctly
    composeTestRule.runOnUiThread {
      assert(sliderPositionPriceState.value == newPriceRange)
      assert(sliderPositionTimeState.value == newTimeRange)
    }
  }
*/
  // Add more tests as needed for string formatting and range validation
}
