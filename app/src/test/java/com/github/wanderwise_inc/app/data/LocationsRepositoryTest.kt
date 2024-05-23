package com.github.wanderwise_inc.app.data

import com.github.wanderwise_inc.app.BuildConfig
import com.github.wanderwise_inc.app.network.LocationsApiService
import com.github.wanderwise_inc.app.network.LocationsApiServiceFactory
import com.github.wanderwise_inc.app.network.Place
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.mockito.kotlin.mock
import retrofit2.Call

class LocationsRepositoryImplTest {
    private lateinit var server: MockWebServer
    private lateinit var locationsApi: LocationsApiService

    // geocode response to query "Empire State Building"
    private val response: String =
        "[{\"place_id\":319622561,\"licence\":\"Data © OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright\",\"osm_type\":\"way\",\"osm_id\":34633854,\"boundingbox\":[\"40.7479255\",\"40.7489585\",\"-73.9865012\",\"-73.9848166\"],\"lat\":\"40.74844205\",\"lon\":\"-73.98565890160751\",\"display_name\":\"Empire State Building, 350, 5th Avenue, Manhattan Community Board 5, Manhattan, New York County, New York, 10118, United States\",\"class\":\"tourism\",\"type\":\"attraction\",\"importance\":0.8515868466874569},{\"place_id\":332355935,\"licence\":\"Data © OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright\",\"osm_type\":\"way\",\"osm_id\":1105667894,\"boundingbox\":[\"41.2571487\",\"41.2574868\",\"-95.941605\",\"-95.9412738\"],\"lat\":\"41.257318999999995\",\"lon\":\"-95.941361792652\",\"display_name\":\"Empire State Building, 300, South 19th Street, Omaha, Douglas County, Nebraska, 68102, United States\",\"class\":\"building\",\"type\":\"yes\",\"importance\":0.30001},{\"place_id\":290599293,\"licence\":\"Data © OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright\",\"osm_type\":\"way\",\"osm_id\":244111841,\"boundingbox\":[\"47.6575504\",\"47.6577876\",\"-117.4256002\",\"-117.4251895\"],\"lat\":\"47.6576574\",\"lon\":\"-117.42539477065071\",\"display_name\":\"Empire State Building, West Riverside Avenue, Riverside, Spokane, Spokane County, Washington, 99201, United States\",\"class\":\"building\",\"type\":\"yes\",\"importance\":0.30001},{\"place_id\":159314734,\"licence\":\"Data © OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright\",\"osm_type\":\"node\",\"osm_id\":5552887205,\"boundingbox\":[\"52.2956835\",\"52.2957835\",\"16.7553074\",\"16.7554074\"],\"lat\":\"52.2957335\",\"lon\":\"16.7553574\",\"display_name\":\"Empire State Building, Poznańska, Trzebaw, gmina Stęszew, Poznań County, Greater Poland Voivodeship, Poland\",\"class\":\"tourism\",\"type\":\"artwork\",\"importance\":0.30001},{\"place_id\":340867917,\"licence\":\"Data © OpenStreetMap contributors, ODbL 1.0. https://osm.org/copyright\",\"osm_type\":\"way\",\"osm_id\":912248052,\"boundingbox\":[\"26.1887675\",\"26.189116\",\"91.694484\",\"91.6948697\"],\"lat\":\"26.1889454\",\"lon\":\"91.69467468588321\",\"display_name\":\"Empire state building, Footpath surrounding sports area, North Guwahati (Pt), Kamrup Metropolitan District, Assam, 781039, India\",\"class\":\"building\",\"type\":\"yes\",\"importance\":0.30001}]"
    private val mockServerUrl = "testing/url"
    private val key = BuildConfig.GEOCODE_API_KEY

    @Before
    fun setup() {
        server = MockWebServer()
        server.enqueue(MockResponse().setBody(response))
        server.start()
        server.url(mockServerUrl)
        locationsApi = LocationsApiServiceFactory.createLocationsApiService(mockServerUrl)
    }

    @Test
    fun locationsRepositoryTest() {
        val response = locationsApi.getLocation("Empire State Building", key)
        val gson: Gson = GsonBuilder().create()
    }

    @After
    fun teardown() {
        server.shutdown()
    }
}