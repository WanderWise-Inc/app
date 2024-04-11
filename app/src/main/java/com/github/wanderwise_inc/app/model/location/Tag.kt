package com.github.wanderwise_inc.app.model.location

import android.content.ActivityNotFoundException
import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents a tag
 *
 * @param str is the string representation of the tag. Used both for storage and for display
 */

typealias Tag = String

/**
 * @brief itinerary tags used for filtering based on user preferences
 *
 * String representations should consist of a single word and start with a Capital letter
 */
object ItineraryTags {
    val SOCIAL      = "Social"
    val CULTURAL    = "Cultural"
    val ADVENTURE   = "Adventure"
    val FOOD        = "Food"
    val RELAXATION  = "Relaxation"
    val NATURE      = "Nature"
    val FOODIE      = "Foodie"
    val RURAL       = "Rural"
    val URBAN       = "Urban"
    val ROMANCE     ="Romance"
    val WILDLIFE    = "Wildlife"
    val PHOTOGRAPHY = "Photography"
    val BUDGET      ="Budget"
    val LUXURY      = "Luxury"
    val ACTIVE      = "Active"
    val WELLNESS    = "Wellness"


}


/*Luxury
Family-friendly
Solo
Backpacking
Road trip
Sustainable
Off-the-beaten-path
Festivals
Wellness
Active
Volunteer
Educational
Exotic
Staycation*/