package com.github.wanderwise_inc.app.ui.creation
import android.location.Location
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.lifecycle.MutableLiveData
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.PlacesReader
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.github.wanderwise_inc.app.viewmodel.UserLocationClient
import com.google.android.gms.maps.model.LatLng
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
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

    @Mock private lateinit var itineraryViewModel: ItineraryViewModel

    @Mock private lateinit var profileRepository: ProfileRepository
    @Mock private lateinit var imageRepository: ImageRepository
    @Mock private lateinit var directionsRepository: DirectionsRepository
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
    fun setUp() {
        //MockKAnnotations.init(this)
        val epflLocation = Mockito.mock(Location::class.java)
        epflLocation.latitude = epflLat
        epflLocation.longitude = epflLon
        val polylinePoints = MutableLiveData<List<LatLng>>()
        polylinePoints.value = listOf(LatLng(epflLat, epflLon))

        Mockito.`when`(
            directionsRepository.getPolylineWayPoints(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyList(),
                ArgumentMatchers.anyString()
            )
        )
            .thenReturn(MutableLiveData(listOf(LatLng(epflLat, epflLon))))
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

        CreateItineraryViewModel =
            ItineraryViewModel(itineraryRepository, directionsRepository, userLocationClient)
        itineraryViewModel.setFocusedItinerary(itinerary)
        profileViewModel = ProfileViewModel(profileRepository, imageRepository)


    }

    @Test
    fun dummy() {
        assert(true)
    }

    @Test
    fun testLocation1TextField() {
        composeTestRule.setContent {
            SelectLocation(mapViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithTag(TestTags.FIRST_LOCATION).assertIsDisplayed()

    }
    @Test
    fun testLocation2TextField() {
        composeTestRule.setContent {
            SelectLocation(mapViewModel = mockViewModel)
        }

        composeTestRule.onNodeWithTag(TestTags.SECOND_LOCATION).assertIsDisplayed()
    }
}

/*

@RunWith(AndroidJUnit4::class)
class CreateItineraryMapTest {
    @get:Rule val composeTestRule = createComposeRule()

    @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock private lateinit var createItineraryViewModel: CreateItineraryViewModel

    @Mock private lateinit var profileRepository: ProfileRepository
    @Mock private lateinit var imageRepository: ImageRepository
    @Mock private lateinit var directionsRepository: DirectionsRepository
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
        val epflLocation = Mockito.mock(Location::class.java)
        epflLocation.latitude = epflLat
        epflLocation.longitude = epflLon
        val polylinePoints = MutableLiveData<List<LatLng>>()
        polylinePoints.value = listOf(LatLng(epflLat, epflLon))

        Mockito.`when`(
            directionsRepository.getPolylineWayPoints(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyList(),
                ArgumentMatchers.anyString()))
            .thenReturn(MutableLiveData(listOf(LatLng(epflLat, epflLon))))
        Mockito.`when`(userLocationClient.getLocationUpdates(anyLong()))
            .thenReturn(flow { emit(epflLocation) })
        val itineraryRepository = Mockito.mock(ItineraryRepository::class.java)

        val dummyProfile = Profile("-")
        Mockito.`when`(profileRepository.getProfile(ArgumentMatchers.anyString()))
            .thenReturn(flow { emit(dummyProfile) })
        Mockito.`when`(imageRepository.fetchImage(ArgumentMatchers.anyString()))
            .thenReturn(flow { emit(null) })

        createItineraryViewModel =
            CreateItineraryViewModel(itineraryRepository, directionsRepository, userLocationClient)
        createItineraryViewModel.startNewItinerary(dummyProfile.userUid)

        profileViewModel = ProfileViewModel(profileRepository, imageRepository)
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
        // TODO Imane needs to define the test tag for her composable
    }
}
*/
