package com.github.wanderwise_inc.app.viewmodel

import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryPreferences
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.network.DirectionsApiService
import com.github.wanderwise_inc.app.network.DirectionsResponseBody
import junit.framework.TestCase.assertEquals
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.Request
import okio.Timeout
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/** @brief test class for mapview model */
class MapViewModelTestOld {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var userLocationClient: UserLocationClient

    private lateinit var itineraryRepository: ItineraryRepository
    @Mock
    private lateinit var mockApiService: DirectionsApiService
    @Mock
    private lateinit var directionsRepository: DirectionsRepository
    private lateinit var mapViewModel: MapViewModel

    // helper function for generating random latitude and longitude...
    private fun randomLat(): Double = Random.nextDouble(-90.0, 90.0)

    private fun randomLon(): Double = Random.nextDouble(-180.0, 180.0)

    // helper function for generating random locations
    private fun randomLocations(size: Int): List<Location> {
        // helper function for generating random locations
        val locations = mutableListOf<Location>()
        for (i in 0 until size) locations.add(Location(randomLat(), randomLon()))
        return locations.toList()
    }

    // some public itineraries
    private val ethanItinerary =
        Itinerary(
            userUid = "Ethan",
            locations = randomLocations(5),
            title = "Ethan's Itinerary",
            tags = listOf(ItineraryTags.BUDGET, ItineraryTags.SOCIAL),
            description = null,
            visible = true
        )
    private val yofiItinerary =
        Itinerary(
            userUid = "Yofthahe",
            locations = randomLocations(10),
            title = "Yofi's Itinerary",
            tags = listOf(ItineraryTags.CULTURAL, ItineraryTags.SOCIAL, ItineraryTags.WILDLIFE),
            description = null,
            visible = true
        )
    private val thomasItinerary =
        Itinerary(
            userUid = "Thomas",
            locations = randomLocations(3),
            title = "Thomas' Itinerary",
            tags = listOf(ItineraryTags.ACTIVE),
            description = null,
            visible = true
        )

    private val mockLocations = randomLocations(4)
    private val mockResponse =
        DirectionsResponseBody(
            listOf(
                DirectionsResponseBody.Route(
                    listOf(
                        DirectionsResponseBody.Route.Leg(
                            listOf(
                                DirectionsResponseBody.Route.Leg.Step(
                                    DirectionsResponseBody.Route.Leg.RespLocation(
                                        mockLocations[0].lat, mockLocations[0].long
                                    ),
                                    DirectionsResponseBody.Route.Leg.RespLocation(
                                        mockLocations[1].lat, mockLocations[1].long
                                    )
                                ),
                                DirectionsResponseBody.Route.Leg.Step(
                                    DirectionsResponseBody.Route.Leg.RespLocation(
                                        mockLocations[2].lat, mockLocations[2].long
                                    ),
                                    DirectionsResponseBody.Route.Leg.RespLocation(
                                        mockLocations[3].lat, mockLocations[3].long
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)

        mockApiService = mock(DirectionsApiService::class.java)

        val mockCall =
            object : Call<DirectionsResponseBody> {
                override fun enqueue(callback: Callback<DirectionsResponseBody>) {
                    // Simulate a successful response
                    callback.onResponse(this, Response.success(mockResponse))
                }

                // Other overridden methods can be added as necessary for your test
                override fun isExecuted(): Boolean = false

                override fun clone(): Call<DirectionsResponseBody> = this

                override fun execute(): Response<DirectionsResponseBody> {
                    throw UnsupportedOperationException("execute() is not supported for async calls")
                }

                override fun isCanceled(): Boolean = false

                override fun request(): Request {
                    TODO("shouldn't be needed for mock")
                }

                override fun timeout(): Timeout {
                    throw UnsupportedOperationException("timeout() is not supported for async calls")
                }

                override fun cancel() {
                    TODO("shouldn't be needed for mock")
                }
            }
        // Mock the directionsApiService so that we don't get billed by Google
        `when`(
            mockApiService.getPolylineWayPoints(
                origin = anyString(),
                destination = anyString(),
                waypoints = anyList(),
                key = anyString()
            )
        )
            .thenReturn(mockCall)

        // directionsRepository = DirectionsRepository(mockApiService)
        directionsRepository = mock(DirectionsRepository::class.java)

        itineraryRepository = ItineraryRepositoryTestImpl()
        mapViewModel =
            MapViewModel(
                itineraryRepository = itineraryRepository,
                directionsRepository = directionsRepository,
                userLocationClient = userLocationClient
            )
    }

    @Test
    fun `itinerary add and retrieve`() = runTest {
        // insert in shuffled order for next test that tests sorting algorithm
        mapViewModel.setItinerary(yofiItinerary)
        mapViewModel.setItinerary(thomasItinerary)
        mapViewModel.setItinerary(ethanItinerary)

        val retrievedItineraries = mapViewModel.getAllPublicItineraries().first().toSet()
        val expected = setOf(ethanItinerary, yofiItinerary, thomasItinerary)

        assertEquals(expected, retrievedItineraries)
    }

    @Test
    fun `itinerary filtering from preferences outputs correct elements 1`() = runTest {
        mapViewModel.setItinerary(yofiItinerary)
        mapViewModel.setItinerary(thomasItinerary)
        mapViewModel.setItinerary(ethanItinerary)

        val testPreferences =
            ItineraryPreferences(
                tags = listOf(ItineraryTags.BUDGET, ItineraryTags.SOCIAL),
            )

        val retrievedItineraries =
            mapViewModel.getItinerariesFromPreferences(testPreferences).first()
        assert(retrievedItineraries.contains(ethanItinerary))
        assert(retrievedItineraries.contains(yofiItinerary))
        assert(!retrievedItineraries.contains(thomasItinerary))
    }

    @Test
    fun `itinerary filtering from preferences outputs correct elements 2`() = runTest {
        mapViewModel.setItinerary(yofiItinerary)
        mapViewModel.setItinerary(thomasItinerary)
        mapViewModel.setItinerary(ethanItinerary)

        val testPreferences =
            ItineraryPreferences(
                tags = listOf(ItineraryTags.ACTIVE),
            )

        val retrievedItineraries =
            mapViewModel.getItinerariesFromPreferences(testPreferences).first()
        assert(!retrievedItineraries.contains(ethanItinerary))
        assert(!retrievedItineraries.contains(yofiItinerary))
        assert(retrievedItineraries.contains(thomasItinerary))
    }

    @Test
    fun `itinerary filtering from preferences outputs correct elements 3`() = runTest {
        mapViewModel.setItinerary(yofiItinerary)
        mapViewModel.setItinerary(thomasItinerary)
        mapViewModel.setItinerary(ethanItinerary)

        val testPreferences =
            ItineraryPreferences(
                tags = listOf(ItineraryTags.FOODIE),
            )

        val retrievedItineraries =
            mapViewModel.getItinerariesFromPreferences(testPreferences).first()
        assert(!retrievedItineraries.contains(ethanItinerary))
        assert(!retrievedItineraries.contains(yofiItinerary))
        assert(!retrievedItineraries.contains(thomasItinerary))
    }

    @Test
    fun `itinerary sorting from preferences outputs correct order`() = runTest {
        mapViewModel.setItinerary(yofiItinerary)
        mapViewModel.setItinerary(thomasItinerary)
        mapViewModel.setItinerary(ethanItinerary)

        val testPreferences =
            ItineraryPreferences(
                tags = listOf(ItineraryTags.BUDGET, ItineraryTags.SOCIAL),
            )

        val retrievedItineraries =
            mapViewModel.getItinerariesFromPreferences(testPreferences).first()

        assertEquals(
            listOf(ethanItinerary, yofiItinerary),
            mapViewModel.sortItinerariesFromPreferences(retrievedItineraries, testPreferences)
        )

        assertNotEquals(
            listOf(yofiItinerary, ethanItinerary),
            mapViewModel.sortItinerariesFromPreferences(retrievedItineraries, testPreferences)
        )
    }

    @Test
    fun `getting itinerary from username works correctly`() = runTest {
        mapViewModel.setItinerary(ethanItinerary)
        mapViewModel.setItinerary(yofiItinerary)
        mapViewModel.setItinerary(thomasItinerary)

        val firstQuery = mapViewModel.getUserItineraries("Ethan").first()
        assert(firstQuery.contains(ethanItinerary))
        assert(!firstQuery.contains(yofiItinerary))
        assert(!firstQuery.contains(thomasItinerary))

        val secondQuery = mapViewModel.getUserItineraries("Thomas").first()
        assert(!secondQuery.contains(ethanItinerary))
        assert(!secondQuery.contains(yofiItinerary))
        assert(secondQuery.contains(thomasItinerary))

        val thirdQuery = mapViewModel.getUserItineraries("Oscar").first()
        assertEquals(listOf<Itinerary>(), thirdQuery)
    }

    @Test
    fun `deleting an itinerary works correctly`() = runTest {
        mapViewModel.setItinerary(ethanItinerary)
        mapViewModel.setItinerary(yofiItinerary)
        mapViewModel.setItinerary(thomasItinerary)

        mapViewModel.deleteItinerary(yofiItinerary)

        val firstQuery = mapViewModel.getAllPublicItineraries().first()

        assert(!firstQuery.contains(yofiItinerary))
        assert(firstQuery.contains(ethanItinerary))
        assert(firstQuery.contains(thomasItinerary))

        mapViewModel.deleteItinerary(ethanItinerary)
        mapViewModel.deleteItinerary(thomasItinerary)

        val secondQuery = mapViewModel.getAllPublicItineraries().first()
        assertEquals(listOf<Itinerary>(), secondQuery)
    }

    /*
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getPolyLineWaypoints() should return a correctly parsed list of Locations`() = runTest {
      val mockCall = mock<Call<DirectionsResponseBody>>()

      val placeholderItinerary =
        Itinerary(
          userUid = "Ethan",
          locations = randomLocations(2),
          title = "Ethan's Itinerary",
          tags = listOf(ItineraryTags.BUDGET, ItineraryTags.SOCIAL),
          description = null,
          visible = true)
      val originLatlng = placeholderItinerary.locations[0]
      val destLatlng = placeholderItinerary.locations[1]

      val originEncoded = "${originLatlng.lat},${originLatlng.long}"
      val destinationEncoded = "${destLatlng.lat},${destLatlng.long}"

      `when`(directionsRepository.getPolylineWayPoints(originEncoded, destinationEncoded, apiKey = BuildConfig.MAPS_API_KEY))
        .thenReturn(MutableLiveData(listOf(LatLng(0.0, 0.0))))

      mapViewModel.fetchPolylineLocations(placeholderItinerary)
      advanceUntilIdle()

      assertEquals(listOf(LatLng(0.0, 0.0)), mapViewModel.polylinePointsLiveData.value)
    }
     */

    @Test
    fun `getUserLocation returns a flow containing the user location`() = runBlocking {
        val epflLocation = Mockito.mock(android.location.Location::class.java)
        epflLocation.latitude = randomLat()
        epflLocation.longitude = randomLon()

        `when`(userLocationClient.getLocationUpdates(anyLong())).thenReturn(flow { emit(epflLocation) })

        val userLocationFlow = mapViewModel.getUserLocation()
        val emittedLocation = userLocationFlow.first()

        assertEquals(epflLocation, emittedLocation)
    }
}
