package com.github.wanderwise_inc.app.ui.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
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
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.LocationClient
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@RunWith(AndroidJUnit4::class)
class PreviewItineraryScreenKtTest {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

  @Mock private lateinit var itineraryViewModel: ItineraryViewModel

  @Mock private lateinit var profileRepository: ProfileRepository
  @Mock private lateinit var imageRepository: ImageRepository
  @Mock private lateinit var directionsRepository: DirectionsRepository
  @Mock private lateinit var locationsRepository: LocationsRepository
  @Mock private lateinit var locationClient: LocationClient
  @Mock private lateinit var navController: NavHostController

  private lateinit var profileViewModel: ProfileViewModel

  private val epflLat = 46.519126741544575
  private val epflLon = 6.5676006970802145

  private val locations = PlacesReader(null).readFromString()
  private val itinerary =
      Itinerary(
          userUid = "uid",
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

    `when`(
            directionsRepository.getPolylineWayPoints(
                anyString(), anyString(), anyList(), anyString()))
        .thenReturn(MutableLiveData(listOf(LatLng(epflLat, epflLon))))
    `when`(locationClient.getLocationUpdates(1000)).thenReturn(flow { emit(epflLocation) })
    val itineraryRepository = mock(ItineraryRepository::class.java)

    val dummyProfile = Profile("-")
    `when`(profileRepository.getProfile(anyString())).thenReturn(flow { emit(dummyProfile) })
    `when`(imageRepository.fetchImage(anyString())).thenReturn(flow { emit(null) })

    itineraryViewModel =
        ItineraryViewModel(
            itineraryRepository, directionsRepository, locationsRepository, locationClient)

    itineraryViewModel.setFocusedItinerary(itinerary)
    profileViewModel = ProfileViewModel(profileRepository, imageRepository)
  }

  @Test
  fun `initial elements are displayed correctly`() {
    profileViewModel.setActiveProfile(Profile("uid"))
    composeTestRule.setContent {
      PreviewItineraryScreen(itineraryViewModel, profileViewModel, imageRepository, navController)
    }

    composeTestRule.onNodeWithTag(TestTags.MAP_PREVIEW_ITINERARY_SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.MAP_MAXIMIZED_BANNER).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.MAP_BANNER_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.MAP_CENTER_CAMERA_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.MAP_PROFILE_PIC).assertIsDisplayed()
  }

  @Test
  fun `pressing banner button should minimize and maximize the banner`() {
    profileViewModel.setActiveProfile(Profile("uid"))
    composeTestRule.setContent {
      PreviewItineraryScreen(itineraryViewModel, profileViewModel, imageRepository, navController)
    }

    composeTestRule.onNodeWithTag(TestTags.MAP_MAXIMIZED_BANNER).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.MAP_MINIMIZED_BANNER).assertIsNotDisplayed()

    // minimize the banner
    composeTestRule.onNodeWithTag(TestTags.MAP_BANNER_BUTTON).performClick()

    composeTestRule.onNodeWithTag(TestTags.MAP_MAXIMIZED_BANNER).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TestTags.MAP_MINIMIZED_BANNER).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.MAP_ITINERARY_TITLE).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.MAP_ITINERARY_DESCRIPTION).assertIsNotDisplayed()

    // maximize the banner
    composeTestRule.onNodeWithTag(TestTags.MAP_BANNER_BUTTON).performClick()

    composeTestRule.onNodeWithTag(TestTags.MAP_MAXIMIZED_BANNER).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.MAP_MINIMIZED_BANNER).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TestTags.MAP_ITINERARY_TITLE).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TestTags.MAP_ITINERARY_DESCRIPTION).assertIsDisplayed()
  }

  @Test
  fun `NullItineraryScreen is displayed when focusedItinerary is null`() {
    itineraryViewModel.setFocusedItinerary(null)
    composeTestRule.setContent {
      PreviewItineraryScreen(itineraryViewModel, profileViewModel, imageRepository, navController)
    }

    composeTestRule.onNodeWithTag(TestTags.MAP_NULL_ITINERARY).assertIsDisplayed()

    // this shouldn't be displayed
    composeTestRule.onNodeWithTag(TestTags.MAP_MAXIMIZED_BANNER).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TestTags.MAP_MINIMIZED_BANNER).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TestTags.MAP_ITINERARY_TITLE).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TestTags.MAP_ITINERARY_DESCRIPTION).assertIsNotDisplayed()
  }

  @Test
  fun `Clicking on the Start Button should go to starting mode`() {
    profileViewModel.setActiveProfile(Profile("uid"))
    composeTestRule.setContent {
      PreviewItineraryScreen(itineraryViewModel, profileViewModel, imageRepository, navController)
    }

    composeTestRule.onNodeWithTag(TestTags.START_NEW_ITINERARY_STARTING).assertIsDisplayed()
  }

  @Test
  fun `delete itinerary button should exist when user is the owner`() {
    profileViewModel.setActiveProfile(Profile("uid"))
    composeTestRule.setContent {
      PreviewItineraryScreen(itineraryViewModel, profileViewModel, imageRepository, navController)
    }

    composeTestRule.onNodeWithTag(TestTags.MAP_DELETE_ITINERARY_BUTTON).assertExists()
    composeTestRule.onNodeWithTag(TestTags.MAP_DELETE_ITINERARY_BUTTON).performScrollTo()
    composeTestRule.onNodeWithTag(TestTags.MAP_DELETE_ITINERARY_BUTTON).assertIsDisplayed()
  }

  @Test
  fun `delete itinerary button shouldn't exist when user isn't the owner`() {
    profileViewModel.setActiveProfile(Profile("another_uid"))
    composeTestRule.setContent {
      PreviewItineraryScreen(itineraryViewModel, profileViewModel, imageRepository, navController)
    }

    composeTestRule.onNodeWithTag(TestTags.MAP_DELETE_ITINERARY_BUTTON).assertDoesNotExist()
  }

  @Test
  fun `delete itinerary Button should correctly delete the itinerary`() = runTest {
    val itineraryList = mutableListOf(itinerary)
    `when`(imageRepository.deleteImageFromStorage(anyString())).thenAnswer { Unit }
    `when`(itineraryViewModel.deleteItinerary(itinerary)).thenAnswer {
      itineraryList.remove(itinerary)
      Unit
    }
    profileViewModel.setActiveProfile(Profile("uid"))
    composeTestRule.setContent {
      PreviewItineraryScreen(itineraryViewModel, profileViewModel, imageRepository, navController)
    }
    composeTestRule.onNodeWithTag(TestTags.MAP_DELETE_ITINERARY_BUTTON).performScrollTo()
    composeTestRule.onNodeWithTag(TestTags.MAP_DELETE_ITINERARY_BUTTON).assertExists()
    composeTestRule.onNodeWithTag(TestTags.MAP_DELETE_ITINERARY_BUTTON).performClick()

    assertTrue(itineraryList.isEmpty())
  }
}
