package com.github.wanderwise_inc.app.ui.navigation

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.R


object Route {
    const val SIGNIN = "SignIn"
    const val OVERVIEW = "Overview"
    const val MAP = "Map"
    const val ITINERARY = "Itinerary"
    const val ITINERARY_PERSONAL = "Itinerary/Personal"
    const val ITINERARY_LIKED = "Itinerary/Liked"
    const val ITINERARY_POPULAR = "Itinerary/Popular"
}

sealed class Destination(
    val name: String,
    val route: String,
    @StringRes val textId: Int
) {
    sealed class TopLevelDestination(
        val icon: Int, name: String, route: String, textId: Int
    ): Destination(name, route, textId) {
        data object Overview: TopLevelDestination(
            name = getNameFromRoute(Route.OVERVIEW),
            route = Route.OVERVIEW,
            icon = R.drawable.menu_icon,
            textId = R.string.overview_string
        )
        data object Map: TopLevelDestination(
            name = getNameFromRoute(Route.MAP),
            route = Route.MAP,
            icon = R.drawable.map_icon,
            textId = R.string.map_string
        )
        data object Itineraries: TopLevelDestination(
            name = getNameFromRoute(Route.ITINERARY),
            route = Route.ITINERARY,
            icon = R.drawable.itinerary_icon,
            textId = R.string.itinerary_string
        )
    }

    sealed class ItineraryLevelDestination(
        val icon: Int, name: String, route: String, textId: Int
    ): Destination(name, route, textId) {
        data object Itineraries_personal: ItineraryLevelDestination(
            name = getNameFromRoute(Route.ITINERARY_PERSONAL),
            route = Route.ITINERARY_PERSONAL,
            icon = R.drawable.person_icon,
            textId = R.string.itinerary_personal_string // MY ITINERARIES ??
        )
        data object Itineraries_liked: ItineraryLevelDestination(
            name = getNameFromRoute(Route.ITINERARY_LIKED),
            route = Route.ITINERARY_LIKED,
            icon = R.drawable.liked_icon,
            textId = R.string.itinerary_liked_string
        )
        data object Itineraries_popular: ItineraryLevelDestination(
            name = getNameFromRoute(Route.ITINERARY_POPULAR),
            route = Route.ITINERARY_POPULAR,
            icon = R.drawable.popular_icon,
            textId = R.string.itinerary_popular_string // TRENDING ??
        )
    }
}

fun getNameFromRoute(route: String): String {
    return route.split("/").last()
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

val TOP_LEVEL_DESTINATIONS = listOf(
    Destination.TopLevelDestination.Overview,
    Destination.TopLevelDestination.Itineraries,
    Destination.TopLevelDestination.Map,
)

val ITINERARY_LEVEL_DESTINATIONS = listOf(
    Destination.ItineraryLevelDestination.Itineraries_personal,
    Destination.ItineraryLevelDestination.Itineraries_liked,
    Destination.ItineraryLevelDestination.Itineraries_popular,
)

class NavigationActions (private val navController: NavHostController) {
    fun navigateTo(destination: Destination) {
        navController.navigate(destination.route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
}