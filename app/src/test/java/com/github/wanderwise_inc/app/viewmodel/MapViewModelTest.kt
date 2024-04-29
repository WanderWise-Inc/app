package com.github.wanderwise_inc.app.viewmodel

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import app.cash.turbine.test
import com.github.wanderwise_inc.app.DEFAULT_USER_UID
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryPreferences
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.utils.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MapViewModelTest {

  @ExperimentalCoroutinesApi @get:Rule val mainDispatcherRule = MainDispatcherRule()

  @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

  @MockK private lateinit var itineraryRepository: ItineraryRepository

  @MockK private lateinit var directionsRepository: DirectionsRepository

  @MockK private lateinit var userLocationClient: UserLocationClient

  private lateinit var mapViewModel: MapViewModel

  private val testDispatcher = StandardTestDispatcher()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)
    MockKAnnotations.init(this)
    mapViewModel = MapViewModel(itineraryRepository, directionsRepository, userLocationClient)
  }

  @Test
  fun getAllPublicItineraries() = runBlocking {
    every { itineraryRepository.getPublicItineraries() } returns
        flow { emit(listOf(FakeItinerary.TOKYO)) }

    val emittedItineraryList = mapViewModel.getAllPublicItineraries().first()
    assertEquals(listOf(FakeItinerary.TOKYO), emittedItineraryList)
  }

  @Test
  fun getUserItineraries() = runBlocking {
    every { itineraryRepository.getUserItineraries(any()) } returns
        flow { emit(listOf(FakeItinerary.TOKYO)) }

    val emittedItineraryList = mapViewModel.getUserItineraries(DEFAULT_USER_UID).first()
    assertEquals(listOf(FakeItinerary.TOKYO), emittedItineraryList)
  }

  @Test
  fun getItineraryLikes() {
    val totalLikes = mapViewModel.getItineraryLikes(listOf(FakeItinerary.TOKYO))
    assertEquals(0, totalLikes)
  }

  @Test
  fun incrementItineraryLikes() {
    val repo = mutableMapOf(FakeItinerary.TOKYO.uid to FakeItinerary.TOKYO)

    every { itineraryRepository.updateItinerary(any(), any()) } answers
        {
          val uid = invocation.args[0] as String
          val itinerary = invocation.args[1] as Itinerary
          repo[uid] = itinerary
        }

    mapViewModel.incrementItineraryLikes(FakeItinerary.TOKYO)
    assertEquals(1, repo[FakeItinerary.TOKYO.uid]!!.numLikes)
  }

  @Test
  fun decrementItineraryLikes() {
    val tokyoLikedItinerary = FakeItinerary.TOKYO
    ++tokyoLikedItinerary.numLikes
    val repo = mutableMapOf(tokyoLikedItinerary.uid to tokyoLikedItinerary)

    every { itineraryRepository.updateItinerary(any(), any()) } answers
        {
          val uid = invocation.args[0] as String
          val itinerary = invocation.args[1] as Itinerary
          repo[uid] = itinerary
        }

    mapViewModel.decrementItineraryLikes(FakeItinerary.TOKYO)
    assertEquals(0, repo[tokyoLikedItinerary.uid]!!.numLikes)
  }

  @Test
  fun getItinerariesFromPreferences() = runBlocking {
    every { itineraryRepository.getItinerariesWithTags(listOf(ItineraryTags.URBAN)) } returns
        flow { emit(listOf(FakeItinerary.TOKYO)) }

    val emittedItineraryList =
        mapViewModel
            .getItinerariesFromPreferences(ItineraryPreferences(listOf(ItineraryTags.URBAN)))
            .first()
    assertEquals(listOf(FakeItinerary.TOKYO), emittedItineraryList)
  }

  @Test
  fun sortItinerariesFromPreferences() {
    val tokyo = mockk<Itinerary>()
    val switzerland = mockk<Itinerary>()
    val itineraries = listOf(switzerland, tokyo)
    val pref = ItineraryPreferences(listOf(ItineraryTags.URBAN, ItineraryTags.WILDLIFE))

    every { switzerland.scoreFromPreferences(any()) } returns 0.0
    every { tokyo.scoreFromPreferences(any()) } returns 1.0

    val sortedItineraryList = mapViewModel.sortItinerariesFromPreferences(itineraries, pref)
    assertEquals(listOf(tokyo, switzerland), sortedItineraryList)
  }

  @Test
  fun getItineraryFromUids() = runBlocking {
    val repo = mapOf(FakeItinerary.TOKYO.uid to FakeItinerary.TOKYO)

    coEvery { itineraryRepository.getItinerary(any()) } answers
        {
          val uid = invocation.args[0] as String
          repo[uid]!!
        }

    mapViewModel.getItineraryFromUids(listOf(FakeItinerary.TOKYO.uid)).test {
      val emittedItineraryList = awaitItem()
      assertEquals(listOf(FakeItinerary.TOKYO), emittedItineraryList)
      awaitComplete()
    }
  }

  @Test
  fun setItinerary() {
    val repo = mutableMapOf<String, Itinerary>()

    every { itineraryRepository.setItinerary(any()) } answers
        {
          val itinerary = invocation.args[0] as Itinerary
          repo[itinerary.uid] = itinerary
        }

    mapViewModel.setItinerary(FakeItinerary.TOKYO)
    assertEquals(FakeItinerary.TOKYO, repo[FakeItinerary.TOKYO.uid])
  }

  @Test
  fun deleteItinerary() {
    val repo = mutableMapOf(FakeItinerary.TOKYO.uid to FakeItinerary.TOKYO)

    every { itineraryRepository.deleteItinerary(any()) } answers
        {
          val itinerary = invocation.args[0] as Itinerary
          repo.remove(itinerary.uid)
        }

    mapViewModel.deleteItinerary(FakeItinerary.TOKYO)
    assertEquals(0, repo.size)
  }

  @ExperimentalCoroutinesApi
  @Test
  fun fetchPolylineLocations() = runTest {
    val locations = FakeItinerary.TOKYO.locations.map { it.toLatLng() }

    every { directionsRepository.getPolylineWayPoints(any(), any(), any(), any()) } returns
        MutableLiveData(locations)

    mapViewModel.fetchPolylineLocations(FakeItinerary.TOKYO)

    advanceUntilIdle()

    val polylineLocations = mapViewModel.getPolylinePointsLiveData().value
    assertNotNull(polylineLocations)
  }

  @ExperimentalCoroutinesApi
  @Test
  fun getPolylinePointsLiveData() = runTest {
    val tokyoLocations = FakeItinerary.TOKYO.locations.map { it.toLatLng() }
    val sanfranciscoLocations = FakeItinerary.SAN_FRANCISCO.locations.map { it.toLatLng() }

    every { directionsRepository.getPolylineWayPoints(any(), any(), any(), any()) } returns
        MutableLiveData(tokyoLocations) andThen
        MutableLiveData(sanfranciscoLocations)

    mapViewModel.fetchPolylineLocations(FakeItinerary.TOKYO)

    advanceUntilIdle()

    var polylineLocations = mapViewModel.getPolylinePointsLiveData().value
    assertEquals(tokyoLocations, polylineLocations)

    mapViewModel.fetchPolylineLocations(FakeItinerary.SAN_FRANCISCO)

    advanceUntilIdle()

    polylineLocations = mapViewModel.getPolylinePointsLiveData().value
    assertEquals(sanfranciscoLocations, polylineLocations)
  }

  @Test
  fun getUserLocation() = runBlocking {
    val epflLocation = Location("TestProvider")
    epflLocation.latitude = 46.5188
    epflLocation.longitude = 6.5593
    val delta = 0.001

    every { userLocationClient.getLocationUpdates(any()) } returns flow { emit(epflLocation) }

    val userLocationFlow = mapViewModel.getUserLocation()
    val emittedLocation = userLocationFlow.first()

    assertEquals(epflLocation.latitude, emittedLocation.latitude, delta)
    assertEquals(epflLocation.longitude, emittedLocation.longitude, delta)
  }
}
