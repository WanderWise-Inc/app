package com.github.wanderwise_inc.app.ui.navigation.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.demoSetup
import com.github.wanderwise_inc.app.ui.creation.CreationScreen
import com.github.wanderwise_inc.app.ui.list_itineraries.LikedScreen
import com.github.wanderwise_inc.app.ui.list_itineraries.OverviewScreen
import com.github.wanderwise_inc.app.ui.map.PreviewItineraryScreen
import com.github.wanderwise_inc.app.ui.navigation.Destination
import com.github.wanderwise_inc.app.ui.profile.ProfileScreen
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.NavigationItem
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun CreationNavGraph(
    navController: NavHostController,
    // mapViewModel: MapViewModel,
    // profileViewModel: ProfileViewModel,
    // bottomNavigationViewModel: BottomNavigationViewModel,
    // imageRepository: ImageRepository,
    // firebaseAuth: FirebaseAuth
) {
    NavHost(
        navController = navController,
        route = Graph.CREATION,
        startDestination = Destination.TopLevelDestination.Overview.route,
        // modifier = Modifier.padding(innerPadding)
    ) {
        composable(route = Destination.TopLevelDestination.Overview.route) {
            bottomNavigationViewModel.setSelected(NavigationItem.OVERVIEW.ordinal)
            OverviewScreen(mapViewModel, profileViewModel, navController, firebaseAuth)
        }
        composable(route = Destination.TopLevelDestination.Liked.route) {
            bottomNavigationViewModel.setSelected(NavigationItem.LIKED.ordinal)
            LikedScreen(mapViewModel, profileViewModel, navController, firebaseAuth)
        }
        composable(route = Destination.TopLevelDestination.Creation.route) {
            bottomNavigationViewModel.setSelected(NavigationItem.CREATE.ordinal)
            CreationScreen(mapViewModel)
        }
        composable(route = Destination.TopLevelDestination.Map.route) {
            bottomNavigationViewModel.setSelected(NavigationItem.MAP.ordinal)
            PreviewItineraryScreen(mapViewModel, profileViewModel)
        }
        composable(route = Destination.TopLevelDestination.Profile.route) {
            bottomNavigationViewModel.setSelected(NavigationItem.PROFILE.ordinal)
            ProfileScreen(mapViewModel, profileViewModel, imageRepository, navController, firebaseAuth)
        }
    }
}
