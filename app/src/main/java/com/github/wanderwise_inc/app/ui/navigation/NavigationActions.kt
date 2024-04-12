package com.github.wanderwise_inc.app.ui.navigation

import androidx.annotation.StringRes
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.R

object Route {
  const val SIGNIN = "SignIn"
  const val OVERVIEW = "Overview"
  const val LIKED = "Liked"
  const val SEARCH = "Search"
  const val MAP = "Map"
  const val PROFILE = "Profile"
}

object Tags {
  const val TRENDING = "Trending"
  const val ADVENTURE = "Adventure"
  const val SHOPPING = "Shopping"
  const val SIGHT_SEEING = "Sight seeing"
  const val DRINKS = "Drinks"
}

// VERY UGLY CODE -> MUST IMPROVE
sealed class Destination(val route: String, val icon: Int, @StringRes val textId: Int) {
  sealed class TopLevelDestination(route: String, icon: Int, textId: Int) :
      Destination(route, icon, textId) {
    data object Overview :
        TopLevelDestination(
            route = Route.OVERVIEW, icon = R.drawable.menu_icon, textId = R.string.overview_string)

    data object Liked :
        TopLevelDestination(
            route = Route.LIKED, icon = R.drawable.liked_icon, textId = R.string.liked_string)

    data object Search :
        TopLevelDestination(
            route = Route.SEARCH,
            icon = R.drawable.search_icon,
            textId = R.string.search_string,
        )

    data object Map :
        TopLevelDestination(
            route = Route.MAP, icon = R.drawable.map_icon, textId = R.string.map_string)

    data object Profile :
        TopLevelDestination(
            route = Route.PROFILE,
            icon = R.drawable.profile_icon,
            textId = R.string.profile_string)
  }

  sealed class OverviewTagLevelDestination(route: String, icon: Int, textId: Int) :
      Destination(route, icon, textId) {
    data object Trending :
        OverviewTagLevelDestination(
            route = Route.OVERVIEW + "/" + Tags.TRENDING,
            icon = R.drawable.trending_icon,
            textId = R.string.trending_string)

    data object Adventure :
        OverviewTagLevelDestination(
            route = Route.OVERVIEW + "/" + Tags.ADVENTURE,
            icon = R.drawable.adventure_icon,
            textId = R.string.adventure_string)

    data object Shopping :
        OverviewTagLevelDestination(
            route = Route.OVERVIEW + "/" + Tags.SHOPPING,
            icon = R.drawable.shopping_icon,
            textId = R.string.shopping_string)

    data object SightSeeing :
        OverviewTagLevelDestination(
            route = Route.OVERVIEW + "/" + Tags.SIGHT_SEEING,
            icon = R.drawable.sight_seeing_icon,
            textId = R.string.sight_seeing_string)

    data object Drinks :
        OverviewTagLevelDestination(
            route = Route.OVERVIEW + "/" + Tags.DRINKS,
            icon = R.drawable.drinks_icon,
            textId = R.string.drinks_string)
  }

  sealed class LikedTagLevelDestination(route: String, icon: Int, textId: Int) :
      Destination(route, icon, textId) {
    data object Adventure :
        LikedTagLevelDestination(
            route = Route.LIKED + "/" + Tags.ADVENTURE,
            icon = R.drawable.adventure_icon,
            textId = R.string.adventure_string)

    data object Shopping :
        LikedTagLevelDestination(
            route = Route.LIKED + "/" + Tags.SHOPPING,
            icon = R.drawable.shopping_icon,
            textId = R.string.shopping_string)

    data object SightSeeing :
        LikedTagLevelDestination(
            route = Route.LIKED + "/" + Tags.SIGHT_SEEING,
            icon = R.drawable.sight_seeing_icon,
            textId = R.string.sight_seeing_string)

    data object Drinks :
        LikedTagLevelDestination(
            route = Route.LIKED + "/" + Tags.DRINKS,
            icon = R.drawable.drinks_icon,
            textId = R.string.drinks_string)
  }
}

/*sealed class TopLevelDestination: Destination(
    val route: String,
    val icon: Int,
    @StringRes val textId: Int = 0
) {
    data object Overview: TopLevelDestination(
        route = Route.OVERVIEW,
        icon = R.drawable.menu_icon,
        textId = R.string.overview_string
    )
    data object Map: TopLevelDestination(
        route = Route.MAP,
        icon = R.drawable.map_icon,
        textId = R.string.map_string
    )
    data object Itineraries: TopLevelDestination(
        route = Route.ITINERARY,
        icon = R.drawable.itinerary_icon,
        textId = R.string.itinerary_string
    )
}*/

/*sealed class ItineraryLevelDestination(
    val route: String,
    @StringRes val textId: Int
) {
    data object Itineraries_personal: ItineraryLevelDestination(
        route = Route.ITINERARY_PERSONAL,
        textId = R.string.itinerary_personal_string // MY ITINERARIES ??
    )
    data object Itineraries_liked: ItineraryLevelDestination(
        route = Route.ITINERARY_LIKED,
        textId = R.string.itinerary_liked_string
    )
    data object Itineraries_popular: ItineraryLevelDestination(
        route = Route.ITINERARY_POPULAR,
        textId = R.string.itinerary_popular_string // TRENDING ??
    )
}*/

val TOP_LEVEL_DESTINATIONS =
    listOf(
        Destination.TopLevelDestination.Overview,
        Destination.TopLevelDestination.Liked,
        Destination.TopLevelDestination.Search,
        Destination.TopLevelDestination.Map,
        Destination.TopLevelDestination.Profile)

val OVERVIEW_LEVEL_DESTINATIONS =
    listOf(
        Destination.OverviewTagLevelDestination.Trending,
        Destination.OverviewTagLevelDestination.Adventure,
        Destination.OverviewTagLevelDestination.Shopping,
        Destination.OverviewTagLevelDestination.SightSeeing,
        Destination.OverviewTagLevelDestination.Drinks,
    )

val LIKED_LEVEL_DESTINATIONS =
    listOf(
        Destination.LikedTagLevelDestination.Adventure,
        Destination.LikedTagLevelDestination.Shopping,
        Destination.LikedTagLevelDestination.SightSeeing,
        Destination.LikedTagLevelDestination.Drinks,
    )

class NavigationActions(private val navController: NavHostController) {
  fun navigateTo(destination: Destination) {
    navController.navigate(destination.route) {
      // Pop up to the start destination of the graph to
      // avoid building up a large stack of destinations
      // on the back stack as users select items
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      // Avoid multiple copies of the same destination when
      // reselecting the same item
      launchSingleTop = true
      // Restore state when reselecting a previously selected item
      restoreState = true
    }
  }
}
