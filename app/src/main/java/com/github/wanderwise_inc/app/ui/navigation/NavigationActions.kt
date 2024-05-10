package com.github.wanderwise_inc.app.ui.navigation

import androidx.annotation.StringRes
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.R

object TopLevelRoute {
  const val SIGNIN = "SignIn"
  const val OVERVIEW = "Overview"
  const val LIKED = "Liked"
  const val CREATION = "Creation"
  const val MAP = "Map"
  const val PROFILE = "Profile"
}

object CreationStepsRoute {
  const val PREVIEW = "Preview"
  const val LOCATIONS = "Locations"
  const val DESCRIPTION = "Description"
  const val TAGS = "Tags"
}

object CreationPreviewOptions {
  const val PREVIEW_BANNER = "Banner"
  const val PREVIEW_ITINERARY = "Itinerary"
}

sealed class Destination(val route: String, val icon: Int, @StringRes val textId: Int) {
  sealed class TopLevelDestination(route: String, icon: Int, textId: Int) :
      Destination(route, icon, textId) {
    data object Overview :
        TopLevelDestination(
            route = TopLevelRoute.OVERVIEW,
            icon = R.drawable.menu_icon,
            textId = R.string.overview_string)

    data object Liked :
        TopLevelDestination(
            route = TopLevelRoute.LIKED,
            icon = R.drawable.liked_icon,
            textId = R.string.liked_string)

    data object Creation :
        TopLevelDestination(
            route = TopLevelRoute.CREATION,
            icon = R.drawable.add_icon,
            textId = R.string.creation_string,
        )

    data object Map :
        TopLevelDestination(
            route = TopLevelRoute.MAP, icon = R.drawable.map_icon, textId = R.string.map_string)

    data object Profile :
        TopLevelDestination(
            route = TopLevelRoute.PROFILE,
            icon = R.drawable.profile_icon,
            textId = R.string.profile_string)
  }

  sealed class CreationStepsDestinations(route: String, icon: Int, textId: Int) :
      Destination("Creation/$route", icon, textId) {
    data object ChooseLocations :
        CreationStepsDestinations(
            route = CreationStepsRoute.LOCATIONS,
            icon = R.drawable.location_icon,
            textId = R.string.locations_string)

    data object ChooseDescription :
        CreationStepsDestinations(
            route = CreationStepsRoute.DESCRIPTION,
            icon = R.drawable.chat_icon,
            textId = R.string.description_string)

    data object ChooseTags :
        CreationStepsDestinations(
            route = CreationStepsRoute.TAGS,
            icon = R.drawable.tag_icon,
            textId = R.string.tags_string,
        )

    data object Preview :
        CreationStepsDestinations(
            route = CreationStepsRoute.PREVIEW,
            icon = R.drawable.tick_icon,
            textId = R.string.preview_string)
  }

  sealed class CreationPreviewOptionsDestinations(route: String, icon: Int, textId: Int) :
      Destination("Creation/Preview/$route", icon, textId) {
    data object PreviewBanner :
        CreationStepsDestinations(
            route = CreationPreviewOptions.PREVIEW_BANNER,
            icon = R.drawable.map_icon,
            textId = R.string.preview_string)

    data object PreviewItinerary :
        CreationStepsDestinations(
            route = CreationPreviewOptions.PREVIEW_ITINERARY,
            icon = R.drawable.home_icon,
            textId = R.string.preview_string)
  }
}

val TOP_LEVEL_DESTINATIONS =
    listOf(
        Destination.TopLevelDestination.Overview,
        Destination.TopLevelDestination.Liked,
        Destination.TopLevelDestination.Creation,
        Destination.TopLevelDestination.Map,
        Destination.TopLevelDestination.Profile)

val CREATION_STEPS_DESTINATIONS =
    listOf(
        Destination.CreationStepsDestinations.ChooseLocations,
        Destination.CreationStepsDestinations.ChooseDescription,
        Destination.CreationStepsDestinations.ChooseTags,
        Destination.CreationStepsDestinations.Preview,
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
