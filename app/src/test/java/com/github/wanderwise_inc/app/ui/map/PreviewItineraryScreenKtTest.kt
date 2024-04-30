package com.github.wanderwise_inc.app.ui.map
//
// import androidx.compose.foundation.layout.Box
// import androidx.compose.foundation.layout.padding
// import androidx.compose.material3.Scaffold
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.test.assertIsDisplayed
// import androidx.compose.ui.test.assertIsNotDisplayed
// import androidx.compose.ui.test.junit4.createComposeRule
// import androidx.compose.ui.test.onNodeWithTag
// import androidx.compose.ui.test.performClick
// import androidx.lifecycle.MutableLiveData
// import androidx.test.ext.junit.runners.AndroidJUnit4
// import com.github.wanderwise_inc.app.data.DirectionsRepository
// import com.github.wanderwise_inc.app.data.ImageRepository
// import com.github.wanderwise_inc.app.data.ItineraryRepository
// import com.github.wanderwise_inc.app.data.ProfileRepository
// import com.github.wanderwise_inc.app.model.location.Itinerary
// import com.github.wanderwise_inc.app.model.location.ItineraryTags
// import com.github.wanderwise_inc.app.model.location.PlacesReader
// import com.github.wanderwise_inc.app.model.profile.Profile
// import com.github.wanderwise_inc.app.viewmodel.MapViewModel
// import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
// import com.github.wanderwise_inc.app.viewmodel.UserLocationClient
// import com.google.android.gms.maps.model.CameraPosition
// import com.google.android.gms.maps.model.LatLng
// import com.google.maps.android.compose.CameraPositionState
// import com.google.maps.android.compose.GoogleMap
// import com.google.maps.android.compose.rememberCameraPositionState
// import kotlinx.coroutines.flow.flow
// import org.junit.Before
// import org.junit.Rule
// import org.junit.Test
// import org.junit.runner.RunWith
// import org.mockito.ArgumentMatchers.anyList
// import org.mockito.ArgumentMatchers.anyString
// import org.mockito.Mock
// import org.mockito.Mockito
// import org.mockito.Mockito.mock
// import org.mockito.Mockito.`when`
// import org.mockito.junit.MockitoJUnit
// import org.mockito.junit.MockitoRule
//
// @RunWith(AndroidJUnit4::class)
// class PreviewItineraryScreenKtTest {
//
//  @get:Rule val composeTestRule = createComposeRule()
//
//  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()
//
//  @Mock private lateinit var mapViewModel: MapViewModel
//
//  @Mock private lateinit var profileRepository: ProfileRepository
//  @Mock private lateinit var imageRepository: ImageRepository
//  @Mock private lateinit var directionsRepository: DirectionsRepository
//  @Mock private lateinit var userLocationClient: UserLocationClient
//
//  private lateinit var profileViewModel: ProfileViewModel
//
//  private val epflLat = 46.519126741544575
//  private val epflLon = 6.5676006970802145
//
//  private val locations = PlacesReader(null).readFromString()
//  private val itinerary =
//      Itinerary(
//          userUid = "",
//          locations = locations,
//          title = "San Francisco Bike Itinerary",
//          tags = listOf(ItineraryTags.CULTURAL, ItineraryTags.NATURE, ItineraryTags.BUDGET),
//          description = "A 3-day itinerary to explore the best of San Francisco on a bike.",
//          visible = true)
//
//  @Before
//  fun setup() {
//    val epflLocation = mock(android.location.Location::class.java)
//    epflLocation.latitude = epflLat
//    epflLocation.longitude = epflLon
//    val polylinePoints = MutableLiveData<List<LatLng>>()
//    polylinePoints.value = listOf(LatLng(epflLat, epflLon))
//
//    `when`(
//            directionsRepository.getPolylineWayPoints(
//                anyString(), anyString(), anyList(), anyString()))
//        .thenReturn(MutableLiveData(listOf(LatLng(epflLat, epflLon))))
//    `when`(userLocationClient.getLocationUpdates(1000)).thenReturn(flow { emit(epflLocation) })
//    val itineraryRepository = mock(ItineraryRepository::class.java)
//
//    // `when`(mapViewModel.getUserLocation()).thenReturn(flow { emit(epflLocation) })
//    // `when`(mapViewModel.getPolylinePointsLiveData()).thenReturn(polylinePoints)
//
//    val dummyProfile = Profile("-")
//    `when`(profileRepository.getProfile(anyString())).thenReturn(flow { emit(dummyProfile) })
//    `when`(imageRepository.fetchImage(anyString())).thenReturn(flow { emit(null) })
//
//    mapViewModel = MapViewModel(itineraryRepository, directionsRepository, userLocationClient)
//    profileViewModel = ProfileViewModel(profileRepository, imageRepository)
//  }
//
//  @Test
//  fun `initial elements are displayed correctly`() {
//    composeTestRule.setContent { PreviewItineraryScreen(itinerary, mapViewModel, profileViewModel)
// }
//
//    composeTestRule.onNodeWithTag(PreviewItineraryScreenTestTags.MAIN_SCREEN).assertIsDisplayed()
//    composeTestRule
//        .onNodeWithTag(PreviewItineraryScreenTestTags.MAXIMIZED_BANNER)
//        .assertIsDisplayed()
//
// composeTestRule.onNodeWithTag(PreviewItineraryScreenTestTags.BANNER_BUTTON).assertIsDisplayed()
//    composeTestRule
//        .onNodeWithTag(PreviewItineraryScreenTestTags.CENTER_CAMERA_BUTTON)
//        .assertIsDisplayed()
//    composeTestRule.onNodeWithTag(PreviewItineraryScreenTestTags.PROFILE_PIC).assertIsDisplayed()
//  }
//
//  @Test
//  fun `pressing banner button should minimize and maximize the banner`() {
//    composeTestRule.setContent { PreviewItineraryScreen(itinerary, mapViewModel, profileViewModel)
// }
//
//    composeTestRule
//        .onNodeWithTag(PreviewItineraryScreenTestTags.MAXIMIZED_BANNER)
//        .assertIsDisplayed()
//    composeTestRule
//        .onNodeWithTag(PreviewItineraryScreenTestTags.MINIMIZED_BANNER)
//        .assertIsNotDisplayed()
//
//    // minimize the banner
//    composeTestRule.onNodeWithTag(PreviewItineraryScreenTestTags.BANNER_BUTTON).performClick()
//
//    composeTestRule
//        .onNodeWithTag(PreviewItineraryScreenTestTags.MAXIMIZED_BANNER)
//        .assertIsNotDisplayed()
//    composeTestRule
//        .onNodeWithTag(PreviewItineraryScreenTestTags.MINIMIZED_BANNER)
//        .assertIsDisplayed()
//    composeTestRule
//        .onNodeWithTag(PreviewItineraryScreenTestTags.ITINERARY_TITLE)
//        .assertIsDisplayed()
//    composeTestRule
//        .onNodeWithTag(PreviewItineraryScreenTestTags.ITINERARY_DESCRIPTION)
//        .assertIsNotDisplayed()
//
//    // maximize the banner
//    composeTestRule.onNodeWithTag(PreviewItineraryScreenTestTags.BANNER_BUTTON).performClick()
//
//    composeTestRule
//        .onNodeWithTag(PreviewItineraryScreenTestTags.MAXIMIZED_BANNER)
//        .assertIsDisplayed()
//    composeTestRule
//        .onNodeWithTag(PreviewItineraryScreenTestTags.MINIMIZED_BANNER)
//        .assertIsNotDisplayed()
//    composeTestRule
//        .onNodeWithTag(PreviewItineraryScreenTestTags.ITINERARY_TITLE)
//        .assertIsDisplayed()
//    composeTestRule
//        .onNodeWithTag(PreviewItineraryScreenTestTags.ITINERARY_DESCRIPTION)
//        .assertIsDisplayed()
//  }
//
//  @Test
//  fun `pressing center button should update camera position`() {
//    val epflLocation = Mockito.mock(android.location.Location::class.java)
//    epflLocation.latitude = epflLat
//    epflLocation.longitude = epflLon
//
//    var cameraPositionStateObserver: CameraPositionState? = null
//
//    val delta = 0.0001
//
//    composeTestRule.setContent {
//      Box {
//        val cameraPositionState = rememberCameraPositionState {
//          position =
//              CameraPosition.fromLatLngZoom(itinerary.computeCenterOfGravity().toLatLng(), 13f)
//        }
//        cameraPositionStateObserver = cameraPositionState
//        Scaffold(floatingActionButton = { CenterButton(cameraPositionState, epflLocation) }) {
//            paddingValues ->
//          GoogleMap(
//              modifier = Modifier.padding(paddingValues), cameraPositionState =
// cameraPositionState)
//        }
//      }
//    }
//
//    composeTestRule
//        .onNodeWithTag(PreviewItineraryScreenTestTags.CENTER_CAMERA_BUTTON)
//        .assertIsDisplayed()
//    // TODO test fails: java.lang.NullPointerException: CameraUpdateFactory is not initialized
//    // composeTestRule.onNodeWithTag("Center Button").performClick()
//    /*
//    cameraPositionStateObserver?.let {
//      assertEquals(it.position.target.latitude, epflLat, delta)
//      assertEquals(it.position.target.longitude, epflLon, delta)
//    }
//     */
//  }
// }
