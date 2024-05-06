package com.github.wanderwise_inc.app.model.location

/** @brief mutable itinerary class for usage during UI creation */
data class MutableItinerary(
    val userUid: String, // only immutable member as this should not change during creation
    var locations: MutableList<Location> = mutableListOf(),
    var title: String = "",
    var tags: MutableList<Tag> = mutableListOf(),
    var description: String = "",
    var price: Float = 0f,
    var time: Int = 0,
    var visible: Boolean = false,
) {
  /** converts the mutable data into immutable data for displaying / uploading */
  fun toItinerary(): Itinerary {
    return Itinerary(
        userUid = userUid,
        locations = locations.toList(),
        title = title,
        tags = tags.toList(),
        description = description,
        price = price,
        time = time,
        visible = visible)
  }
}
