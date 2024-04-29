package com.github.wanderwise_inc.app.ui.navigation.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.wanderwise_inc.app.PREVIEW_ITINERARY_DEMO_UID
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.demoSetup
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.ui.list_itineraries.LikedScreen
import com.github.wanderwise_inc.app.ui.list_itineraries.OverviewScreen
import com.github.wanderwise_inc.app.ui.map.PreviewItineraryScreen
import com.github.wanderwise_inc.app.ui.navigation.Destination.TopLevelDestination
import com.github.wanderwise_inc.app.ui.profile.ProfileScreen
import com.github.wanderwise_inc.app.ui.search.SearchScreen
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.NavigationItem
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel,
    bottomNavigationViewModel: BottomNavigationViewModel,
    imageRepository: ImageRepository,
) {

  // BEGIN DEMO SETUP
  demoSetup(mapViewModel, profileViewModel)
  var itinerary: Itinerary? = null
  runBlocking {
    itinerary = mapViewModel.getItineraryFromUids(listOf(PREVIEW_ITINERARY_DEMO_UID)).first()[0]
  }
  // END DEMO SETUP

  NavHost(
      navController = navController,
      route = Graph.HOME,
      startDestination = TopLevelDestination.Overview.route,
      // modifier = Modifier.padding(innerPadding)
  ) {
    composable(route = TopLevelDestination.Overview.route) {
        bottomNavigationViewModel.setSelected(NavigationItem.OVERVIEW.ordinal)
      OverviewScreen(mapViewModel, profileViewModel, navController)
    }
    composable(route = TopLevelDestination.Liked.route) {
        bottomNavigationViewModel.setSelected(NavigationItem.LIKED.ordinal)
      LikedScreen(mapViewModel, profileViewModel, navController)
    }
    composable(route = TopLevelDestination.Search.route) {
        bottomNavigationViewModel.setSelected(NavigationItem.CREATE.ordinal)
        SearchScreen(mapViewModel) }
    composable(route = TopLevelDestination.Map.route) {
        bottomNavigationViewModel.setSelected(NavigationItem.MAP.ordinal)
      PreviewItineraryScreen(itinerary!!, mapViewModel, profileViewModel)
    }
    composable(route = TopLevelDestination.Profile.route) {
        bottomNavigationViewModel.setSelected(NavigationItem.PROFILE.ordinal)
      ProfileScreen(mapViewModel, profileViewModel, imageRepository, navController)
    }
  }
}
