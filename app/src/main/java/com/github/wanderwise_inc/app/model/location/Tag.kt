package com.github.wanderwise_inc.app.model.location

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Diversity3
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FoodBank
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.Hiking
import androidx.compose.material.icons.outlined.Museum
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.ui.graphics.vector.ImageVector

/** Represents a tag Used both for storage and for display */
typealias Tag = String

/** @brief Search categories displayed on the top bar. */
data class SearchCategory(val tag: Tag, val icon: ImageVector)

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
  const val SHOPPING = "Shopping"
  const val NATURE = "Nature"
  const val PHOTOGRAPHY = "Photography"
  const val RELAXATION = "Relaxation"
  const val ROMANCE = "Romance"
  const val RURAL = "Rural"
  const val SOCIAL = "Social"
  const val URBAN = "Urban"
  const val WELLNESS = "Wellness"
  const val WILDLIFE = "Wildlife"

  const val ALL = "All"

  fun toList(): List<String> {
    return listOf(
        ACTIVE,
        ADVENTURE,
        BUDGET,
        CULTURAL,
        FOOD,
        FOODIE,
        SHOPPING,
        NATURE,
        PHOTOGRAPHY,
        RELAXATION,
        ROMANCE,
        RURAL,
        SOCIAL,
        URBAN,
        WELLNESS,
        WILDLIFE,
    )
  }

  fun toSearchCategoryList(): List<SearchCategory> {
    return listOf(
        SearchCategory(ALL, Icons.Outlined.Public),
        SearchCategory(ADVENTURE, Icons.Outlined.Hiking),
        SearchCategory(SHOPPING, Icons.Outlined.ShoppingBag),
        SearchCategory(PHOTOGRAPHY, Icons.Outlined.PhotoCamera),
        SearchCategory(FOODIE, Icons.Outlined.Restaurant),
        SearchCategory(ROMANCE, Icons.Outlined.FavoriteBorder),
        SearchCategory(ACTIVE, Icons.AutoMirrored.Filled.DirectionsRun),
        SearchCategory(BUDGET, Icons.Outlined.Savings),
        SearchCategory(CULTURAL, Icons.Outlined.Museum),
        SearchCategory(FOOD, Icons.Outlined.FoodBank),
        SearchCategory(NATURE, Icons.Outlined.Forest),
        SearchCategory(RELAXATION, Icons.Outlined.SelfImprovement),
        SearchCategory(RURAL, Icons.Outlined.Agriculture),
        SearchCategory(SOCIAL, Icons.Outlined.Diversity3),
        SearchCategory(URBAN, Icons.Outlined.Apartment),
        SearchCategory(WELLNESS, Icons.Outlined.Spa),
        SearchCategory(WILDLIFE, Icons.Outlined.Pets),
    )
  }
}
