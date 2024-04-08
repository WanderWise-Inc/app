package com.github.wanderwise_inc.app.model.location

/**
 * @brief Represents user preferences for itinerary queries
 *
 * @param tags
 */
data class ItineraryDetail (
    val tags: List<Tag>,
    val upVotes: Int,
    val downVotes: Int,
) {
    /**
     * @return the rating of the itinerary which is a fraction between [0; 1]
     */
    fun rating(): Double = (this.upVotes.toDouble()) / (upVotes + downVotes)
}