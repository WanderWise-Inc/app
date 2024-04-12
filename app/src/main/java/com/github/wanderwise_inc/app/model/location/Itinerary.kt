package com.github.wanderwise_inc.app.model.location

/** @brief labels for accessing itinerary information from a hashmap representation */
enum class ItineraryLabels(val dbLabel: String) {
  UID("uid"),
  USER_UID("user_uid"),
  LOCATIONS("locations"),
  TITLE("title"),
  DESCRIPTION("description"),
  VISIBLE("visible")
}

/**
 * @param uid a unique identifier
 * @param userUid the UID of the user who created the itinerary
 * @param locations an ordered list of locations
 * @param title the title of the itinerary
 * @param description a short description of the itinerary
 * @param visible `true` if the itinerary should be visible publicly
 * @brief represents an itinerary
 */
data class Itinerary(
    var uid: String = "",
    val userUid: String,
    val locations: List<Location>,
    val title: String,
    val description: String?,
    val visible: Boolean,
) {
  /** @return a map representation of an itinerary */
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
   * @param uid a unique identifier
   * @param userUid the UID of the user who created the itinerary
   * @param locations an ordered list of locations
   * @param title the title of the itinerary
   * @param description a short description of the itinerary
   * @param visible `true` if the itinerary should be visible publicly
   * @brief builder for an itinerary
   */
  data class Builder(
      var uid: String = "",
      val userUid: String,
      var locations: MutableList<Location> = mutableListOf(),
      var title: String = "",
      var description: String? = null,
      var visible: Boolean = false
  ) {
    /**
     * @param location the location to be added
     * @return the builder to support method chaining
     * @brief add a location to the itinerary builder list of locations
     */
    fun addLocation(location: Location): Builder {
      locations.add(location)
      return this
    }

    /**
     * @param title the new title
     * @return the builder to support method chaining
     * @brief set the title of the itinerary builder
     */
    fun title(title: String): Builder {
      this.title = title
      return this
    }

    /**
     * @param description the new description
     * @return the builder to support method chaining
     * @brief set the description of the itinerary builder
     */
    fun description(description: String): Builder {
      this.description = description
      return this
    }

    /**
     * @param visible the new visibility
     * @return the builder to support method chaining
     * @brief set the visibility of the itinerary builder
     */
    fun visible(visible: Boolean): Builder {
      this.visible = visible
      return this
    }

    /**
     * @return an itinerary with the parameters of the builder
     * @throws IllegalArgumentException if no locations are provided
     * @throws IllegalArgumentException if the title is blank
     * @brief build the itinerary from its builder
     */
    fun build(): Itinerary {
      require(locations.isNotEmpty()) { "At least one location must be provided" }
      require(title.isNotBlank()) { "Title must not be blank" }
      return Itinerary(uid, userUid, locations.toList(), title, description, visible)
    }
  }

  /**
   * @return a builder with the same values as the itinerary
   * @brief convert an itinerary to a builder with the same attributes
   */
  fun toBuilder(): Builder {
    return Builder(uid, userUid).apply {
      locations.addAll(this@Itinerary.locations)
      title = this@Itinerary.title
      description = this@Itinerary.description
      visible = this@Itinerary.visible
    }
  }
}
