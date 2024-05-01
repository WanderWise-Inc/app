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
import com.github.wanderwise_inc.app.ui.list_itineraries.LikedScreen
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.github.wanderwise_inc.app.viewmodel.UserLocationClient
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LikedScreenTest {
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
  @Mock private lateinit var firebaseUser: FirebaseUser
  @Mock private lateinit var firebaseAuth: FirebaseAuth

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
      mockApplication = mock(Application::class.java)
      mockContext = mock(Context::class.java)
      Mockito.`when`(mockApplication.applicationContext).thenReturn(mockContext)

      directionsRepository = mock(DirectionsRepository::class.java)
      imageRepository = mock(ImageRepository::class.java)
      userLocationClient = mock(UserLocationClient::class.java)

      itineraryRepository = ItineraryRepositoryTestImpl()
      profileRepository = ProfileRepositoryTestImpl()
      // imageRepository = ImageRepositoryImpl(mockApplication)

      mapViewModel = MapViewModel(itineraryRepository, directionsRepository, userLocationClient)
      profileViewModel = ProfileViewModel(profileRepository, imageRepository)

      navHostController = mock(NavHostController::class.java)
      firebaseAuth = mock(FirebaseAuth::class.java)
      firebaseUser = mock(FirebaseUser::class.java)
      `when`(firebaseAuth.currentUser).thenReturn(firebaseUser)
      `when`(firebaseUser.uid).thenReturn(null)

      LikedScreen(mapViewModel, profileViewModel, navHostController, firebaseAuth)
    }
  }

  @Before
  fun `add an itinerary to MapViewModel, and like it`() = runTest {
    profileRepository.setProfile(profile)
    mapViewModel.setItinerary(itinerary)
    profileViewModel.addLikedItinerary(profile.userUid, itinerary.uid)
  }

  @Test
  fun `category selector should be displayed on liked screen`() {
    composeTestRule.onNodeWithTag(TestTags.CATEGORY_SELECTOR).assertIsDisplayed()
  }

  @Test
  fun `a liked itinerary should be displayed`() {
    composeTestRule
        .onNodeWithTag("${ItineraryBannerTestTags.ITINERARY_BANNER}_${itinerary.uid}")
        .isDisplayed()
  }
}
