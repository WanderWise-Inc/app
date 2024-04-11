package com.github.wanderwise_inc.app.model.location

/**
 * @brief Represents user preferences for itinerary queries
 *
 * @param tags
 */
data class ItineraryPreferences (
    val tags: List<Tag>,
    val minRating: Double   // contained in [0, 1]
)