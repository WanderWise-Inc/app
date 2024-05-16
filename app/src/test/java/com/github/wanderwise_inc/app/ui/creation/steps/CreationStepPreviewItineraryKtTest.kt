package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.lifecycle.MutableLiveData
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
import com.github.wanderwise_inc.app.ui.map.PreviewItineraryScreen
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.github.wanderwise_inc.app.viewmodel.UserLocationClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.flow
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreationStepPreviewItineraryKtTest {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

  @Mock private lateinit var createItineraryViewModel: CreateItineraryViewModel

  @Mock private lateinit var profileRepository: ProfileRepository
  @Mock private lateinit var imageRepository: ImageRepository
  @Mock private lateinit var directionsRepository: DirectionsRepository
  @Mock private lateinit var locationsRepository: LocationsRepository
  @Mock private lateinit var userLocationClient: UserLocationClient

  private lateinit var profileViewModel: ProfileViewModel

  private val epflLat = 46.519126741544575
  private val epflLon = 6.5676006970802145

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
  fun setup() {
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
    `when`(createItineraryViewModel.getNewItinerary()).thenReturn(null)

    val dummyProfile = Profile("-")
    Mockito.`when`(profileRepository.getProfile(ArgumentMatchers.anyString()))
        .thenReturn(flow { emit(dummyProfile) })
    Mockito.`when`(imageRepository.fetchImage(ArgumentMatchers.anyString()))
        .thenReturn(flow { emit(null) })

    createItineraryViewModel =
        CreateItineraryViewModel(
            itineraryRepository, directionsRepository, locationsRepository, userLocationClient)
    createItineraryViewModel.setFocusedItinerary(itinerary)
    profileViewModel = ProfileViewModel(profileRepository, imageRepository)
  }

  @Test
  fun `PreviewItineraryScreen is called`() {
    composeTestRule.setContent {
      PreviewItineraryScreen(
          itineraryViewModel = createItineraryViewModel, profileViewModel = profileViewModel)
    }

    composeTestRule.onNodeWithTag(TestTags.MAP_PREVIEW_ITINERARY_SCREEN).assertIsDisplayed()
  }
}
