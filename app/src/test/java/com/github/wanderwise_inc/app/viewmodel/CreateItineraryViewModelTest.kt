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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CreateItineraryViewModelTest() {
  @ExperimentalCoroutinesApi @get:Rule val mainDispatcherRule = MainDispatcherRule()

  @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

  @MockK private lateinit var itineraryRepository: ItineraryRepository

  @MockK private lateinit var directionsRepository: DirectionsRepository

  @MockK private lateinit var locationsRepository: LocationsRepository

  @MockK private lateinit var mockAndroidLocation: android.location.Location

  @MockK private lateinit var userLocationClient: UserLocationClient

  private lateinit var createItineraryViewModel: CreateItineraryViewModel

  private val testDispatcher = StandardTestDispatcher()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    mockAndroidLocation = mockk()
    every { mockAndroidLocation.latitude } returns 0.0
    every { mockAndroidLocation.longitude } returns 0.0
    userLocationClient = mockk()
    every { userLocationClient.getLocationUpdates(any()) } returns
        flow { emit(mockAndroidLocation) }
    Dispatchers.setMain(testDispatcher)
    MockKAnnotations.init(this)
    createItineraryViewModel =
        CreateItineraryViewModel(
            itineraryRepository, directionsRepository, locationsRepository, userLocationClient)
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
        createItineraryViewModel.startLocationTracking(intervalMillis)

        // delay for some time because of coroutines...
        delay(5000L)
        assertNotNull(createItineraryViewModel.locationJob)
        verify { userLocationClient.getLocationUpdates(intervalMillis) }
        assert(createItineraryViewModel.getNewItinerary()!!.build().locations.isNotEmpty())
      }

  @Test
  fun `stopLocationTracking cancels locationJob`() = runTest {
    createItineraryViewModel.startLocationTracking(1000L)
    createItineraryViewModel.stopLocationTracking()
    delay(5000L)
    assertNotNull(createItineraryViewModel.locationJob)
    assert(createItineraryViewModel.locationJob!!.isCancelled)
  }

  @Test
  fun `test onCleared`() {
    createItineraryViewModel.onCleared()

    // Verify that the coroutineScope was cancelled
    assertTrue(createItineraryViewModel.coroutineScope.isActive.not())
  }
}
