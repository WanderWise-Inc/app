package com.github.wanderwise_inc.app.data

import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ItineraryRepositoryTest {
  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

  private val itineraryObject = FakeItinerary
  private lateinit var itineraryRepository: ItineraryRepository
  private val locationsList =
      listOf(itineraryObject.SAN_FRANCISCO, itineraryObject.SWITZERLAND, itineraryObject.TOKYO)

  @Before
  fun setup() {
    itineraryRepository = ItineraryRepositoryTestImpl()
  }

  @Test
  fun `getPublicItineraries should return an empty list if no itineraries were added`() = runTest {
    val getPublicList = itineraryRepository.getPublicItineraries().first()
    assertTrue(getPublicList.isEmpty())
  }

  @Test
  fun `getPublicItineraries should correctly return public itineraries`() = runTest {
    for (i in locationsList.indices) {
      itineraryRepository.setItinerary(locationsList[i])
    }

    val getPublicList = itineraryRepository.getPublicItineraries().first()
    assertEquals(locationsList.size, getPublicList.size)

    for (i in getPublicList.indices) {
      assertEquals(locationsList[i], getPublicList[i])
    }
  }

  @Test
  fun `getUserItineraries should correctly returns a list of user itineraries`() = runTest {
    for (i in locationsList.indices) {
      itineraryRepository.setItinerary(locationsList[i])
    }

    val user0List = itineraryRepository.getUserItineraries("Sophia Reynolds").first()
    val user1List = itineraryRepository.getUserItineraries("Elena Cruz").first()
    val user2List = itineraryRepository.getUserItineraries("Liam Bennett").first()

    assertTrue(user0List.size == 1)
    assertTrue(user1List.size == 1)
    assertTrue(user2List.size == 1)

    assertEquals(itineraryObject.TOKYO, user0List[0])
    assertEquals(itineraryObject.SAN_FRANCISCO, user1List[0])
    assertEquals(itineraryObject.SWITZERLAND, user2List[0])
  }

  @Test
  fun `getItinerariesWithTags should return correct list of itineraries with following tags`() =
      runTest {
        for (i in locationsList.indices) {
          itineraryRepository.setItinerary(locationsList[i])
        }
        val tags0 = listOf(ItineraryTags.URBAN)
        val tags1 = listOf(ItineraryTags.ACTIVE)
        val tags2 = listOf(ItineraryTags.ADVENTURE, ItineraryTags.URBAN)
        val list0 = itineraryRepository.getItinerariesWithTags(tags0).first()
        val list1 = itineraryRepository.getItinerariesWithTags(tags1).first()
        val list2 = itineraryRepository.getItinerariesWithTags(tags2).first()

        assertTrue(list0.size == 2)
        assertTrue(list1.size == 1)
        assertTrue(list2.size == 3)

        assertTrue(list0.containsAll(listOf(itineraryObject.TOKYO, itineraryObject.SAN_FRANCISCO)))
        assertTrue(list1.contains(itineraryObject.SAN_FRANCISCO))
        assertTrue(list2.containsAll(locationsList))
      }

  @Test
  fun `getItinerary should return the itinerary with the given uid`() = runTest {
    for (i in locationsList.indices) {
      itineraryRepository.setItinerary(locationsList[i])
    }

    for (i in 0..2) {
      assertEquals(locationsList[i], itineraryRepository.getItinerary(i.toString()))
    }
  }

  @Test
  fun `setItinerary should correctly add a new itinerary to the list`() = runTest {
    val a = itineraryRepository.getPublicItineraries().first()
    assertTrue(a.isEmpty())
    for (i in locationsList.indices) {
      itineraryRepository.setItinerary(locationsList[i])
      val currList = itineraryRepository.getPublicItineraries().first()
      assertEquals(i + 1, currList.size)
      assertTrue(currList.contains(locationsList[i]))
    }
  }

  @Test
  fun `update Itinerary should correctly update the itinerary in the list`() = runTest {
    for (i in 0..1) {
      itineraryRepository.setItinerary(locationsList[i])
    }
    val getList = itineraryRepository.getPublicItineraries().first()
    assertTrue(getList.size == 2)
    assertTrue(
        getList.containsAll(listOf(itineraryObject.SWITZERLAND, itineraryObject.SAN_FRANCISCO)))

    itineraryRepository.updateItinerary(getList[0].uid, itineraryObject.SAN_FRANCISCO)
    val updateList = itineraryRepository.getPublicItineraries().first()
    assertTrue(getList.containsAll(updateList))
  }

  @Test
  fun `delete itinerary should correctly delete the itinerary in the list`() = runTest {
    for (i in 0..2) {
      itineraryRepository.setItinerary(locationsList[i])
    }

    val a = itineraryRepository.getPublicItineraries().first()
    assertTrue(a.size == 3)

    for (i in 2 downTo 0) {
      itineraryRepository.deleteItinerary(locationsList[i])
      val currList = itineraryRepository.getPublicItineraries().first()
      assertTrue(currList.size == i)
      assertFalse(currList.contains(locationsList[i]))
    }
  }
}
