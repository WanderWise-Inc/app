package com.github.wanderwise_inc.app.data

import com.github.wanderwise_inc.app.BuildConfig
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.network.LocationsApiService
import com.github.wanderwise_inc.app.network.LocationsApiServiceFactory
import com.github.wanderwise_inc.app.network.Place
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONObject
import org.junit.After
import org.mockito.kotlin.mock
import retrofit2.Call

class LocationsRepositoryImplTest {
    private lateinit var server: MockWebServer
    private lateinit var locationsApi: LocationsApiService
    private lateinit var locationsRepository: LocationsRepositoryImpl

    // geocode response to query "Empire State Building"
    private val places = listOf(
        Place(
            78934259,
            "Data © OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright",
            "relation",
            1685097,
            listOf("46.5879671", "46.6128698", "6.5272448", "6.5540055"),
            46.597822,
            6.540398,
            "Penthaz, District du Gros-de-Vaud, Vaud, 1303, Switzerland",
            "boundary",
            "administrative",
            0.4754548f
        )
    )
    private val response: String = GsonBuilder().create().toJson(places)
    private val expectedLocations = listOf(
        Location(
            lat = 46.597822,
            long = 6.540398,
            title = "Penthaz",
            address = "District du Gros-de-Vaud, Vaud, 1303, Switzerland",
            googleRating = 2.377274f
        )
    /*Location(
            lat = 40.74844205,
            long = -73.98565890160751,
            title = "Empire State Building",
            address = "350, 5th Avenue, Manhattan Community Board 5, Manhattan, New York County, New York, 10118, United States",
            googleRating = 4.2579342334372845f
        ),
        Location(
            lat = 41.257318999999995,
            long = -95.941361792652,
            title = "Empire State Building",
            address = "300, South 19th Street, Omaha, Douglas County, Nebraska, 68102, United States",
            googleRating = 1.50005f
        ),
        Location(
            lat = 47.6576574,
            long = -117.42539477065071,
            title = "Empire State Building",
            address = "West Riverside Avenue, Riverside, Spokane, Spokane County, Washington, 99201, United States",
            googleRating = 1.50005f
        ),
        Location(
            lat = 52.2957335,
            long = 16.7553574,
            title = "Empire State Building",
            address = "Poznańska, Trzebaw, gmina Stęszew, Poznań County, Greater Poland Voivodeship, Poland",
            googleRating = 1.50005f
        ),
        Location(
            lat = 26.1889454,
            long = 91.69467468588321,
            title = "Empire state building",
            address = "Footpath surrounding sports area, North Guwahati (Pt), Kamrup Metropolitan District, Assam, 781039, India",
            googleRating = 1.50005f
        )*/
    )

    private val key = BuildConfig.GEOCODE_API_KEY

    @Before
    fun setup() {
        server = MockWebServer()
        server.enqueue(
            MockResponse()
                .setHeader("content-type", "application/json; charset=UTF-8")
                .setHeader("content-length", response.length)
                .setBody(response))
        server.start()
        val serverUrl = server.url("/")
        locationsApi = LocationsApiServiceFactory.createLocationsApiService(serverUrl)
        locationsRepository = LocationsRepositoryImpl(locationsApi)
    }

    @Test
    fun locationsRepositoryTest() {
        val actual = locationsRepository.getPlaces("Empire State Building", 5, key)
        val request = server.takeRequest()
        assertEquals("GET", request.method)
        println(request.requestUrl)
        //assertEquals("", request.)
        assertNotNull(actual.value)
        assertEquals(expectedLocations, actual.value)
    }

    @After
    fun teardown() {
        server.shutdown()
    }
}