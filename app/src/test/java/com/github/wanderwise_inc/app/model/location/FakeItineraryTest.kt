package com.github.wanderwise_inc.app.model.location

import org.junit.Assert.*
import org.junit.Test

class FakeItineraryTest {

  @Test
  fun getTOKYO() {
    assertNotNull(FakeItinerary.TOKYO)
  }

  @Test
  fun getSAN_FRANCISCO() {
    assertNotNull(FakeItinerary.SAN_FRANCISCO)
  }

  @Test
  fun getSWITZERLAND() {
    assertNotNull(FakeItinerary.SWITZERLAND)
  }
}
