package com.github.wanderwise_inc.app.model.location

import org.junit.Assert.*
import org.junit.Test

class ItineraryTest {

  private val sfObject = FakeItinerary.SAN_FRANCISCO
  private val tkObject = FakeItinerary.TOKYO
  private val swObject = FakeItinerary.SWITZERLAND

  @Test
  fun toMap() {
    val expected =
        mapOf(
            ItineraryLabels.UID to sfObject.uid,
            ItineraryLabels.USER_UID to sfObject.userUid,
            ItineraryLabels.LOCATIONS to sfObject.locations.map { location -> location.toMap() },
            ItineraryLabels.TITLE to sfObject.title,
            ItineraryLabels.TAGS to sfObject.tags,
            ItineraryLabels.DESCRIPTION to (sfObject.description ?: ""),
            ItineraryLabels.VISIBLE to sfObject.visible,
            ItineraryLabels.PRICE to sfObject.price,
            ItineraryLabels.TIME to sfObject.time)
    assertEquals(expected, sfObject.toMap())
  }

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
}
