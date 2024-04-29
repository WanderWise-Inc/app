package com.github.wanderwise_inc.app.viewmodel

import android.location.Location
import com.github.wanderwise_inc.app.DEFAULT_USER_UID
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryPreferences
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.Score
import com.github.wanderwise_inc.app.model.location.Tag
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class MapViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var itineraryRepository: ItineraryRepository

    @Mock
    private lateinit var directionsRepository: DirectionsRepository

    @Mock
    private lateinit var userLocationClient: UserLocationClient

    private lateinit var mapViewModel: MapViewModel

    @Before
    fun setup() {
        mapViewModel = MapViewModel(itineraryRepository, directionsRepository, userLocationClient)
    }

    @Test
    fun getAllPublicItineraries() = runBlocking {
        `when`(itineraryRepository.getPublicItineraries())
            .thenReturn(flow { emit(listOf(FakeItinerary.TOKYO)) })

        val emittedItineraryList = mapViewModel.getAllPublicItineraries().first()
        assertEquals(listOf(FakeItinerary.TOKYO), emittedItineraryList)
    }

    @Test
    fun getUserItineraries() = runBlocking {
        `when`(itineraryRepository.getUserItineraries(anyString()))
            .thenReturn(flow { emit(listOf(FakeItinerary.TOKYO)) })

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

        `when`(itineraryRepository.updateItinerary(FakeItinerary.TOKYO.uid, FakeItinerary.TOKYO))
            .thenAnswer {
                val uid = it.arguments[0] as String
                val itinerary = it.arguments[1] as Itinerary
                repo[uid] = itinerary
                itinerary
            }

        mapViewModel.incrementItineraryLikes(FakeItinerary.TOKYO)
        assertEquals(1, repo[FakeItinerary.TOKYO.uid]!!.numLikes)
    }

    @Test
    fun decrementItineraryLikes() {
        val tokyoLikedItinerary = FakeItinerary.TOKYO
        ++tokyoLikedItinerary.numLikes
        val repo = mutableMapOf(tokyoLikedItinerary.uid to tokyoLikedItinerary)

        `when`(itineraryRepository.updateItinerary(tokyoLikedItinerary.uid, tokyoLikedItinerary))
            .thenAnswer {
                val uid = it.arguments[0] as String
                val itinerary = it.arguments[1] as Itinerary
                repo[uid] = itinerary
                itinerary
            }

        mapViewModel.decrementItineraryLikes(FakeItinerary.TOKYO)
        assertEquals(0, repo[tokyoLikedItinerary.uid]!!.numLikes)
    }

    @Test
    fun getItinerariesFromPreferences() = runBlocking {
        `when`(itineraryRepository.getItinerariesWithTags(listOf(ItineraryTags.URBAN)))
            .thenReturn(flow { emit(listOf(FakeItinerary.TOKYO)) })

        val emittedItineraryList = mapViewModel.getItinerariesFromPreferences(
            ItineraryPreferences(listOf(ItineraryTags.URBAN))
        ).first()
        assertEquals(listOf(FakeItinerary.TOKYO), emittedItineraryList)
    }

    @Test
    fun sortItinerariesFromPreferences() {
        val tokyo = mock(Itinerary::class.java)
        val switzerland = mock(Itinerary::class.java)
        val itineraries = listOf(switzerland, tokyo)
        val pref = ItineraryPreferences(listOf(ItineraryTags.URBAN, ItineraryTags.WILDLIFE))

        `when`(switzerland.scoreFromPreferences(pref)).thenReturn(0.0)
        `when`(tokyo.scoreFromPreferences(pref)).thenReturn(1.0)

        val sortedItineraryList = mapViewModel.sortItinerariesFromPreferences(itineraries, pref)
        assertEquals(listOf(tokyo, switzerland), sortedItineraryList)
    }

    @Test
    fun getItineraryFromUids() = runBlocking {
        val repo = mapOf(FakeItinerary.TOKYO.uid to FakeItinerary.TOKYO)

        `when`(itineraryRepository.getItinerary(anyString())).thenAnswer {
            val uid = it.arguments[0] as String
            repo[uid]
        }

        val emittedItineraryList = mapViewModel.getItineraryFromUids(listOf(FakeItinerary.TOKYO.uid)).first()
        assertEquals(listOf(FakeItinerary.TOKYO), emittedItineraryList)
    }

    @Test
    fun setItinerary() {
        val repo = mutableMapOf<String, Itinerary>()

        `when`(itineraryRepository.setItinerary(FakeItinerary.TOKYO)).thenAnswer {
            val itinerary = it.arguments[0] as Itinerary
            repo[itinerary.uid] = itinerary
            itinerary
        }

        mapViewModel.setItinerary(FakeItinerary.TOKYO)
        assertEquals(FakeItinerary.TOKYO, repo[FakeItinerary.TOKYO.uid])
    }

    @Test
    fun deleteItinerary() {
        val repo = mutableMapOf(FakeItinerary.TOKYO.uid to FakeItinerary.TOKYO)

        `when`(itineraryRepository.deleteItinerary(FakeItinerary.TOKYO)).thenAnswer {
            val itinerary = it.arguments[0] as Itinerary
            repo.remove(itinerary.uid)
        }

        mapViewModel.deleteItinerary(FakeItinerary.TOKYO)
        assertEquals(0, repo.size)
    }

    @Test
    fun fetchPolylineLocations() {
    }

    @Test
    fun getPolylinePointsLiveData() {
    }

    @Test
    fun getUserLocation() = runBlocking {
        val epflLocation = Mockito.mock(Location::class.java)
        epflLocation.latitude = 46.5188
        epflLocation.longitude = 6.5593

        `when`(userLocationClient.getLocationUpdates(anyLong()))
            .thenReturn(flow { emit(epflLocation) })

        val userLocationFlow = mapViewModel.getUserLocation()
        val emittedLocation = userLocationFlow.first()

        TestCase.assertEquals(epflLocation, emittedLocation)
    }
}