import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.wanderwise_inc.app.ui.home.SearchBar
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import android.os.Build

class SearchBarTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  @Before
  fun setup() {
    mockkStatic(Build::class)
    every { Build.FINGERPRINT } returns "mock_fingerprint"
  }

  @Test
  fun testSearchBar() {
    // Initialize the states
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

    // Test the TextField
    composeTestRule.onNodeWithTag("SearchBar")
      .assertExists()
      .assertIsDisplayed()
      .performTextInput("Test input")

    // Test the DropdownMenu
    composeTestRule.onNodeWithText("How much do I want to spend ?")
      .assertExists()
      .assertIsDisplayed()

    composeTestRule.onNodeWithText("How Long do I want to wander ?")
      .assertExists()
      .assertIsDisplayed()

    // Test the RangeSliders
    composeTestRule.onNode(hasTestTag("RangeSlider"))
      .assertExists()
      .assertIsDisplayed()
  }
}