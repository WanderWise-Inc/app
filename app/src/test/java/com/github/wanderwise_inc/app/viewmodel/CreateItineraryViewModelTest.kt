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
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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

  @MockK private lateinit var userLocationClient: UserLocationClient

  private lateinit var createItineraryViewModel: CreateItineraryViewModel

  private val testDispatcher = StandardTestDispatcher()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)
    MockKAnnotations.init(this)
    createItineraryViewModel =
        CreateItineraryViewModel(itineraryRepository, directionsRepository, locationsRepository, userLocationClient)
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
}
