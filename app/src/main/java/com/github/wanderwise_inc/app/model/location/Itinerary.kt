package com.github.wanderwise_inc.app.model.location

import java.io.InvalidObjectException

const val MAX_TAGS: Int = 3 // maximum number of tags allowed for an itinerary

/** @brief labels for accessing itinerary information from a hashmap representation */
object ItineraryLabels {
  const val UID = "uid"
  const val USER_UID = "user_uid"
  const val LOCATIONS = "locations"
  const val TITLE = "title"
  const val DESCRIPTION = "description"
  const val VISIBLE = "visible"
  const val TAGS = "tags"
}

/** @brief score of an itinerary based on some preferences */
typealias Score = Double

/**
 * @param uid a unique identifier
 * @param userUid the UID of the user who created the itinerary
 * @param locations an ordered list of locations
 * @param title the title of the itinerary
 * @param tags a list of tags used for visibility and filtering of itinerary
 * @param description a short description of the itinerary
 * @param visible `true` if the itinerary should be visible publicly
 * @brief represents an itinerary
 */
data class Itinerary(
    var uid: String = "",
    val userUid: String,
    val locations: List<Location>,
    val title: String,
    var tags: List<Tag>,
    val description: String?,
    val visible: Boolean,
    var numLikes: Int = 0
) {
  /** @return a map representation of an itinerary */
  fun toMap(): Map<String, Any> {
    return mapOf(
        ItineraryLabels.UID to uid,
        ItineraryLabels.USER_UID to userUid,
        ItineraryLabels.LOCATIONS to locations.map { location -> location.toMap() },
        ItineraryLabels.TITLE to title,
        ItineraryLabels.TAGS to tags,
        ItineraryLabels.DESCRIPTION to (description ?: ""),
        ItineraryLabels.VISIBLE to visible,
    )
  }

  /*fun copy(): Itinerary {
      val builder = Itinerary.Builder(uid = this.uid)
  }*/

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
      val tags: MutableList<Tag> = mutableListOf(),
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

    /**  */
    fun addTag(tag: Tag): Builder {
      if (tags.size >= 3)
          throw InvalidObjectException("An itinerary should not have more than $MAX_TAGS tags")
      tags.add(tag)
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
      return Itinerary(uid, userUid, locations.toList(), title, tags.toList(), description, visible)
    }
  }

  /**
   * @return a builder with the same values as the itinerary
   * @brief convert an itinerary to a builder with the same attributes
   */
  fun toBuilder(): Builder {
    return Builder(uid, userUid).apply {
      locations.addAll(this@Itinerary.locations)
      tags.addAll(this@Itinerary.tags)
      title = this@Itinerary.title
      description = this@Itinerary.description
      visible = this@Itinerary.visible
    }
  }

  /**
   * @return the score of an itinerary
   * @brief Scoring algorithm for ranking an itinerary based on user preferences. Used for sorting
   */
  fun scoreFromPreferences(preferences: ItineraryPreferences): Score {
    var score: Score = 0.0
    for (tag in preferences.tags) {
      if (tags.contains(tag)) score += 10.0
    }
    score *= (numLikes + 1) // numLikes + 1 to prevent multiplying by zero for unliked itineraries
    return score
  }

  /**
   * @return the center of gravity of all locations in an itinerary. Useful for computing camera
   *   position in maps
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

  /** @brief no-argument constructor for firebase de-serialization */
  constructor() : this("", "", listOf(), "", listOf(), null, false)
}
