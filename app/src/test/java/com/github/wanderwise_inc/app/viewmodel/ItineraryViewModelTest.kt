package com.github.wanderwise_inc.app.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import app.cash.turbine.test
import com.github.wanderwise_inc.app.DEFAULT_USER_UID
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.LocationsRepository
import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.location.FakeLocation.EMPIRE_STATE_BUILDING
import com.github.wanderwise_inc.app.model.location.FakeLocation.STATUE_OF_LIBERTY
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryPreferences
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.utils.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
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

class ItineraryViewModelTest {

  @ExperimentalCoroutinesApi @get:Rule val mainDispatcherRule = MainDispatcherRule()

  @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

  @MockK private lateinit var itineraryRepository: ItineraryRepository

  @MockK private lateinit var directionsRepository: DirectionsRepository

  @MockK private lateinit var locationsRepository: LocationsRepository

  @MockK private lateinit var userLocationClient: UserLocationClient

  private lateinit var itineraryViewModel: ItineraryViewModel

  private val testDispatcher = StandardTestDispatcher()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)
    MockKAnnotations.init(this)
    itineraryViewModel =
        ItineraryViewModel(
            itineraryRepository, directionsRepository, locationsRepository, userLocationClient)
  }

  @Test
  fun getAllPublicItineraries() = runBlocking {
    every { itineraryRepository.getPublicItineraries() } returns
        flow { emit(listOf(FakeItinerary.TOKYO)) }

    val emittedItineraryList = itineraryViewModel.getAllPublicItineraries().first()
    assertEquals(listOf(FakeItinerary.TOKYO), emittedItineraryList)
  }

  @Test
  fun getUserItineraries() = runBlocking {
    every { itineraryRepository.getUserItineraries(any()) } returns
        flow { emit(listOf(FakeItinerary.TOKYO)) }

    val emittedItineraryList = itineraryViewModel.getUserItineraries(DEFAULT_USER_UID).first()
    assertEquals(listOf(FakeItinerary.TOKYO), emittedItineraryList)
  }

  @Test
  fun getItineraryLikes() {
    val totalLikes = itineraryViewModel.getItineraryLikes(listOf(FakeItinerary.TOKYO))
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

    itineraryViewModel.incrementItineraryLikes(FakeItinerary.TOKYO)
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

    itineraryViewModel.decrementItineraryLikes(FakeItinerary.TOKYO)
    assertEquals(0, repo[tokyoLikedItinerary.uid]!!.numLikes)
  }

  @Test
  fun getItinerariesFromPreferences() = runBlocking {
    every { itineraryRepository.getItinerariesWithTags(listOf(ItineraryTags.URBAN)) } returns
        flow { emit(listOf(FakeItinerary.TOKYO)) }

    val emittedItineraryList =
        itineraryViewModel
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

    val sortedItineraryList = itineraryViewModel.sortItinerariesFromPreferences(itineraries, pref)
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

    itineraryViewModel.getItineraryFromUids(listOf(FakeItinerary.TOKYO.uid)).test {
      val emittedItineraryList = awaitItem()
      assertEquals(listOf(FakeItinerary.TOKYO), emittedItineraryList)
      awaitComplete()
    }
  }

  @Test
  fun setItinerary() = runTest {
    val repo = mutableMapOf<String, Itinerary>()

    every { itineraryRepository.setItinerary(any()) } answers
        {
          val itinerary = invocation.args[0] as Itinerary
          repo[itinerary.uid] = itinerary
        }

    runBlocking { itineraryViewModel.setItinerary(FakeItinerary.TOKYO) }
  }

  @Test
  fun deleteItinerary() {
    val repo = mutableMapOf(FakeItinerary.TOKYO.uid to FakeItinerary.TOKYO)

    every { itineraryRepository.deleteItinerary(any()) } answers
        {
          val itinerary = invocation.args[0] as Itinerary
          repo.remove(itinerary.uid)
        }

    itineraryViewModel.deleteItinerary(FakeItinerary.TOKYO)
    assertEquals(0, repo.size)
  }

  @ExperimentalCoroutinesApi
  @Test
  fun fetchPolylineLocations() = runTest {
    val locations = FakeItinerary.TOKYO.locations.map { it.toLatLng() }

    every { directionsRepository.getPolylineWayPoints(any(), any(), any(), any()) } returns
        MutableLiveData(locations)

    itineraryViewModel.fetchPolylineLocations(FakeItinerary.TOKYO)

    advanceUntilIdle()

    val polylineLocations = itineraryViewModel.getPolylinePointsLiveData().value
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

    itineraryViewModel.fetchPolylineLocations(FakeItinerary.TOKYO)

    advanceUntilIdle()

    var polylineLocations = itineraryViewModel.getPolylinePointsLiveData().value
    assertEquals(tokyoLocations, polylineLocations)

    itineraryViewModel.fetchPolylineLocations(FakeItinerary.SAN_FRANCISCO)

    advanceUntilIdle()

    polylineLocations = itineraryViewModel.getPolylinePointsLiveData().value
    assertEquals(sanfranciscoLocations, polylineLocations)
  }

  @ExperimentalCoroutinesApi
  @Test
  fun fetchPlaces() = runTest {
    val locations = listOf(Location(STATUE_OF_LIBERTY.lat, STATUE_OF_LIBERTY.long))

    every { locationsRepository.getPlaces(any(), any(), any()) } returns MutableLiveData(locations)

    itineraryViewModel.fetchPlaces("Statue of Liberty")

    advanceUntilIdle()

    val placesLocations = itineraryViewModel.getPlacesLiveData().value
    assertNotNull(placesLocations)
  }

  @ExperimentalCoroutinesApi
  @Test
  fun getPlacesLiveData() = runTest {
    val statueOfLibertyLocation = listOf(Location(STATUE_OF_LIBERTY.lat, STATUE_OF_LIBERTY.long))
    val empireStateBuildingLocation =
        listOf(Location(EMPIRE_STATE_BUILDING.lat, EMPIRE_STATE_BUILDING.long))

    every { locationsRepository.getPlaces(any(), any(), any()) } returns
        MutableLiveData(statueOfLibertyLocation) andThen
        MutableLiveData(empireStateBuildingLocation)

    itineraryViewModel.fetchPlaces("Statue of Liberty")

    advanceUntilIdle()

    var polylineLocations = itineraryViewModel.getPlacesLiveData().value
    assertEquals(statueOfLibertyLocation, polylineLocations)

    itineraryViewModel.fetchPlaces("Empire State Building")

    advanceUntilIdle()

    polylineLocations = itineraryViewModel.getPlacesLiveData().value
    assertEquals(empireStateBuildingLocation, polylineLocations)
  }

  @Test
  fun getUserLocation() = runBlocking {
    val epflLocation = Location(46.5188, 6.5593)
    val delta = 0.001

    every { userLocationClient.getLocationUpdates(any()) } returns flow { emit(epflLocation) }

    val userLocationFlow = itineraryViewModel.getUserLocation()
    val emittedLocation = userLocationFlow.first()

    assertEquals(epflLocation.lat, emittedLocation.lat, delta)
    assertEquals(epflLocation.long, emittedLocation.long, delta)
  }

  @Test
  fun `saving itineraries should behave correctly`() = runTest {
    coEvery { itineraryRepository.writeItinerariesToDisk(any()) } returns Unit

    val itineraryZero = Itinerary()
    itineraryZero.uid = "0"
    val itineraryOne = Itinerary()
    itineraryOne.uid = "1"
    val itineraryTwo = Itinerary()
    itineraryTwo.uid = "2"

    itineraryViewModel.saveItinerary(itineraryZero)
    itineraryViewModel.saveItineraries(listOf(itineraryOne, itineraryTwo))
  }

  @Test
  fun `remove itinerary to users like should correctly call the func`() = runTest {
    val likedUsers = listOf("0", "1", "2")
    val profileViewModel = mockk<ProfileViewModel>(relaxed = true)
    itineraryViewModel.removeItineraryToUsersLiked("uid", profileViewModel, likedUsers)
    for (user in likedUsers) {
      // verify that the function is called for each user
      // profileViewModel.removeLikedItinerary(user, itineraryUid)
      verify { profileViewModel.removeLikedItinerary(user, "uid") }
    }
  }

  @Test
  fun `get liked users should correctly call the function`() = runTest {
    val likedUsers = listOf("0", "1", "2")
    every { itineraryRepository.getLikedUsers("uid") } returns flow { emit(likedUsers) }
    val emittedLikedUsers = itineraryViewModel.getLikedUsers("uid").first()
    assertEquals(likedUsers, emittedLikedUsers)
    verify { itineraryRepository.getLikedUsers("uid") }
  }
}
