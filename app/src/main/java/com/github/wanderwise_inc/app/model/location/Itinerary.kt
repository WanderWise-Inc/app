package com.github.wanderwise_inc.app.model.location

/**
 * @brief labels for accessing itinerary information from a hashmap representation
 */
enum class ItineraryLabels(val dbLabel: String) {
    UID("uid"),
    USER_UID("user_uid"),
    LOCATIONS("locations"),
    TITLE("title"),
    DESCRIPTION("description"),
    VISIBLE("visible")
}

/**
 * @brief represents an itinerary
 *
 * @param uid a unique identifier
 * @param userUid the UID of the user who created the itinerary
 * @param locations an ordered list of locations
 * @param title the title of the itinerary
 * @param description a short description of the itinerary
 * @param visible `true` if the itinerary should be visible publicly
 */
data class Itinerary(
    var uid: String = "",
    val userUid: String,
    val locations: List<Location>,
    val title: String,
    val description: String?,
    val visible: Boolean,
) {
    /**
     * @return a map representation of an itinerary
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            ItineraryLabels.UID.dbLabel to uid,
            ItineraryLabels.USER_UID.dbLabel to userUid,
            ItineraryLabels.LOCATIONS.dbLabel to locations.map { location -> location.toMap() },
            ItineraryLabels.TITLE.dbLabel to title,
            ItineraryLabels.DESCRIPTION.dbLabel to (description ?: ""),
            ItineraryLabels.VISIBLE.dbLabel to visible,
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
            return Itinerary(uid, userUid, locations.toList(), title, description, visible)
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