package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToIndex
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreationStepPreviewBannerKtTest {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

  @MockK private lateinit var createItineraryViewModelTest: CreateItineraryViewModel
  @Mock private lateinit var imageRepository: ImageRepository
  @Mock private lateinit var profileViewModel: ProfileViewModel
  private val profile = Profile(userUid = "0", displayName = "me", bio = "bio")

  val title = FakeItinerary.SWITZERLAND.title

  @Before
  fun setup() {
    MockKAnnotations.init(this)
    `when`(imageRepository.fetchImage(anyString())).thenReturn(flow { emit(null) })
    `when`(profileViewModel.getProfile(anyString())).thenReturn(flow { emit(profile) })
  }

  @Test
  fun `test if the banner screen is composed`() = runTest {
    composeTestRule.setContent {
      every { createItineraryViewModelTest.getFocusedItinerary() } returns FakeItinerary.SWITZERLAND

      CreationStepPreviewBanner(
          createItineraryViewModel = createItineraryViewModelTest,
          imageRepository = imageRepository,
      )
    }
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_BANNER).assertIsDisplayed()
  }

  @Test
  fun `title is displayed`() = runTest {
    composeTestRule.setContent {
      every { createItineraryViewModelTest.getFocusedItinerary() } returns FakeItinerary.SWITZERLAND

      CreationStepPreviewBanner(
          createItineraryViewModel = createItineraryViewModelTest,
          imageRepository = imageRepository,
      )
    }
    composeTestRule.onNodeWithText(title).assertIsDisplayed()
  }

  @Test
  fun `dummy Itinerary banners are displayed`() {
    composeTestRule.setContent {
      every { createItineraryViewModelTest.getFocusedItinerary() } returns FakeItinerary.SWITZERLAND
      CreationStepPreviewBanner(
          createItineraryViewModel = createItineraryViewModelTest,
          imageRepository = imageRepository,
      )
    }
    for (i in listOf(0, 1, 2)) {
      composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_BANNER).performScrollToIndex(i)
      composeTestRule.onNodeWithTag("${TestTags.DUMMY_BANNER}_$i")
    }
  }
}
