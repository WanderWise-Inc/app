package com.github.wanderwise_inc.app.model.location

/**
 * Represents a tag
 * Used both for storage and for display
 */
typealias Tag = String

/**
 * @brief itinerary tags used for filtering based on user preferences
 *
 * String representations should consist of a single word and start with a Capital letter
 */
object ItineraryTags {
  const val ACTIVE = "Active"
  const val ADVENTURE = "Adventure"
  const val BUDGET = "Budget"
  const val CULTURAL = "Cultural"
  const val FOOD = "Food"
  const val FOODIE = "Foodie"
  const val LUXURY = "Luxury"
  const val NATURE = "Nature"
  const val PHOTOGRAPHY = "Photography"
  const val RELAXATION = "Relaxation"
  const val ROMANCE = "Romance"
  const val RURAL = "Rural"
  const val SOCIAL = "Social"
  const val URBAN = "Urban"
  const val WELLNESS = "Wellness"
  const val WILDLIFE = "Wildlife"
}
