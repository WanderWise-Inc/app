package com.github.wanderwise_inc.app.ui.creation.steps

import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import coil.request.ImageRequest
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.PlacesReader
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreationStepChooseTagsTest {
  @get:Rule val composeTestRule = createComposeRule()

  @MockK private lateinit var createItineraryViewModel: CreateItineraryViewModel
  @MockK private lateinit var imageRepository: ImageRepository
  @MockK private lateinit var itineraryBuilder: Itinerary.Builder
  @MockK private lateinit var imageRequestBuilder: ImageRequest.Builder
  @MockK private lateinit var imageRequest: ImageRequest

  private val epflLat = 46.519126741544575
  private val epflLon = 6.5676006970802145

  private val locations = PlacesReader(null).readFromString()
  private val itinerary =
      Itinerary(
          userUid = "uid",
          locations = locations,
          title = "San Francisco Bike Itinerary",
          tags = listOf(ItineraryTags.CULTURAL, ItineraryTags.NATURE, ItineraryTags.BUDGET),
          description = "A 3-day itinerary to explore the best of San Francisco on a bike.",
          visible = true,
          price = 10f)

  private val tagsList = itinerary.tags.toMutableList()

  private val imageUri =
      Uri.parse(
          "https://firebasestorage.googleapis.com/v0/b/wanderwise-d8d36.appspot.com/o/images%2FitineraryPictures%2FdefaultItinerary.png?alt=media&token=b7170586-9168-445b-8784-8ad3ac5345bc")

  @Before
  fun setup() {
    MockKAnnotations.init(this)

    every { imageRepository.setIsItineraryImage(true) } just Runs
    every { imageRepository.setOnImageSelectedListener(any()) } just Runs
    every { itineraryBuilder.price } returns itinerary.price
    every { createItineraryViewModel.getNewItinerary() } returns itineraryBuilder
    every { itineraryBuilder.tags } returns tagsList
    every { itineraryBuilder.build() } returns itinerary
    every { itineraryBuilder.visible } returns itinerary.visible
    every { itineraryBuilder.time } returns itinerary.time
  }

  @Test
  fun `if the itinerary image banner is displayed, the screen should be displayed and if image is null then a text should appear`() {
    every { imageRepository.getCurrentFile() } returns null
    composeTestRule.setContent {
      CreationStepChooseTagsScreen(
          createItineraryViewModel = createItineraryViewModel, imageRepository = imageRepository)
    }
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_TAGS).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_IMAGE_BANNER_BOX).assertIsDisplayed()
    composeTestRule.onNodeWithText("Itinerary Banner Please Upload Image").assertIsDisplayed()
  }

  @Test
  fun `if the itinerary image banner is displayed, the screen should be displayed and if image is not null then the image should appear`() {
    every { imageRepository.getCurrentFile() } returns imageUri
    every { imageRequestBuilder.data(imageUri) } returns imageRequestBuilder
    every { imageRequestBuilder.crossfade(500) } returns imageRequestBuilder
    every { imageRequestBuilder.build() } returns imageRequest
    composeTestRule.setContent {
      CreationStepChooseTagsScreen(
          createItineraryViewModel = createItineraryViewModel, imageRepository = imageRepository)
    }
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_TAGS).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_IMAGE_BANNER_BOX).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TestTags.CREATION_SCREEN_IMAGE_BANNER, useUnmergedTree = true)
        .assertIsDisplayed()
  }

  @Test
  fun `if the box is clicked, it should correctly launch an activity`() {
    var isLaunched = false
    every { imageRepository.launchActivity(any()) } answers { isLaunched = true }
    every { imageRepository.getCurrentFile() } returns null
    composeTestRule.setContent {
      CreationStepChooseTagsScreen(
          createItineraryViewModel = createItineraryViewModel, imageRepository = imageRepository)
    }
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_IMAGE_BANNER_BOX).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_IMAGE_BANNER_BOX).performClick()
    assertTrue(isLaunched)
  }

  @Test
  fun `click to choose tags should display the tags`() {
    val allTags =
        listOf(
            "Adventure",
            "Food",
            "Culture",
            "Nature",
            "History",
            "Relaxation",
            "Shopping",
            "Nightlife")
    every { imageRepository.getCurrentFile() } returns null
    composeTestRule.setContent {
      CreationStepChooseTagsScreen(
          createItineraryViewModel = createItineraryViewModel, imageRepository = imageRepository)
    }
    composeTestRule.onNodeWithTag(TestTags.ITINERARY_CREATION_TAGS).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.ITINERARY_CREATION_TAGS).performClick()
    for (tag in allTags) {
      composeTestRule.onNodeWithText(tag).assertIsDisplayed()
      composeTestRule.onNodeWithText(tag).performClick()
      composeTestRule.onNodeWithTag(TestTags.ITINERARY_CREATION_TAGS).performClick()
    }
  }
}
