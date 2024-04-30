package com.github.wanderwise_inc.app.model.location

import java.io.InvalidObjectException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ItineraryTest {

  private val itinerary =
      Itinerary(
          uid = "1",
          userUid = "user123",
          locations = listOf(Location(10.0, 20.0), Location(30.0, 40.0)),
          title = "Vacation Plan",
          tags = listOf(ItineraryTags.NATURE),
          description = "Summer vacation",
          visible = true)

  @Test
  fun toMap() {
    val map = itinerary.toMap()

    assertEquals("1", map[ItineraryLabels.UID])
    assertEquals("Summer vacation", map[ItineraryLabels.DESCRIPTION])

    val emptyMap = Itinerary().toMap()
    assertEquals("", emptyMap[ItineraryLabels.DESCRIPTION])
  }

  @Test
  fun scoreFromPreferences() {
    val preferences = ItineraryPreferences(listOf(ItineraryTags.NATURE, ItineraryTags.ACTIVE))
    val score = itinerary.scoreFromPreferences(preferences)

    assertEquals(10.0, score, 0.01)
  }

  @Test
  fun computeCenterOfGravity() {
    val center = itinerary.computeCenterOfGravity()

    assertEquals(20.0, center.lat, 0.01)
    assertEquals(30.0, center.long, 0.01)
  }

  @Test
  fun testNoArgumentConstructor() {
    // Create an itinerary using the no-argument constructor
    val emptyItinerary = Itinerary()

    // Assert that all fields are initialized to their default values
    assertEquals("", emptyItinerary.uid)
    assertEquals("", emptyItinerary.userUid)
    assertTrue(emptyItinerary.locations.isEmpty())
    assertEquals("", emptyItinerary.title)
    assertTrue(emptyItinerary.tags.isEmpty())
    assertNull(emptyItinerary.description)
    assertFalse(emptyItinerary.visible)
  }

  @Test
  fun testBuilder() {
    val builder = itinerary.toBuilder()

    builder.uid = "2"
    builder
        .title("Changed Title")
        .addTag(ItineraryTags.RELAXATION)
        .addLocation(Location(15.0, -10.0))
        .description("Modified Description")
        .visible(false)
        .price(10f)
        .time(2)

    assertEquals("2", builder.uid)
    assertEquals("user123", builder.userUid)
    assertEquals("Changed Title", builder.title)
    assertEquals(2, builder.tags.size) // New one should have 3 tags
    assertEquals(3, builder.locations.size) // New one should have 2 locations
    assertEquals("Modified Description", builder.description)
    assertFalse(builder.visible)
    assertEquals(10f, builder.price)
    assertEquals(2, builder.time)
    assertThrows(IllegalArgumentException::class.java) { builder.price(-1f) }
    assertThrows(IllegalArgumentException::class.java) { builder.time(-1) }

    val newItinerary = builder.build()

    assertEquals("2", newItinerary.uid)
    assertEquals("Changed Title", newItinerary.title)
    assertEquals(2, newItinerary.tags.size) // New one should have 3 tags
    assertEquals(3, newItinerary.locations.size) // New one should have 2 locations
    assertEquals("Modified Description", newItinerary.description)
    assertFalse(newItinerary.visible)
    assertEquals(10f, newItinerary.price)
    assertEquals(2, newItinerary.time)

    assertThrows(InvalidObjectException::class.java) {
      builder.addTag(ItineraryTags.ROMANCE).addTag(ItineraryTags.WILDLIFE)
    }

    val emptyBuilder = Itinerary().toBuilder()

    assertThrows(IllegalArgumentException::class.java) { emptyBuilder.build() }

    emptyBuilder.addLocation(Location(0.0, 0.0))

    assertThrows(IllegalArgumentException::class.java) { emptyBuilder.build() }
  }
}
