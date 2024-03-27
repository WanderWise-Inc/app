package com.github.wanderwise_inc.app.model.location

data class Itinerary(
    val locations: List<Location>,
    val creator: Int,
    val title: String,
    val description: String?,
    val visible: Boolean
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

