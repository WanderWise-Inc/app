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

  private val sfObject = FakeItinerary.SAN_FRANCISCO
  private val tkObject = FakeItinerary.TOKYO
  private val swObject = FakeItinerary.SWITZERLAND

  @Test
  fun toBuilder() {
    val builder = sfObject.toBuilder()
    assertEquals(sfObject.uid, builder.uid)
    assertEquals(sfObject.userUid, builder.userUid)
    assertEquals(sfObject.locations, builder.locations)
    assertEquals(sfObject.title, builder.title)
    assertEquals(sfObject.tags, builder.tags)
    assertEquals(sfObject.description, builder.description)
    assertEquals(sfObject.visible, builder.visible)
  }

  @Test
  fun scoreFromPreferences() {
    val sfPreferences = ItineraryPreferences(listOf(ItineraryTags.URBAN))
    val tkPreferences = ItineraryPreferences(listOf(ItineraryTags.URBAN, ItineraryTags.CULTURAL))
    val swPreferences = ItineraryPreferences(listOf(ItineraryTags.URBAN, ItineraryTags.CULTURAL))
    assertEquals(
        10.0 * (sfObject.getLikes() + 1), sfObject.scoreFromPreferences(sfPreferences), 0.000001)
    assertEquals(
        20.0 * (tkObject.getLikes() + 1), tkObject.scoreFromPreferences(tkPreferences), 0.000001)
    assertEquals(
        0.0 * (swObject.getLikes() + 1), swObject.scoreFromPreferences(swPreferences), 0.000001)
  }

  @Test
  fun computeCenterOfGravity() {
    val newLocation = Location(46.61844, 7.705)
    val centerOfGrav = swObject.computeCenterOfGravity()
    assertEquals(newLocation.lat, centerOfGrav.lat, 0.0001)
    assertEquals(newLocation.long, centerOfGrav.long, 0.0001)
  }

  @Test
  fun getUid() {
    assertEquals(sfObject.uid, sfObject.getItineraryUid())
    assertEquals(tkObject.uid, tkObject.getItineraryUid())
    assertEquals(swObject.uid, swObject.getItineraryUid())
  }

  @Test
  fun setUid() {
    val newUid = "newUid"
    sfObject.setItineraryUid(newUid)
    assertEquals(newUid, sfObject.uid)
  }

  @Test
  fun getUserUid() {
    assertEquals("Elena Cruz", sfObject.userUid)
    assertEquals("Sophia Reynolds", tkObject.userUid)
    assertEquals("Liam Bennett", swObject.userUid)
  }

  @Test
  fun `getLocations should return the correct locations of an itinerary`() {
    assertEquals(sfObject.locations, sfObject.getItineraryLocations())
    assertEquals(tkObject.locations, tkObject.getItineraryLocations())
    assertEquals(swObject.locations, swObject.getItineraryLocations())
  }

  @Test
  fun `get likes should return the correct number of like`() {
    assertEquals(sfObject.numLikes, sfObject.getLikes())
    assertEquals(tkObject.numLikes, tkObject.getLikes())
    assertEquals(swObject.numLikes, swObject.getLikes())
  }

  @Test
  fun `incrementLike should increment the number of likes`() {
    val likes = sfObject.numLikes
    sfObject.incrementLike()
    assertEquals(likes + 1, sfObject.numLikes)
  }

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
