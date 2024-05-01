package com.github.wanderwise_inc.app.model.location

import org.junit.Assert.*
import org.junit.Test

class ItineraryTest {
  private val dummyItinerary =
      Itinerary(
          uid = "test",
          userUid = "test",
          locations = listOf(Location(0.0, 0.0)),
          tags = listOf(ItineraryTags.ADVENTURE),
          title = "test",
          description = "test",
          visible = false,
      )

  @Test
  fun toMap() {
    val actual = dummyItinerary.toMap()
    val expected =
        mapOf(
            "uid" to "test",
            "user_uid" to "test",
            "locations" to dummyItinerary.locations.map { it.toMap() },
            "title" to "test",
            "tags" to listOf(ItineraryTags.ADVENTURE),
            "description" to "test",
            "visible" to false,
            "price" to 0f,
            "time" to 0,
        )

    assertEquals(expected, actual)
  }
}
