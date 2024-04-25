package com.github.wanderwise_inc.app.ui

import android.app.Application
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ImageRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.data.ProfileRepositoryTestImpl
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.list_itineraries.LikedScreen
import com.github.wanderwise_inc.app.ui.list_itineraries.LikedScreenTestTags
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.FirebaseApp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LikedScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Mock private lateinit var mockApplication: Application
  @Mock private lateinit var mockContext: Context

  private lateinit var itineraryRepository: ItineraryRepository
  private lateinit var profileRepository: ProfileRepository
  private lateinit var imageRepository: ImageRepository
  private lateinit var mapViewModel: MapViewModel
  private lateinit var profileViewModel: ProfileViewModel

  @Before
  fun `setup environment`() {
    composeTestRule.setContent {
      FirebaseApp.initializeApp(LocalContext.current)
      mockApplication = Mockito.mock(Application::class.java)
      mockContext = Mockito.mock(Context::class.java)

      Mockito.`when`(mockApplication.applicationContext).thenReturn(mockContext)

      itineraryRepository = ItineraryRepositoryTestImpl()
      profileRepository = ProfileRepositoryTestImpl()
      imageRepository = ImageRepositoryTestImpl(mockApplication)

      mapViewModel = MapViewModel(itineraryRepository)
      profileViewModel = ProfileViewModel(profileRepository, imageRepository)

      LikedScreen(mapViewModel, profileViewModel)
    }
  }

  @Before
  fun `add an itinerary to MapViewModel, and like it`() {
    val itinerary =
        Itinerary(
            userUid = "Ethan",
            locations = listOf(Location(0.0, 0.0)),
            title = "Ethan's Itinerary",
            tags = listOf(ItineraryTags.BUDGET, ItineraryTags.SOCIAL),
            description = null,
            visible = true)

    val profile = Profile(userUid = "testing")

    profileRepository.setProfile(profile)
    mapViewModel.setItinerary(itinerary)
    profileViewModel.addLikedItinerary(profile.userUid, itinerary.uid)
  }

  @Test
  fun `verify that category selector is displayed on liked screen`() {
    composeTestRule.onNodeWithTag(LikedScreenTestTags.CATEGORY_SELECTOR).assertIsDisplayed()
  }
}
