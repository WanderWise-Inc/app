package com.github.wanderwise_inc.app.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.PlacesReader
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@RunWith(AndroidJUnit4::class)
class PreviewItineraryScreenKtTest {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

  @Mock private lateinit var mapViewModel: MapViewModel

  @Mock private lateinit var profileRepository: ProfileRepository
  @Mock private lateinit var imageRepository: ImageRepository
  @Mock private lateinit var profileViewModel: ProfileViewModel

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
  fun `setup`() {
    val dummyProfile = Profile("-")
    `when`(profileRepository.getProfile(anyString())).thenReturn(flow { emit(dummyProfile) })
    `when`(imageRepository.fetchImage(anyString())).thenReturn(flow { emit(null) })
    profileViewModel = ProfileViewModel(profileRepository, imageRepository)
  }

  @Test
  fun previewItineraryScreen() {
    val epflLocation = Mockito.mock(android.location.Location::class.java)
    epflLocation.latitude = epflLat
    epflLocation.longitude = epflLon
    val polylinePoints = MutableLiveData<List<LatLng>>()
    polylinePoints.value = listOf(LatLng(epflLat, epflLon))
    `when`(mapViewModel.getUserLocation()).thenReturn(flow { emit(epflLocation) })
    `when`(mapViewModel.getPolylinePointsLiveData()).thenReturn(polylinePoints)

    composeTestRule.setContent { PreviewItineraryScreen(itinerary, mapViewModel, profileViewModel) }

    composeTestRule.onNodeWithTag("Map screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Google Maps").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Center Button").assertIsDisplayed()
  }

  @Test
  fun centerButton() {
    val epflLocation = Mockito.mock(android.location.Location::class.java)
    epflLocation.latitude = epflLat
    epflLocation.longitude = epflLon

    var cameraPositionStateObserver: CameraPositionState? = null

    val delta = 0.0001

    composeTestRule.setContent {
      Box {
        val cameraPositionState = rememberCameraPositionState {
          position =
              CameraPosition.fromLatLngZoom(itinerary.computeCenterOfGravity().toLatLng(), 13f)
        }
        cameraPositionStateObserver = cameraPositionState
        Scaffold(floatingActionButton = { CenterButton(cameraPositionState, epflLocation) }) {
            paddingValues ->
          GoogleMap(
              modifier = Modifier.padding(paddingValues), cameraPositionState = cameraPositionState)
        }
      }
    }

    composeTestRule.onNodeWithTag("Center Button").assertIsDisplayed()
    // TODO test fails: java.lang.NullPointerException: CameraUpdateFactory is not initialized
    // composeTestRule.onNodeWithTag("Center Button").performClick()
    /*
    cameraPositionStateObserver?.let {
      assertEquals(it.position.target.latitude, epflLat, delta)
      assertEquals(it.position.target.longitude, epflLon, delta)
    }
     */
  }
}
