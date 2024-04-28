package com.github.wanderwise_inc.app.ui.navigation

import androidx.annotation.StringRes
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.R

object Route { 
  const val SIGNIN = "SignIn"
  const val OVERVIEW = "Overview"
  const val LIKED = "Liked"
  const val CREATION = "Creation" 
  const val MAP = "Map"
  const val PROFILE = "Profile"
}

sealed class Destination(val route: String, val icon: Int, @StringRes val textId: Int) {
  sealed class TopLevelDestination(route: String, icon: Int, textId: Int) :
      Destination(route, icon, textId) {
    data object Overview :
        TopLevelDestination(
            route = Route.OVERVIEW, icon = R.drawable.menu_icon, textId = R.string.overview_string)

    data object Liked :
        TopLevelDestination(
            route = Route.LIKED, icon = R.drawable.liked_icon, textId = R.string.liked_string)

    data object Creation :
        TopLevelDestination(
            route = Route.CREATION, icon = R.drawable.pencil_icon, textId = R.string.creation_string,)

    data object Map :
        TopLevelDestination(
            route = Route.MAP, icon = R.drawable.map_icon, textId = R.string.map_string)

    data object Profile :
        TopLevelDestination(
            route = Route.PROFILE, icon = R.drawable.profile_icon, textId = R.string.profile_string)
  }
}

val TOP_LEVEL_DESTINATIONS =
    listOf(
        Destination.TopLevelDestination.Overview,
        Destination.TopLevelDestination.Liked,
        Destination.TopLevelDestination.Creation,
        Destination.TopLevelDestination.Map,
        Destination.TopLevelDestination.Profile)

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
