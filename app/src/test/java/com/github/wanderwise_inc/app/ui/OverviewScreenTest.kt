package com.github.wanderwise_inc.app.ui

import android.app.Application
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.data.ProfileRepositoryTestImpl
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.itinerary.ItineraryBannerTestTags
import com.github.wanderwise_inc.app.ui.list_itineraries.OverviewScreen
import com.github.wanderwise_inc.app.ui.list_itineraries.OverviewScreenTestTags
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.github.wanderwise_inc.app.viewmodel.UserLocationClient
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OverviewScreenTest {
  private val profile = Profile(userUid = "testing")
  val itinerary =
      Itinerary(
          userUid = "Ethan",
          locations = listOf(Location(0.0, 0.0)),
          title = "test itinerary. Don't go on this one uwu",
          tags = listOf(ItineraryTags.BUDGET, ItineraryTags.SOCIAL),
          description = null,
          visible = true)

  @get:Rule val composeTestRule = createComposeRule()

  @Mock private lateinit var mockApplication: Application
  @Mock private lateinit var mockContext: Context

  @Mock private lateinit var profileRepository: ProfileRepository
  @Mock private lateinit var imageRepository: ImageRepository
  @Mock private lateinit var directionsRepository: DirectionsRepository
  @Mock private lateinit var userLocationClient: UserLocationClient
  @Mock private lateinit var navHostController: NavHostController

  private lateinit var itineraryRepository: ItineraryRepository
  private lateinit var mapViewModel: MapViewModel
  private lateinit var profileViewModel: ProfileViewModel

  private val epflLat = 46.519126741544575
  private val epflLon = 6.5676006970802145

  @Before
  fun `setup environment`() {
    composeTestRule.setContent {
      // mock application context
      FirebaseApp.initializeApp(LocalContext.current)
      mockApplication = Mockito.mock(Application::class.java)
      mockContext = Mockito.mock(Context::class.java)
      Mockito.`when`(mockApplication.applicationContext).thenReturn(mockContext)

      directionsRepository = Mockito.mock(DirectionsRepository::class.java)
      imageRepository = Mockito.mock(ImageRepository::class.java)
      userLocationClient = Mockito.mock(UserLocationClient::class.java)

      itineraryRepository = ItineraryRepositoryTestImpl()
      profileRepository = ProfileRepositoryTestImpl()
      // imageRepository = ImageRepositoryImpl(mockApplication)

      navHostController = mock(NavHostController::class.java)

      mapViewModel = MapViewModel(itineraryRepository, directionsRepository, userLocationClient)
      profileViewModel = ProfileViewModel(profileRepository, imageRepository)

      OverviewScreen(mapViewModel, profileViewModel, navHostController)
    }
  }

  @Before
  fun `add an itinerary to MapViewModel`() = runTest {
    profileRepository.setProfile(profile)
    mapViewModel.setItinerary(itinerary)
  }

  @Test
  fun `category selector should be displayed on liked screen`() {
    composeTestRule.onNodeWithTag(TestTags.CATEGORY_SELECTOR).assertIsDisplayed()
  }

  @Test
  fun `at least one itinerary should be displayed`() {
    composeTestRule
        .onNodeWithTag("${ItineraryBannerTestTags.ITINERARY_BANNER}_${itinerary.uid}")
        .isDisplayed()
  }
}
