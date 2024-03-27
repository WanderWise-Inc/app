package com.github.wanderwise_inc.app.model.location

/**
 * @brief represents an itinerary
 *
 * @param uid a unique identifier
 * @param associatedUserUid the UID of the user who created the itinerary
 * @param locations an ordered list of locations
 * @param title
 * @param description
 * @param visible `true` if the itinerary should be visible publicly
 */
data class Itinerary(
    var uid: String = "",
    val associatedUserUid: String,
    val locations: List<Location>,
    val title: String,
    val description: String?,
    val visible: Boolean,
) {
    data class Builder(
        var locations: MutableList<Location> = mutableListOf(),
        var creator: Int = 0,
        var title: String= "",
        var description: String? = null,
        var visible: Boolean = false
    ) {
        fun addLocation(location: Location): Builder {
            return this
        }

        fun creator(creator: Int): Builder {
            return this
        }

        fun title(title: String): Builder {
            return this
        }

        fun description(description: String): Builder {
            return this
        }

        fun visible(visible: Boolean): Builder {
            return this
        }

        fun build(): Itinerary? {
            return null
        }
    }

    fun toBuilder(): Builder {
        return Builder()
    }
}

