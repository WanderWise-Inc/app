package com.github.wanderwise_inc.app.ui.navigation.graph

import OverviewScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.wanderwise_inc.app.ui.liked.LikedScreen
import com.github.wanderwise_inc.app.ui.map.DummyPreviewItinerary
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
  NavHost(
      navController = navController,
      route = Graph.HOME,
      startDestination = TopLevelDestination.Overview.route,
      // modifier = Modifier.padding(innerPadding)
  ) {
    composable(route = TopLevelDestination.Overview.route) { OverviewScreen(mapViewModel) }
    composable(route = TopLevelDestination.Liked.route) {
      LikedScreen(mapViewModel, profileViewModel)
    }
    composable(route = TopLevelDestination.Search.route) { SearchScreen(mapViewModel) }
    composable(route = TopLevelDestination.Map.route) { DummyPreviewItinerary() }
    composable(route = TopLevelDestination.Profile.route) {
      ProfileScreen(mapViewModel, profileViewModel)
    }
    composable(route = TopLevelDestination.Profile.route) {
      ProfileScreen(mapViewModel, profileViewModel)
    }
  }
}
