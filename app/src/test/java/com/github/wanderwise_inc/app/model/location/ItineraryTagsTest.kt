package com.github.wanderwise_inc.app.model.location

import org.junit.Assert.assertNotNull
import org.junit.Test

class ItineraryTagsTest {

    @Test
    fun testItineraryTags() {
        assertNotNull(ItineraryTags.ACTIVE)
        assertNotNull(ItineraryTags.ADVENTURE)
        assertNotNull(ItineraryTags.BUDGET)
        assertNotNull(ItineraryTags.CULTURAL)
        assertNotNull(ItineraryTags.FOOD)
        assertNotNull(ItineraryTags.FOODIE)
        assertNotNull(ItineraryTags.LUXURY)
        assertNotNull(ItineraryTags.NATURE)
        assertNotNull(ItineraryTags.PHOTOGRAPHY)
        assertNotNull(ItineraryTags.RELAXATION)
        assertNotNull(ItineraryTags.ROMANCE)
        assertNotNull(ItineraryTags.RURAL)
        assertNotNull(ItineraryTags.SOCIAL)
        assertNotNull(ItineraryTags.URBAN)
        assertNotNull(ItineraryTags.WELLNESS)
        assertNotNull(ItineraryTags.WILDLIFE)
    }
}