package com.github.wanderwise_inc.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.ui.itinerary.ItineraryBanner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ItineraryBannerTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var itinerary: Itinerary

  @Before
  fun `initialize itinerary`() {
    composeTestRule.setContent {
      itinerary =
          Itinerary(
              userUid = "Ethan",
              locations = listOf(Location(0.0, 0.0)),
              title = "Ethan's Itinerary",
              tags = listOf(ItineraryTags.BUDGET, ItineraryTags.SOCIAL),
              description = null,
              visible = true)

      ItineraryBanner(itinerary = itinerary, onLikeButtonClick = { _, _ -> }, onBannerClick = {})
    }
  }

  @Test
  fun `Banner should be displayed`() {
    composeTestRule.onNodeWithTag("Itinerary banner").assertIsDisplayed()
  }
}
