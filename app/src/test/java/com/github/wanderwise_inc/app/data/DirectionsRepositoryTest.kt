package com.github.wanderwise_inc.app.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.github.wanderwise_inc.app.BuildConfig
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.network.DirectionsResponseBody
import com.github.wanderwise_inc.app.network.LocationsApiService
import com.github.wanderwise_inc.app.network.LocationsApiServiceFactory
import com.github.wanderwise_inc.app.network.Place
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.LocationClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import okhttp3.internal.wait
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilNotNull
import org.json.JSONObject
import org.junit.After
import org.junit.Rule
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito.anyList
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import retrofit2.Call
import java.util.concurrent.CountDownLatch


class DirectionsRepositoryTest {
    @get:Rule var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var server: MockWebServer
    private lateinit var locationsApi: LocationsApiService
    private lateinit var locationsRepository: LocationsRepositoryImpl

    private val query: String = ""
    // google directions api response to above query
    private val directions: String = ""
    private val response: String = GsonBuilder().create().toJson(directions)
    private val expectedDirections = DirectionsResponseBody(
        listOf(
            DirectionsResponseBody.Route()
        )
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun locationsRepositoryTest() = runTest {
        var actual: List<Location>? = null
        backgroundScope.launch {
            locationsRepository.getPlaces("Penthaz", 1, key).observeForever {
                actual = it
            }
        }
        testScheduler.advanceTimeBy(20000L)
        val request = server.takeRequest()
        await untilNotNull { actual }
        println(request)
        println(actual)
        assertEquals(expectedLocations, actual)
    }

    @After
    fun teardown() {
        server.shutdown()
    }
}
