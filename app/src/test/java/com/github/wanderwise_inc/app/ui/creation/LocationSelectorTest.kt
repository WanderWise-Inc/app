package com.github.wanderwise_inc.app.ui.creation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.LocationsRepository
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.model.location.PlacesReader
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.creation.steps.CreateLiveItinerary
import com.github.wanderwise_inc.app.ui.creation.steps.LocationSelector
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.github.wanderwise_inc.app.viewmodel.UserLocationClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreateItineraryMapUITest {

  /*   @get:Rule
  val composeTestRule = createComposeRule()
  @MockK
  private lateinit var mockViewModel: CreateItineraryViewModel*/

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

  @Mock private lateinit var createItineraryViewModel: CreateItineraryViewModel

  @Mock private lateinit var profileRepository: ProfileRepository
  @Mock private lateinit var imageRepository: ImageRepository
  @Mock private lateinit var directionsRepository: DirectionsRepository
  @Mock private lateinit var locationsRepository: LocationsRepository
  @Mock private lateinit var userLocationClient: UserLocationClient
  @Mock private lateinit var navController: NavHostController

  private lateinit var profileViewModel: ProfileViewModel

  private val epflLat = 46.519126741544575
  private val epflLon = 6.5676006970802145
  private var showLocationSelector = mutableStateOf(false)
  private var showLiveCreation = mutableStateOf(false)

  private val locations = PlacesReader(null).readFromString()
  private val itinerary =
      Itinerary(
          userUid = "",
          locations = locations,
          title = "San Francisco Bike Itinerary",
          tags = listOf(ItineraryTags.CULTURAL, ItineraryTags.NATURE, ItineraryTags.BUDGET),
          description = "A 3-day itinerary to explore the best of San Francisco on a bike.",
          visible = true)

  @Before
  fun setUp() {
    // MockKAnnotations.init(this)
    val epflLocation = Location(epflLat, epflLon)
    val polylinePoints = MutableLiveData<List<LatLng>>()
    polylinePoints.value = listOf(LatLng(epflLat, epflLon))

    Mockito.`when`(
            directionsRepository.getPolylineWayPoints(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyList(),
                ArgumentMatchers.anyString()))
        .thenReturn(MutableLiveData(listOf(LatLng(epflLat, epflLon))))
    Mockito.`when`(
            locationsRepository.getPlaces(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyString()))
        .thenReturn(
            MutableLiveData(
                listOf(com.github.wanderwise_inc.app.model.location.Location(epflLat, epflLon))))
    Mockito.`when`(userLocationClient.getLocationUpdates(1000))
        .thenReturn(flow { emit(epflLocation) })
    val itineraryRepository = Mockito.mock(ItineraryRepository::class.java)

    // `when`(itineraryViewModel.getUserLocation()).thenReturn(flow { emit(epflLocation) })
    // `when`(itineraryViewModel.getPolylinePointsLiveData()).thenReturn(polylinePoints)

    val dummyProfile = Profile("-")
    Mockito.`when`(profileRepository.getProfile(ArgumentMatchers.anyString()))
        .thenReturn(flow { emit(dummyProfile) })
    Mockito.`when`(imageRepository.fetchImage(ArgumentMatchers.anyString()))
        .thenReturn(flow { emit(null) })

    createItineraryViewModel =
        CreateItineraryViewModel(
            itineraryRepository, directionsRepository, locationsRepository, userLocationClient)
    createItineraryViewModel.startNewItinerary(dummyProfile.userUid)

    profileViewModel = ProfileViewModel(profileRepository, imageRepository)
  }

  @Test
  fun testBackButton() {
    composeTestRule.setContent {
      CreateLiveItinerary(showLiveCreation, createItineraryViewModel, {})
    }

    composeTestRule.onNodeWithTag(TestTags.BACK_BUTTON).assertIsDisplayed()
  }

  @Test
  fun testAddLocationButton() {

    composeTestRule.setContent {
      LocationSelector(showLocationSelector, emptyList(), {}, navController)
    }

    composeTestRule.onNodeWithTag(TestTags.ADD_LOCATION_BUTTON).assertIsDisplayed()
  }

  @Test
  fun testLocation2TextField() {
    composeTestRule.setContent {
      LocationSelector(showLocationSelector, emptyList(), {}, navController)
    }

    composeTestRule.onNodeWithTag(TestTags.RESTART_ITINERARY_BUTTON).assertIsDisplayed()
  }

  @Test
  fun testStartButton() {
    composeTestRule.setContent {
      CreateLiveItinerary(showLiveCreation, createItineraryViewModel, {})
    }

    // Assert that the Start button is initially enabled
    composeTestRule.onNodeWithTag(TestTags.START_BUTTON).assertIsEnabled()

    // Click the Start button
    composeTestRule.onNodeWithTag(TestTags.START_BUTTON).performClick()

    // Assert that the Start button is now disabled
    composeTestRule.onNodeWithTag(TestTags.START_BUTTON).assertIsNotEnabled()
  }

  @Test
  fun testStopButton() {
    composeTestRule.setContent {
      CreateLiveItinerary(showLiveCreation, createItineraryViewModel, {})
    }

    // Assert that the Stop button is initially disabled
    composeTestRule.onNodeWithTag(TestTags.STOP_BUTTON).assertIsNotEnabled()

    // Click the Start button to start the tracking
    composeTestRule.onNodeWithTag(TestTags.START_BUTTON).performClick()

    // Assert that the Stop button is now enabled
    composeTestRule.onNodeWithTag(TestTags.STOP_BUTTON).assertIsEnabled()
  }
}
