package com.github.wanderwise_inc.app.model

import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.Kilometers
import com.github.wanderwise_inc.app.model.location.Location
import com.google.android.gms.maps.model.LatLng
import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Test

class LocationTest {
  private val epflLat = 46.519126741544575
  private val epflLon = 6.5676006970802145
  private val epflLocation = Location(epflLat, epflLon)

  @Test
  fun toLatLngTest() {
    val expected = LatLng(epflLat, epflLon)
    assertEquals(expected, epflLocation.toLatLng())
  }

  @Test
  fun fromLatLngTest() {
    val actual = Location.fromLatLng(LatLng(epflLat, epflLon))
    assertEquals(epflLocation, actual)
  }

  @Test
  fun toMapTest() {
    val expected =
        mapOf(
            "lat" to epflLat,
            "long" to epflLon,
            "title" to "",
            "address" to "",
            "googleRating" to 0f)
    assertEquals(expected, epflLocation.toMap())
  }

  @Test
  fun distToTest1() {
    // source: maps.google.com
    val parisLocation = Location(48.8660530581034, 2.3545854606836865)
    val londonLocation = Location(51.51485441449061, -0.10778831509702094)

    // distance operation should commute
    val actual0 = parisLocation.distTo(londonLocation)
    val actual1 = londonLocation.distTo(parisLocation)

    val expected: Kilometers = 342.54 // source: https://www.gps-coordinates.net/distance
    val delta: Kilometers = expected * 0.01 // 10% margin of error
    assert(abs(actual0 - expected) < delta)
    assert(actual0 == actual1)
  }

  @Test
  fun distToTest2() {
    // a point should have distance 0 to itself
    val actual = epflLocation.distTo(epflLocation)
    assert(actual == 0.0)
  }

  @Test
  fun distToStartTest() {
    val parisLocation = Location(48.8660530581034, 2.3545854606836865)
    val londonLocation = Location(51.51485441449061, -0.10778831509702094)

    val testItinerary =
        Itinerary.Builder(userUid = "none")
            .title("Test Itinerary")
            .addLocation(parisLocation)
            .addLocation(londonLocation)
            .build()

    assert(parisLocation.distToStart(testItinerary) == parisLocation.distTo(parisLocation))
    assert(londonLocation.distToStart(testItinerary) == londonLocation.distTo(parisLocation))
  }

  @Test
  fun noArgConstructorTest() {
    assertEquals(Location(), Location(0.0, 0.0))
  }
}
