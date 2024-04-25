package com.github.wanderwise_inc.app.ui.navigation.graph

import OverviewScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.PlacesReader
import com.github.wanderwise_inc.app.ui.liked.LikedScreen
import com.github.wanderwise_inc.app.ui.map.PreviewItineraryScreen

import com.github.wanderwise_inc.app.ui.navigation.Destination.TopLevelDestination
import com.github.wanderwise_inc.app.ui.profile.ProfileScreen
import com.github.wanderwise_inc.app.ui.search.SearchScreen
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel,
) {
  val placeReader = PlacesReader(null)
  val locations = placeReader.readFromString()

  val itinerary =
      Itinerary(
          userUid = "",
          locations = locations,
          title = "San Francisco Bike Itinerary",
          tags = listOf(ItineraryTags.CULTURAL, ItineraryTags.NATURE, ItineraryTags.BUDGET),
          description = "A 3-day itinerary to explore the best of San Francisco on a bike.",
          visible = true)

  NavHost(
      navController = navController,
      route = Graph.HOME,
      startDestination = TopLevelDestination.Overview.route,
      // modifier = Modifier.padding(innerPadding)
  ) {
    composable(route = TopLevelDestination.Overview.route) {
      OverviewScreen(mapViewModel, profileViewModel)
    }
    composable(route = TopLevelDestination.Liked.route) {
      LikedScreen(mapViewModel, profileViewModel)
    }
    composable(route = TopLevelDestination.Search.route) { SearchScreen(mapViewModel) }
    composable(route = TopLevelDestination.Map.route) {
      PreviewItineraryScreen(itinerary, mapViewModel)
    }
    composable(route = TopLevelDestination.Profile.route) {
      ProfileScreen(mapViewModel, profileViewModel)
    }
    composable(route = TopLevelDestination.Profile.route) {
      ProfileScreen(mapViewModel, profileViewModel)
    }
  }
}
