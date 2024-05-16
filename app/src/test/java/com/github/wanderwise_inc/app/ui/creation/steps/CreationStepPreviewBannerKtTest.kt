package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreationStepPreviewBannerKtTest {
  @get:Rule val composeTestRule = createComposeRule()

  @MockK private lateinit var createItineraryViewModelTest: CreateItineraryViewModel

  val title = FakeItinerary.SWITZERLAND.title
  // val itineraryBuilder = Itinerary.Builder(uid = "0", userUid = "me", title = title)

  @Before
  fun setup() {
    MockKAnnotations.init(this)
  }

  @Test
  fun `test if the banner screen is composed`() {
    composeTestRule.setContent {
      every { createItineraryViewModelTest.getFocusedItinerary() } returns FakeItinerary.SWITZERLAND

      CreationStepPreviewBanner(createItineraryViewModel = createItineraryViewModelTest)
    }
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_BANNER).assertIsDisplayed()
  }

  @Test
  fun `title is displayed`() {
    composeTestRule.setContent {
      every { createItineraryViewModelTest.getFocusedItinerary() } returns FakeItinerary.SWITZERLAND

      CreationStepPreviewBanner(createItineraryViewModel = createItineraryViewModelTest)
    }
    composeTestRule.onNodeWithText(title).assertIsDisplayed()
  }

  // makes the code stupid
  //    @Test
  //    fun `null itinerary displays 3 dummys`(){
  //        composeTestRule.setContent {
  //            every { createItineraryViewModelTest.getNewItinerary() } returns null
  //
  //            CreationStepPreviewBanner(createItineraryViewModel = createItineraryViewModelTest)
  //        }
  //    }

}
