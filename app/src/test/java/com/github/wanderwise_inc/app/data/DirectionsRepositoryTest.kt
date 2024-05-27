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

class DirectionsRepositoryTest {
  @get:Rule var rule: TestRule = InstantTaskExecutorRule()

  private lateinit var server: MockWebServer
  private lateinit var directionsApi: DirectionsApiService
  private lateinit var directionsRepository: DirectionsRepository

  private val query =
      listOf("37.41528948098993, -122.09922440350056", "37.40438128839703, -122.07692451775073")
  // google directions api response to above query
  private val directions =
      DirectionsResponseBody(
          routes =
              listOf(
                  DirectionsResponseBody.Route(
                      legs =
                          listOf(
                              DirectionsResponseBody.Route.Leg(
                                  steps =
                                      listOf(
                                          DirectionsResponseBody.Route.Leg.Step(
                                              startLocation =
                                                  DirectionsResponseBody.Route.Leg.RespLocation(
                                                      lat = 37.41528479999999, lng = -122.0991067),
                                              endLocation =
                                                  DirectionsResponseBody.Route.Leg.RespLocation(
                                                      lat = 37.41500690000001, lng = -122.0991245)),
                                          DirectionsResponseBody.Route.Leg.Step(
                                              startLocation =
                                                  DirectionsResponseBody.Route.Leg.RespLocation(
                                                      lat = 37.41500690000001, lng = -122.0991245),
                                              endLocation =
                                                  DirectionsResponseBody.Route.Leg.RespLocation(
                                                      lat = 37.4146274, lng = -122.0925773)),
                                          DirectionsResponseBody.Route.Leg.Step(
                                              startLocation =
                                                  DirectionsResponseBody.Route.Leg.RespLocation(
                                                      lat = 37.4146274, lng = -122.0925773),
                                              endLocation =
                                                  DirectionsResponseBody.Route.Leg.RespLocation(
                                                      lat = 37.4114544, lng = -122.0924344)),
                                          DirectionsResponseBody.Route.Leg.Step(
                                              startLocation =
                                                  DirectionsResponseBody.Route.Leg.RespLocation(
                                                      lat = 37.4114544, lng = -122.0924344),
                                              endLocation =
                                                  DirectionsResponseBody.Route.Leg.RespLocation(
                                                      lat = 37.4058869, lng = -122.0778857)),
                                          DirectionsResponseBody.Route.Leg.Step(
                                              startLocation =
                                                  DirectionsResponseBody.Route.Leg.RespLocation(
                                                      lat = 37.4058869, lng = -122.0778857),
                                              endLocation =
                                                  DirectionsResponseBody.Route.Leg.RespLocation(
                                                      lat = 37.4058452, lng = -122.0779829)),
                                          DirectionsResponseBody.Route.Leg.Step(
                                              startLocation =
                                                  DirectionsResponseBody.Route.Leg.RespLocation(
                                                      lat = 37.4058452, lng = -122.0779829),
                                              endLocation =
                                                  DirectionsResponseBody.Route.Leg.RespLocation(
                                                      lat = 37.4046563, lng = -122.0779898)),
                                          DirectionsResponseBody.Route.Leg.Step(
                                              startLocation =
                                                  DirectionsResponseBody.Route.Leg.RespLocation(
                                                      lat = 37.4046563, lng = -122.0779898),
                                              endLocation =
                                                  DirectionsResponseBody.Route.Leg.RespLocation(
                                                      lat = 37.4041406, lng = -122.0769676)),
                                          DirectionsResponseBody.Route.Leg.Step(
                                              startLocation =
                                                  DirectionsResponseBody.Route.Leg.RespLocation(
                                                      lat = 37.4041406, lng = -122.0769676),
                                              endLocation =
                                                  DirectionsResponseBody.Route.Leg.RespLocation(
                                                      lat = 37.4043308, lng = -122.0768208))))))))
  private val response: String = GsonBuilder().create().toJson(directions)
  private val expectedLatLngList =
      listOf(
          LatLng(37.41500690000001, -122.0991245),
          LatLng(37.4146274, -122.0925773),
          LatLng(37.4114544, -122.0924344),
          LatLng(37.4058869, -122.0778857),
          LatLng(37.4058452, -122.0779829),
          LatLng(37.4046563, -122.0779898),
          LatLng(37.4041406, -122.0769676),
          LatLng(37.4043308, -122.0768208))

  private val key = BuildConfig.MAPS_API_KEY

  @Before
  fun setup() {
    server = MockWebServer()
    server.start()
    val serverUrl = server.url("/")
    directionsApi = DirectionsApiServiceFactory.createDirectionsApiService(serverUrl)
    directionsRepository = DirectionsRepositoryImpl(directionsApi)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test(expected = ConditionTimeoutException::class)
  fun `directionsRepository test on failure`() = runTest {
    // no response created should result in a failure
    var actual: List<LatLng>? = null
    backgroundScope.launch {
      directionsRepository
          .getPolylineWayPoints(
              origin = query.first(),
              destination = query.last(),
              waypoints = query.drop(1).dropLast(1),
              apiKey = key)
          .observeForever { actual = it }
    }
    testScheduler.advanceTimeBy(20000L)
    await untilNotNull
        {
          actual
        } // this should throw a ConditionTimeoutException as actual will always be null
    assertNull(actual)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test(expected = ConditionTimeoutException::class)
  fun `directionsRepository test on unsuccessful response`() = runTest {
    server.enqueue(
        MockResponse().setResponseCode(400) // unsuccessful response
        )

    var actual: List<LatLng>? = null
    backgroundScope.launch {
      directionsRepository
          .getPolylineWayPoints(
              origin = query.first(),
              destination = query.last(),
              waypoints = query.drop(1).dropLast(1),
              apiKey = key)
          .observeForever { actual = it }
    }
    testScheduler.advanceTimeBy(20000L)
    await untilNotNull
        {
          actual
        } // this should throw a ConditionTimeoutException as actual will always be null
    assertNull(actual)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `directionsRepository test on successful response`() = runTest {
    server.enqueue(
        MockResponse()
            .setResponseCode(200) // successful response
            .setHeader("content-type", "application/json; charset=UTF-8")
            .setHeader("content-length", response.length)
            .setBody(response))

    var actual: List<LatLng>? = null
    backgroundScope.launch {
      directionsRepository
          .getPolylineWayPoints(
              origin = query.first(),
              destination = query.last(),
              waypoints = query.drop(1).dropLast(1),
              apiKey = key)
          .observeForever { actual = it }
    }
    testScheduler.advanceTimeBy(20000L)
    await untilNotNull { actual }
    assertEquals(expectedLatLngList, actual)
  }

  @After
  fun teardown() {
    server.shutdown()
  }
}
