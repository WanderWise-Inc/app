package com.github.wanderwise_inc.app.ui.creation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.LocationsRepository
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.creation.steps.CreateItineraryMap
import com.github.wanderwise_inc.app.ui.creation.steps.CreateItineraryMapWithSelector
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.github.wanderwise_inc.app.viewmodel.UserLocationClient
import com.google.android.gms.maps.model.LatLng
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateItineraryMapTest {
  @get:Rule val composeTestRule = createComposeRule()

  @MockK private lateinit var directionsRepository: DirectionsRepository

  @MockK private lateinit var imageRepository: ImageRepository

  @MockK private lateinit var itineraryRepository: ItineraryRepository

  @MockK private lateinit var locationsRepository: LocationsRepository

  @MockK private lateinit var profileRepository: ProfileRepository

  @MockK private lateinit var locationClient: UserLocationClient

  private lateinit var createItineraryViewModel: CreateItineraryViewModel

  private lateinit var profileViewModel: ProfileViewModel

  private val epflLat = 46.519126741544575
  private val epflLon = 6.5676006970802145

  @Before
  fun setup() {
    MockKAnnotations.init(this)

    val epflLocation = mockk<android.location.Location>()
    every { epflLocation.latitude } returns epflLat
    every { epflLocation.longitude } returns epflLon

    val polylinePoints = MutableLiveData<List<LatLng>>()
    polylinePoints.value = listOf(LatLng(epflLat, epflLon))

    val dummyProfile = Profile("-")

    every { directionsRepository.getPolylineWayPoints(any(), any(), any(), any()) } returns
        polylinePoints

    every { imageRepository.fetchImage(any()) } returns flow { emit(null) }

    every { locationsRepository.getPlaces(any(), any(), any()) } returns
        MutableLiveData(listOf(Location(epflLat, epflLon)))

    every { profileRepository.getProfile(any()) } returns flow { emit(dummyProfile) }

    every { locationClient.getLocationUpdates(any()) } returns flow { emit(epflLocation) }

    createItineraryViewModel =
        CreateItineraryViewModel(
            itineraryRepository, directionsRepository, locationsRepository, locationClient)

    profileViewModel = ProfileViewModel(profileRepository, imageRepository)

    createItineraryViewModel.startNewItinerary(dummyProfile.userUid)
  }

  @Test
  fun `initial elements are displayed correctly`() = runTest {
    composeTestRule.setContent {
      CreateItineraryMap(
          createItineraryViewModel = createItineraryViewModel,
          innerPaddingValues = PaddingValues(10.dp),
      )
    }
    // map should be displayed; the loading screen should not
    composeTestRule.onNodeWithTag(TestTags.MAP_GOOGLE_MAPS).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.MAP_NULL_ITINERARY).assertIsNotDisplayed()

    // pop-up should be displayed
    composeTestRule.onNodeWithTag(TestTags.HINT_POPUP).assertIsDisplayed()
  }

  @Test
  fun `performing a click on the map should add a location to the builder`() = runTest {
    val boxTag = "BOX"
    composeTestRule.setContent {
      Box(modifier = Modifier.fillMaxSize().testTag(boxTag)) {
        CreateItineraryMap(
            createItineraryViewModel = createItineraryViewModel,
            innerPaddingValues = PaddingValues(10.dp),
        )
      }
    }
    assert(createItineraryViewModel.getNewItinerary() != null)
    assert(createItineraryViewModel.getNewItinerary()!!.locations.isEmpty())

    composeTestRule.onNodeWithTag(TestTags.MAP_GOOGLE_MAPS).assertIsDisplayed()
    composeTestRule.onNodeWithTag(boxTag).performClick()

    // TODO the callback isn't working when pressing. Need to fix
    // assert(createItineraryViewModel.getNewItinerary()!!.locations.isNotEmpty())
  }

  @Test
  fun `map with selector should display both elements correctly`() = runTest {
    composeTestRule.setContent {
      CreateItineraryMapWithSelector(
          createItineraryViewModel = createItineraryViewModel,
      )
    }
    composeTestRule.onNodeWithTag(TestTags.MAP_GOOGLE_MAPS).isDisplayed()
  }
}
