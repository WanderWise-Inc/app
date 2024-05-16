package com.github.wanderwise_inc.app.ui.navigation.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.ui.creation.CreationScreen
import com.github.wanderwise_inc.app.ui.list_itineraries.LikedScreen
import com.github.wanderwise_inc.app.ui.list_itineraries.OverviewScreen
import com.github.wanderwise_inc.app.ui.map.PreviewItineraryScreen
import com.github.wanderwise_inc.app.ui.navigation.Destination
import com.github.wanderwise_inc.app.ui.navigation.Destination.TopLevelDestination
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.ui.profile.ProfileScreen
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.NavigationItem
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    itineraryViewModel: ItineraryViewModel,
    createItineraryViewModel: CreateItineraryViewModel,
    profileViewModel: ProfileViewModel,
    bottomNavigationViewModel: BottomNavigationViewModel,
    imageRepository: ImageRepository,
    firebaseAuth: FirebaseAuth
) {

  NavHost(
      navController = navController,
      route = Graph.HOME,
      startDestination = TopLevelDestination.Overview.route,
  ) {
    composable(route = TopLevelDestination.Overview.route) {
      bottomNavigationViewModel.setSelected(NavigationItem.OVERVIEW.ordinal)
      OverviewScreen(
          itineraryViewModel, profileViewModel, navController, firebaseAuth, imageRepository)
    }
    composable(route = TopLevelDestination.Liked.route) {
      bottomNavigationViewModel.setSelected(NavigationItem.LIKED.ordinal)
      LikedScreen(
          itineraryViewModel, profileViewModel, navController, firebaseAuth, imageRepository)
    }
    composable(route = TopLevelDestination.Creation.route) {
      bottomNavigationViewModel.setSelected(NavigationItem.CREATE.ordinal)
      CreationScreen(
          createItineraryViewModel,
          profileViewModel,
          onFinished = {
            NavigationActions(navController).navigateTo(Destination.TopLevelDestination.Profile)
          },
          firebaseAuth = firebaseAuth,
          imageRepository = imageRepository)
    }
    composable(route = TopLevelDestination.Map.route) {
      bottomNavigationViewModel.setSelected(NavigationItem.MAP.ordinal)
      PreviewItineraryScreen(itineraryViewModel, profileViewModel)
    }
    composable(route = TopLevelDestination.Profile.route) {
      bottomNavigationViewModel.setSelected(NavigationItem.PROFILE.ordinal)
      ProfileScreen(
          itineraryViewModel, profileViewModel, imageRepository, navController, firebaseAuth)
    }
  }
}
