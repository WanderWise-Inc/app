package com.github.wanderwise_inc.app.viewmodel

import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryPreferences
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.Location
import junit.framework.TestCase.assertEquals
import kotlin.random.Random
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

/** @brief test class for mapview model */
class MapViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var userLocationClient: UserLocationClient

    private lateinit var itineraryRepository: ItineraryRepository
    private lateinit var mapViewModel: MapViewModel

    // helper function for generating random latitude and longitude...
    private fun randomLat(): Double = Random.nextDouble(-90.0, 90.0)

    private fun randomLon(): Double = Random.nextDouble(-180.0, 180.0)

    // helper function for generating random locations
    private fun randomLocations(size: Int): List<Location> {
        // helper function for generating random locations
        val locations = mutableListOf<Location>()
        for (i in 0 until size) locations.add(Location(randomLat(), randomLon()))
        return locations.toList()
    }

    // some public itineraries
    private val ethanItinerary =
        Itinerary(
            userUid = "Ethan",
            locations = randomLocations(5),
            title = "Ethan's Itinerary",
            tags = listOf(ItineraryTags.BUDGET, ItineraryTags.SOCIAL),
            description = null,
            visible = true
        )
    private val yofiItinerary =
        Itinerary(
            userUid = "Yofthahe",
            locations = randomLocations(10),
            title = "Yofi's Itinerary",
            tags = listOf(ItineraryTags.CULTURAL, ItineraryTags.SOCIAL, ItineraryTags.WILDLIFE),
            description = null,
            visible = true
        )
    private val thomasItinerary =
        Itinerary(
            userUid = "Thomas",
            locations = randomLocations(3),
            title = "Thomas' Itinerary",
            tags = listOf(ItineraryTags.ACTIVE),
            description = null,
            visible = true
        )

    private val epflLat = 46.519126741544575
    private val epflLon = 6.5676006970802145

    @Before
    fun setup() {
        itineraryRepository = ItineraryRepositoryTestImpl()
        mapViewModel = MapViewModel(itineraryRepository, userLocationClient)
    }

    @Test
    fun itinerary_add_and_retrieve() = runTest {
        // insert in shuffled order for next test that tests sorting algorithm
        mapViewModel.setItinerary(yofiItinerary)
        mapViewModel.setItinerary(thomasItinerary)
        mapViewModel.setItinerary(ethanItinerary)

        val retrievedItineraries = mapViewModel.getAllPublicItineraries().first().toSet()
        val expected = setOf(ethanItinerary, yofiItinerary, thomasItinerary)

        assertEquals(expected, retrievedItineraries)
    }

    @Test
    fun itinerary_filtering_from_preferences_outputs_correct_elements_1() = runTest {
        mapViewModel.setItinerary(yofiItinerary)
        mapViewModel.setItinerary(thomasItinerary)
        mapViewModel.setItinerary(ethanItinerary)

        val testPreferences =
            ItineraryPreferences(
                tags = listOf(ItineraryTags.BUDGET, ItineraryTags.SOCIAL),
            )

        val retrievedItineraries =
            mapViewModel.getItinerariesFromPreferences(testPreferences).first()
        assert(retrievedItineraries.contains(ethanItinerary))
        assert(retrievedItineraries.contains(yofiItinerary))
        assert(!retrievedItineraries.contains(thomasItinerary))
    }

    @Test
    fun itinerary_filtering_from_preferences_outputs_correct_elements_2() = runTest {
        mapViewModel.setItinerary(yofiItinerary)
        mapViewModel.setItinerary(thomasItinerary)
        mapViewModel.setItinerary(ethanItinerary)

        val testPreferences =
            ItineraryPreferences(
                tags = listOf(ItineraryTags.ACTIVE),
            )

        val retrievedItineraries =
            mapViewModel.getItinerariesFromPreferences(testPreferences).first()
        assert(!retrievedItineraries.contains(ethanItinerary))
        assert(!retrievedItineraries.contains(yofiItinerary))
        assert(retrievedItineraries.contains(thomasItinerary))
    }

    @Test
    fun itinerary_filtering_from_preferences_outputs_correct_elements_3() = runTest {
        mapViewModel.setItinerary(yofiItinerary)
        mapViewModel.setItinerary(thomasItinerary)
        mapViewModel.setItinerary(ethanItinerary)

        val testPreferences =
            ItineraryPreferences(
                tags = listOf(ItineraryTags.FOODIE),
            )

        val retrievedItineraries =
            mapViewModel.getItinerariesFromPreferences(testPreferences).first()
        assert(!retrievedItineraries.contains(ethanItinerary))
        assert(!retrievedItineraries.contains(yofiItinerary))
        assert(!retrievedItineraries.contains(thomasItinerary))
    }

    @Test
    fun itinerary_sorting_from_preferences_outputs_correct_order() = runTest {
        mapViewModel.setItinerary(yofiItinerary)
        mapViewModel.setItinerary(thomasItinerary)
        mapViewModel.setItinerary(ethanItinerary)

        val testPreferences =
            ItineraryPreferences(
                tags = listOf(ItineraryTags.BUDGET, ItineraryTags.SOCIAL),
            )

        val retrievedItineraries =
            mapViewModel.getItinerariesFromPreferences(testPreferences).first()

        assertEquals(
            listOf(ethanItinerary, yofiItinerary),
            mapViewModel.sortItinerariesFromPreferences(retrievedItineraries, testPreferences)
        )

        assertNotEquals(
            listOf(yofiItinerary, ethanItinerary),
            mapViewModel.sortItinerariesFromPreferences(retrievedItineraries, testPreferences)
        )
    }

    @Test
    fun getting_itinerary_from_username_works_correctly() = runTest {
        mapViewModel.setItinerary(ethanItinerary)
        mapViewModel.setItinerary(yofiItinerary)
        mapViewModel.setItinerary(thomasItinerary)

        val firstQuery = mapViewModel.getUserItineraries("Ethan").first()
        assert(firstQuery.contains(ethanItinerary))
        assert(!firstQuery.contains(yofiItinerary))
        assert(!firstQuery.contains(thomasItinerary))

        val secondQuery = mapViewModel.getUserItineraries("Thomas").first()
        assert(!secondQuery.contains(ethanItinerary))
        assert(!secondQuery.contains(yofiItinerary))
        assert(secondQuery.contains(thomasItinerary))

        val thirdQuery = mapViewModel.getUserItineraries("Oscar").first()
        assertEquals(listOf<Itinerary>(), thirdQuery)
    }

    @Test
    fun deleting_an_itinerary_works_correctly() = runTest {
        mapViewModel.setItinerary(ethanItinerary)
        mapViewModel.setItinerary(yofiItinerary)
        mapViewModel.setItinerary(thomasItinerary)

        mapViewModel.deleteItinerary(yofiItinerary)

        val firstQuery = mapViewModel.getAllPublicItineraries().first()

        assert(!firstQuery.contains(yofiItinerary))
        assert(firstQuery.contains(ethanItinerary))
        assert(firstQuery.contains(thomasItinerary))

        mapViewModel.deleteItinerary(ethanItinerary)
        mapViewModel.deleteItinerary(thomasItinerary)

        val secondQuery = mapViewModel.getAllPublicItineraries().first()
        assertEquals(listOf<Itinerary>(), secondQuery)
    }

    @Test
    fun `getUserLocation returns a flow containing the user location`() = runBlocking {
        val epflLocation = Mockito.mock(android.location.Location::class.java)
        epflLocation.latitude = epflLat
        epflLocation.longitude = epflLon

        `when`(userLocationClient.getLocationUpdates(anyLong())).thenReturn(
            flow {
                emit(
                    epflLocation
                )
            }
        )

        val userLocationFlow = mapViewModel.getUserLocation()
        val emittedLocation = userLocationFlow.first()

        assertEquals(epflLocation, emittedLocation)
    }
}
