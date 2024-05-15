package com.github.wanderwise_inc.app.ui.itineraries

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.itinerary.ItineraryBanner
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
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
class ItineraryBannerTest {
  @get:Rule val composeTestRule = createComposeRule()
    @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

  private lateinit var itinerary: Itinerary

  @Mock private lateinit var imageRepository: ImageRepository
  @Mock private lateinit var profileViewModel: ProfileViewModel

  private var itineraryUid = "some_uid"
    private val profile = Profile(userUid = "0", displayName = "me", bio = "bio")

  @Before
  fun `initialize itinerary`() {
    MockKAnnotations.init(this)
      `when`(imageRepository.fetchImage(anyString())).thenReturn(flow{emit(null)})
      `when`(profileViewModel.getProfile(anyString())).thenReturn(flow{emit(profile)})
    composeTestRule.setContent {
      itinerary =
          Itinerary(
              uid = itineraryUid,
              userUid = "Ethan",
              locations = listOf(Location(0.0, 0.0)),
              title = "Ethan's Itinerary",
              tags = listOf(ItineraryTags.BUDGET, ItineraryTags.SOCIAL),
              description = null,
              visible = true,
              numLikes = 42)

      ItineraryBanner(
          itinerary = itinerary,
          onLikeButtonClick = { _, _ -> },
          onBannerClick = {},
          imageRepository = imageRepository,
          profileViewModel = profileViewModel)
    }
  }

  @Test
  fun `Banner should be displayed`() {
    composeTestRule
        .onNodeWithTag("${TestTags.ITINERARY_BANNER}_${itineraryUid}")
        .assertIsDisplayed()
  }

  @Test
  fun `like button should be displayed and clickable`() {
    // composeTestRule.onRoot(useUnmergedTree = true).printToLog()
    val likeButton =
        composeTestRule.onNodeWithTag("${TestTags.ITINERARY_BANNER_LIKE_BUTTON}_${itineraryUid}")

    likeButton.assertExists()
    likeButton.assertIsDisplayed()
    likeButton.assertHasClickAction()
  }
}
