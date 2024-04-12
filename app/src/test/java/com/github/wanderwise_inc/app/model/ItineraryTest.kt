package com.github.wanderwise_inc.app.model

import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryLabels
import com.github.wanderwise_inc.app.model.location.Location
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ItineraryTest {

  private val sampleLocation = Location(1.3521, 103.8198)
  private val sampleTag = "CULTURE"

  // Helper function to create a sample Itinerary
  private fun createSampleItinerary(): Itinerary {
    return Itinerary(
        uid = "1",
        userUid = "user1",
        locations = listOf(sampleLocation),
        title = "Sample Itinerary",
        tags = listOf(sampleTag),
        description = "Sample Description",
        visible = true)
  }

  @Test
  fun `toMap returns correct map representation`() {
    val itinerary = createSampleItinerary()
    val map = itinerary.toMap()

    assertEquals(itinerary.uid, map[ItineraryLabels.UID])
    assertEquals(itinerary.userUid, map[ItineraryLabels.USER_UID])
    assertEquals(itinerary.locations.map { it.toMap() }, map[ItineraryLabels.LOCATIONS])
    assertEquals(itinerary.title, map[ItineraryLabels.TITLE])
    assertEquals(itinerary.tags, map[ItineraryLabels.TAGS])
    assertEquals(itinerary.description, map[ItineraryLabels.DESCRIPTION])
    assertEquals(itinerary.visible, map[ItineraryLabels.VISIBLE])
  }

  @Test
  fun `builder builds correctly`() {
    val builder =
        Itinerary.Builder(userUid = "user2")
            .title("My Itinerary")
            .addLocation(sampleLocation)
            .addTag(sampleTag)
            .description("A nice trip")
            .visible(true)

    val itinerary = builder.build()

    assertEquals("user2", itinerary.userUid)
    assertEquals("My Itinerary", itinerary.title)
    assertEquals(1, itinerary.locations.size)
    assertEquals(sampleLocation, itinerary.locations.first())
    assertEquals(1, itinerary.tags.size)
    assertEquals(sampleTag, itinerary.tags.first())
    assertEquals("A nice trip", itinerary.description)
    assertTrue(itinerary.visible)
  }

  @Test(expected = IllegalArgumentException::class)
  fun `builder throws IllegalArgumentException if no locations`() {
    Itinerary.Builder(userUid = "user1").title("Title").build()
  }

  @Test(expected = IllegalArgumentException::class)
  fun `builder throws IllegalArgumentException if title is blank`() {
    Itinerary.Builder(userUid = "user1").addLocation(sampleLocation).build()
  }
}
