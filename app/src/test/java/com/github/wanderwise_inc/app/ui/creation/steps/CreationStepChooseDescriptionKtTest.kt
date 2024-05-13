package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreationStepChooseDescriptionKtTest {
  @get:Rule val composeTestRule = createComposeRule()

  @MockK private lateinit var createItineraryViewModelTest: CreateItineraryViewModel

  private val title = "test title"
  private val description = "test description"

  lateinit var nullItineraryBuilder: Itinerary.Builder
  lateinit var setItineraryBuilder: Itinerary.Builder

  @Test
  fun `page composes with both title and description composable`() {
    MockKAnnotations.init(this)
    composeTestRule.setContent {
      nullItineraryBuilder = Itinerary.Builder(userUid = "1")
      every { createItineraryViewModelTest.getNewItinerary() } returns nullItineraryBuilder

      CreationStepChooseDescriptionScreen(createItineraryViewModel = createItineraryViewModelTest)
    }

    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_DESCRIPTION_TITLE).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_TITLE).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_DESCRIPTION).assertIsDisplayed()
  }

  @Test
  fun `writing inside title and description box changes the text`() {
    MockKAnnotations.init(this)
    composeTestRule.setContent {
      nullItineraryBuilder = Itinerary.Builder(userUid = "1")
      every { createItineraryViewModelTest.getNewItinerary() } returns nullItineraryBuilder

      CreationStepChooseDescriptionScreen(createItineraryViewModel = createItineraryViewModelTest)
    }
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_TITLE).performTextInput(title)
    composeTestRule
        .onNodeWithTag(TestTags.CREATION_SCREEN_DESCRIPTION)
        .performTextInput(description)

    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_TITLE).assertTextContains(value = title)
    composeTestRule
        .onNodeWithTag(TestTags.CREATION_SCREEN_DESCRIPTION)
        .assertTextContains(value = description)
  }

  @Test
  fun `writing inside the text and description box changes createItineraryViewModel newItinerary`() {
    MockKAnnotations.init(this)
    composeTestRule.setContent {
      nullItineraryBuilder = Itinerary.Builder(userUid = "1")
      every { createItineraryViewModelTest.getNewItinerary() } returns nullItineraryBuilder

      CreationStepChooseDescriptionScreen(createItineraryViewModel = createItineraryViewModelTest)
    }
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_TITLE).performTextInput(title)
    composeTestRule
        .onNodeWithTag(TestTags.CREATION_SCREEN_DESCRIPTION)
        .performTextInput(description)

    assertEquals(nullItineraryBuilder.title, title)
    assertEquals(nullItineraryBuilder.description, description)
  }

  @Test
  fun `if the title and description has already been set they are the one inside the text field`() {
    MockKAnnotations.init(this)
    composeTestRule.setContent {
      setItineraryBuilder =
          Itinerary.Builder(userUid = "1", title = title, description = description)
      every { createItineraryViewModelTest.getNewItinerary() } returns setItineraryBuilder

      CreationStepChooseDescriptionScreen(createItineraryViewModel = createItineraryViewModelTest)
    }

    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_TITLE).assertTextContains(value = title)
    composeTestRule
        .onNodeWithTag(TestTags.CREATION_SCREEN_DESCRIPTION)
        .assertTextContains(value = description)
  }
}
