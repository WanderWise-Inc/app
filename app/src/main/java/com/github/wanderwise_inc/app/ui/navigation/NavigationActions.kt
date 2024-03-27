package com.github.wanderwise_inc.app.ui.navigation

import androidx.annotation.StringRes
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.R


object Route {
    const val SIGNIN = "SignIn"
    const val OVERVIEW = "Overview"
    const val MAP = "Map"
    const val ITINERARY = "Itinerary"
}

sealed class TopLevelDestination(
    val route: String,
    val icon: Int,
    @StringRes val textId: Int
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
}

val TOP_LEVEL_DESTINATIONS = listOf(
    TopLevelDestination.Map,
    TopLevelDestination.Overview
)

class NavigationActions (private val navController: NavHostController) {
    fun navigateTo(destination: TopLevelDestination) {
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

    fun goBack() {
        /* TODO */
    }
}