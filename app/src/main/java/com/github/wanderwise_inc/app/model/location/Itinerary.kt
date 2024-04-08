package com.github.wanderwise_inc.app.model.location

import java.io.InvalidObjectException

const val MAX_TAGS: Int = 3 // maximum number of tags allowed for an itinerary

/**
 * @brief labels for accessing itinerary information from a hashmap representation
 */
object ItineraryLabels {
    const val UID = "uid"
    const val USER_UID = "user_uid"
    const val LOCATIONS = "locations"
    const val TITLE = "title"
    const val DESCRIPTION = "description"
    const val VISIBLE = "visible"
    const val TAGS = "tags"
}

/**
 * @brief represents an itinerary
 *
 * @param uid a unique identifier
 * @param userUid the UID of the user who created the itinerary
 * @param locations an ordered list of locations
 * @param title the title of the itinerary
 * @param tags a list of tags used for visibility and filtering of itinerary
 * @param description a short description of the itinerary
 * @param visible `true` if the itinerary should be visible publicly
 */
data class Itinerary(
    var uid: String = "",
    val userUid: String,
    val locations: List<Location>,
    val title: String,
    val tags: List<Tag>,
    val description: String?,
    val visible: Boolean,
) {
    /**
     * @return a map representation of an itinerary
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            ItineraryLabels.UID to uid,
            ItineraryLabels.USER_UID to userUid,
            ItineraryLabels.LOCATIONS to locations.map { location -> location.toMap() },
            ItineraryLabels.TITLE to title,
            ItineraryLabels.TAGS to tags.map { tag -> tag.str },
            ItineraryLabels.DESCRIPTION to (description ?: ""),
            ItineraryLabels.VISIBLE to visible,
        )
    }
    /**
     * @brief builder for an itinerary
     *
     * @param uid a unique identifier
     * @param userUid the UID of the user who created the itinerary
     * @param locations an ordered list of locations
     * @param title the title of the itinerary
     * @param description a short description of the itinerary
     * @param visible `true` if the itinerary should be visible publicly
     */
    data class Builder(
        var uid: String = "",
        val userUid: String,
        var locations: MutableList<Location> = mutableListOf(),
        var title: String= "",
        val tags: MutableList<Tag> = mutableListOf(),
        var description: String? = null,
        var visible: Boolean = false
    ) {
        /**
         * @brief add a location to the itinerary builder list of locations
         *
         * @param location the location to be added
         * @return the builder to support method chaining
         */
        fun addLocation(location: Location): Builder {
            locations.add(location)
            return this
        }

        /**
         *
         */
        fun addTag(tag: Tag): Builder {
            if (tags.size >= 3)
                throw InvalidObjectException("An itinerary should not have more than $MAX_TAGS tags")
            tags.add(tag)
            return this
        }

        /**
         * @brief set the title of the itinerary builder
         *
         * @param title the new title
         * @return the builder to support method chaining
         */
        fun title(title: String): Builder {
            this.title = title
            return this
        }

        /**
         * @brief set the description of the itinerary builder
         *
         * @param description the new description
         * @return the builder to support method chaining
         */
        fun description(description: String): Builder {
            this.description = description
            return this
        }

        /**
         * @brief set the visibility of the itinerary builder
         *
         * @param visible the new visibility
         * @return the builder to support method chaining
         */
        fun visible(visible: Boolean): Builder {
            this.visible = visible
            return this
        }

        /**
         * @brief build the itinerary from its builder
         *
         * @throws IllegalArgumentException if no locations are provided
         * @throws IllegalArgumentException if the title is blank
         * @return an itinerary with the parameters of the builder
         */
        fun build(): Itinerary {
            require(locations.isNotEmpty()) { "At least one location must be provided" }
            require(title.isNotBlank()) { "Title must not be blank" }
            return Itinerary(uid, userUid, locations.toList(), title, tags.toList(), description, visible)
        }
    }

    /**
     * @brief convert an itinerary to a builder with the same attributes
     *
     * @return a builder with the same values as the itinerary
     */
    fun toBuilder(): Builder {
        return Builder(uid, userUid).apply {
            locations.addAll(this@Itinerary.locations)
            title = this@Itinerary.title
            description = this@Itinerary.description
            visible = this@Itinerary.visible
        }
    }

    /**
     * @return the center of gravity of all locations in an itinerary. Useful for computing
     * camera position in maps
     */
    fun computeCenterOfGravity(): Location {
        var avgLat = 0.0
        var avgLon = 0.0
        locations.map {
            avgLat += it.lat
            avgLon += it.long
        }
        avgLat /= locations.size
        avgLon /= locations.size
        return Location(avgLat, avgLon)
    }
}