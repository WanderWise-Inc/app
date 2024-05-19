package com.github.wanderwise_inc.app.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.LocationsRepository
import com.github.wanderwise_inc.app.model.location.ItineraryLabels
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.utils.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import java.lang.StringBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CreateItineraryViewModelTest() {
  @ExperimentalCoroutinesApi @get:Rule val mainDispatcherRule = MainDispatcherRule()

  @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

  @MockK private lateinit var itineraryRepository: ItineraryRepository

  @MockK private lateinit var directionsRepository: DirectionsRepository

  @MockK private lateinit var locationsRepository: LocationsRepository

  @MockK private lateinit var locatation: Location

  @MockK private lateinit var userLocationClient: UserLocationClient

  private lateinit var createItineraryViewModel: CreateItineraryViewModel

  private val testDispatcher = StandardTestDispatcher()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    locatation = mockk()
    every { locatation.lat } returns 0.0
    every { locatation.long } returns 0.0
    userLocationClient = mockk()
    every { userLocationClient.getLocationUpdates(any()) } returns flow { emit(locatation) }
    Dispatchers.setMain(testDispatcher)
    MockKAnnotations.init(this)
    createItineraryViewModel =
        CreateItineraryViewModel(
            itineraryRepository, directionsRepository, locationsRepository, userLocationClient)
  }

  @Test
  fun `validTitle works as intended`() {
    val maxTitleLength = createItineraryViewModel.getMaxTitleLength()
    val n = (maxTitleLength / 5) + 3
    val sb = StringBuilder()
    for (i in 0 until n) {
      sb.append("title")
    }

    assert(createItineraryViewModel.validTitle("title"))
    assert(!createItineraryViewModel.validTitle(sb.toString()))
  }

  @Test
  fun `invalid title message works as intended`() {
    val maxTitleLength = createItineraryViewModel.getMaxTitleLength()
    assertEquals(
        createItineraryViewModel.invalidTitleMessage(),
        "title field must be shorter than ${maxTitleLength} characters")
  }

  @Test
  fun `calling notSetValues on a freshly created Itinerary returns every ItineraryLabel`() {
    createItineraryViewModel.startNewItinerary("Me")

    val notSetValues = createItineraryViewModel.notSetValues()

    assert(notSetValues.contains(ItineraryLabels.TITLE))
    assert(notSetValues.contains(ItineraryLabels.DESCRIPTION))
    assert(notSetValues.contains(ItineraryLabels.TAGS))
    assert(notSetValues.contains(ItineraryLabels.LOCATIONS))
    assert(notSetValues.contains(ItineraryLabels.PRICE))
    assert(notSetValues.contains(ItineraryLabels.TIME))
  }

  @Test
  fun `calling notSetValues on a fully set Itinerary returns an empty list`() {
    createItineraryViewModel.startNewItinerary("Me")

    createItineraryViewModel.setNewItineraryTitle("title")
    createItineraryViewModel.setNewItineraryDescription("description")
    createItineraryViewModel.getNewItinerary()?.addTag(ItineraryTags.ADVENTURE)
    createItineraryViewModel
        .getNewItinerary()
        ?.addLocation(Location(15.0, -10.0))
        ?.addLocation(Location(20.0, -10.0))
    createItineraryViewModel.getNewItinerary()?.price(20f)
    createItineraryViewModel.getNewItinerary()?.time(4)

    assert(createItineraryViewModel.notSetValues().isEmpty())
  }

  @Test
  fun `startLocationTracking initializes locationJob and adds locations to the builder`() =
      runTest {
        createItineraryViewModel.startNewItinerary("ME")
        assert(createItineraryViewModel.getNewItinerary() != null)
        assert(createItineraryViewModel.getNewItinerary()!!.build().locations.isEmpty())

        val intervalMillis = 1000L
        createItineraryViewModel.startLocationTracking(intervalMillis, {})

        // delay for some time because of coroutines...
        delay(5000L)
        assertNotNull(createItineraryViewModel.locationJob)
        verify { userLocationClient.getLocationUpdates(intervalMillis) }
        assert(createItineraryViewModel.getNewItinerary()!!.build().locations.isNotEmpty())
      }

  @Test
  fun `stopLocationTracking cancels locationJob`() = runTest {
    createItineraryViewModel.startLocationTracking(1000L, {})
    createItineraryViewModel.stopLocationTracking()
    delay(5000L)
    assertNotNull(createItineraryViewModel.locationJob)
    assert(createItineraryViewModel.locationJob!!.isCancelled)
  }

  @Test
  fun `test onCleared`() {
    createItineraryViewModel.onCleared()

    // Verify that the coroutineScope was cancelled
    assert(createItineraryViewModel.coroutineScope.isActive.not())
  }
}
