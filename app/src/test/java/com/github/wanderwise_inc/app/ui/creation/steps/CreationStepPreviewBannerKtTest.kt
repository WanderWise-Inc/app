package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import io.mockk.MockKAnnotations
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
  // val itineraryBuilder = Itinerary.Builder(uid = "0", userUid = "me", title = title)

  @Before
  fun setup() {
    MockKAnnotations.init(this)
    `when`(imageRepository.fetchImage(anyString())).thenReturn(flow { emit(null) })
    `when`(profileViewModel.getProfile(anyString())).thenReturn(flow { emit(profile) })
  }

  @Test
  fun `test if the banner screen is composed`() = runTest {
    composeTestRule.setContent {
      // every { createItineraryViewModelTest.getNewItinerary() } returns itineraryBuilder

      CreationStepPreviewBanner(
          createItineraryViewModel = createItineraryViewModelTest,
          imageRepository = imageRepository,
          profileViewModel = profileViewModel)
    }
    composeTestRule.onNodeWithTag(TestTags.CREATION_SCREEN_PREVIEW_BANNER).assertIsDisplayed()
  }

  @Test
  fun `title is displayed`() = runTest {
    composeTestRule.setContent {
      // every { createItineraryViewModelTest.getNewItinerary() } returns itineraryBuilder

      CreationStepPreviewBanner(
          createItineraryViewModel = createItineraryViewModelTest,
          imageRepository = imageRepository,
          profileViewModel = profileViewModel)
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
