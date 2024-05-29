package com.github.wanderwise_inc.app.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.wanderwise_inc.app.BuildConfig
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.network.LocationsApiService
import com.github.wanderwise_inc.app.network.LocationsApiServiceFactory
import com.github.wanderwise_inc.app.network.Place
import com.google.gson.GsonBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.awaitility.core.ConditionTimeoutException
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilNotNull
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class LocationsRepositoryTest {
  @get:Rule var rule: TestRule = InstantTaskExecutorRule()

  private lateinit var server: MockWebServer
  private lateinit var locationsApi: LocationsApiService
  private lateinit var locationsRepository: LocationsRepositoryImpl

  // geocode response to query "Empire State Building"
  private val places =
      listOf(
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
              0.4754548f))
  private val response: String = GsonBuilder().create().toJson(places)
  private val expectedLocations =
      listOf(
          Location(
              lat = 46.597822,
              long = 6.540398,
              title = "Penthaz",
              address = "District du Gros-de-Vaud, Vaud, 1303, Switzerland",
              googleRating = 2.377274f))

  private val key = BuildConfig.GEOCODE_API_KEY

  @Before
  fun setup() {
    server = MockWebServer()
    server.start()
    val serverUrl = server.url("/")
    locationsApi = LocationsApiServiceFactory.createLocationsApiService(serverUrl)
    locationsRepository = LocationsRepositoryImpl(locationsApi)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test(expected = ConditionTimeoutException::class)
  fun `locationsRepository test on failure`() = runTest {
    var actual: List<Location>? = null
    backgroundScope.launch {
      locationsRepository.getPlaces("Penthaz", 1, key).observeForever { actual = it }
    }
    testScheduler.advanceTimeBy(20000L)
    await untilNotNull
        {
          actual
        } // this should throw a ConditionTimeoutException as actual will never be not null
    assertNull(actual)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test(expected = ConditionTimeoutException::class)
  fun `locationsRepository test on unsuccessful response`() = runTest {
    server.enqueue( // enqueue unsuccessful response
        MockResponse().setResponseCode(400))

    var actual: List<Location>? = null
    backgroundScope.launch {
      locationsRepository.getPlaces("Penthaz", 1, key).observeForever { actual = it }
    }
    testScheduler.advanceTimeBy(20000L)
    await untilNotNull
        {
          actual
        } // this should throw a ConditionTimeoutException as actual will never be not null
    assertNull(actual)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `locationsRepository test on successful response`() = runTest {
    server.enqueue( // enqueue successful response
        MockResponse()
            .setResponseCode(200) // Successful response code
            .setHeader("content-type", "application/json; charset=UTF-8")
            .setHeader("content-length", response.length)
            .setBody(response))

    var actual: List<Location>? = null
    backgroundScope.launch {
      locationsRepository.getPlaces("Penthaz", 1, key).observeForever { actual = it }
    }

    testScheduler.advanceTimeBy(20000L)
    await untilNotNull { actual }
    assertEquals(expectedLocations, actual)
  }

  @After
  fun teardown() {
    server.shutdown()
  }
}
