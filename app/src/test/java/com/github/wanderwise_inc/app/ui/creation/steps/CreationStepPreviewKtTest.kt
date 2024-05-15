package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.github.wanderwise_inc.app.model.location.ItineraryLabels
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
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
class CreationStepPreviewKtTest() {
  @get:Rule val composeTestRule = createComposeRule()

  @MockK private lateinit var createItineraryViewModel: CreateItineraryViewModel
  @MockK private lateinit var profileViewModel: ProfileViewModel

  private lateinit var onFinished: () -> Unit

  private var success: Boolean = false

  @Before
  fun setup() {
    MockKAnnotations.init(this)
  }

  @Test
  fun `verify that the finish button is composed`() {
    composeTestRule.setContent {
      every { createItineraryViewModel.notSetValues() } returns
          listOf(ItineraryLabels.TITLE, ItineraryLabels.DESCRIPTION)
      every { createItineraryViewModel.uploadNewItinerary() } returns Unit

      success = false
      onFinished = { success = true }

      CreationStepPreview(
          createItineraryViewModel = createItineraryViewModel,
          profileViewModel = profileViewModel,
          onFinished = onFinished)
    }

    composeTestRule.onNodeWithTag(TestTags.CREATION_FINISH_BUTTON).assertIsDisplayed()
  }

  @Test
  fun `clicking on finish with unset fields displays a popup telling us what we haven't set`() {
    composeTestRule.setContent {
      every { createItineraryViewModel.notSetValues() } returns
          listOf(ItineraryLabels.TITLE, ItineraryLabels.DESCRIPTION)
      every { createItineraryViewModel.uploadNewItinerary() } returns Unit

      success = false
      onFinished = { success = true }

      CreationStepPreview(
          createItineraryViewModel = createItineraryViewModel,
          profileViewModel = profileViewModel,
          onFinished = onFinished)
    }
    composeTestRule.onNodeWithTag(TestTags.CREATION_FINISH_BUTTON).performClick()

    composeTestRule.onNodeWithTag(TestTags.HINT_POPUP).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TestTags.HINT_POPUP_MESSAGE)
        .assertTextContains(ItineraryLabels.TITLE, true)
    composeTestRule
        .onNodeWithTag(TestTags.HINT_POPUP_MESSAGE)
        .assertTextContains(ItineraryLabels.DESCRIPTION, true)

    // didn't call onFinished
    assert(!success)
  }

  @Test
  fun `clicking on finish with all fields set calls onFinish button and doesn't display popup`() {
    composeTestRule.setContent {
      every { createItineraryViewModel.notSetValues() } returns listOf()
      every { createItineraryViewModel.uploadNewItinerary() } returns Unit

      success = false
      onFinished = { success = true }

      CreationStepPreview(
          createItineraryViewModel = createItineraryViewModel,
          profileViewModel = profileViewModel,
          onFinished = onFinished)
    }
    composeTestRule.onNodeWithTag(TestTags.CREATION_FINISH_BUTTON).performClick()
    composeTestRule.onNodeWithTag(TestTags.HINT_POPUP).assertIsNotDisplayed()
    assert(success)
  }
}
