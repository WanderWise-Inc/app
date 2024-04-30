package com.github.wanderwise_inc.app.model.location

import org.junit.Assert.*

import org.junit.Test
import java.io.InvalidObjectException

class ItineraryTest {

    private val itinerary = Itinerary(
        uid = "1",
        userUid = "user123",
        locations = listOf(Location(10.0, 20.0), Location(30.0, 40.0)),
        title = "Vacation Plan",
        tags = listOf(ItineraryTags.NATURE),
        description = "Summer vacation",
        visible = true
    )

    @Test
    fun toMap() {
        val map = itinerary.toMap()

        assertEquals("1", map[ItineraryLabels.UID])
        assertEquals("Summer vacation", map[ItineraryLabels.DESCRIPTION])
    }

    @Test
    fun testBuilder() {
        val builder = itinerary.toBuilder()

        builder.uid = "2"
        builder.title = "Changed Title"
        builder.addTag(ItineraryTags.RELAXATION)
        builder.locations.add(Location(15.0, -10.0))
        builder.description("Modified Description")
        builder.visible(false)

        val newItinerary = builder.build()

        assertEquals("1", itinerary.uid)
        assertEquals("Vacation Plan", itinerary.title)
        assertEquals(1, itinerary.tags.size) // Original had 2 tags
        assertEquals(2, itinerary.locations.size) // Original had 1 location
        assertEquals("Summer vacation", itinerary.description)
        assertTrue(itinerary.visible)

        assertEquals("2", newItinerary.uid)
        assertEquals("Changed Title", newItinerary.title)
        assertEquals(2, newItinerary.tags.size) // New one should have 3 tags
        assertEquals(3, newItinerary.locations.size) // New one should have 2 locations
        assertEquals("Modified Description", newItinerary.description)
        assertFalse(newItinerary.visible)

        assertThrows(InvalidObjectException::class.java) {
            builder.addTag(ItineraryTags.ROMANCE).addTag(ItineraryTags.WILDLIFE)
        }

        builder.title = ""

        assertThrows(IllegalArgumentException::class.java) {
            builder.build()
        }

        builder.title = "Not Blank"
        builder.locations.clear()

        assertThrows(IllegalArgumentException::class.java) {
            builder.build()
        }
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
}