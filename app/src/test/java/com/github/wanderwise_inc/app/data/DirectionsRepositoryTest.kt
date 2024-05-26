package com.github.wanderwise_inc.app.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.wanderwise_inc.app.BuildConfig
import com.github.wanderwise_inc.app.network.DirectionsApiService
import com.github.wanderwise_inc.app.network.DirectionsApiServiceFactory
import com.github.wanderwise_inc.app.network.DirectionsResponseBody
import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import okhttp3.mockwebserver.MockWebServer
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilNotNull
import org.junit.After
import org.junit.Rule
import org.junit.rules.TestRule

class DirectionsRepositoryTest {
    @get:Rule var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var server: MockWebServer
    private lateinit var directionsApi: DirectionsApiService
    private lateinit var directionsRepository: DirectionsRepository

    private val query: String = ""
    // google directions api response to above query
    private val directions: String = ""
    private val response: String = GsonBuilder().create().toJson(directions)
    private val expectedLatLngList = listOf(
        LatLng(0.0, 0.0)
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
        directionsApi = DirectionsApiServiceFactory.createDirectionsApiService(serverUrl)
        directionsRepository = DirectionsRepositoryImpl(directionsApi)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun locationsRepositoryTest() = runTest {
        var actual: List<LatLng>? = null
        backgroundScope.launch {
            directionsRepository.getPolylineWayPoints(
                origin = "",
                destination = "",
                waypoints = listOf(),
                apiKey = key
            ).observeForever {
                    actual = it
                }
        }
        testScheduler.advanceTimeBy(20000L)
        val request = server.takeRequest()
        await untilNotNull { actual }
        println(request)
        println(actual)
        assertEquals(expectedLatLngList, actual)
    }

    @After
    fun teardown() {
        server.shutdown()
    }
}
