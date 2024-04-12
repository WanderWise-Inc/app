package com.github.wanderwise_inc.app.ui.navigation.graph

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.wanderwise_inc.app.ui.liked.categories.LikedAdventureScreen
import com.github.wanderwise_inc.app.ui.liked.categories.LikedDrinksScreen
import com.github.wanderwise_inc.app.ui.liked.categories.LikedShoppingScreen
import com.github.wanderwise_inc.app.ui.liked.categories.LikedSightSeeingScreen
import com.github.wanderwise_inc.app.ui.navigation.Destination.LikedTagLevelDestination
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

@Composable
fun LikedNavGraph(
    mapViewModel: MapViewModel,
    navController: NavHostController,
    innerPadding: PaddingValues
) {
  NavHost(
      navController = navController,
      route = Graph.LIKED,
      startDestination = LikedTagLevelDestination.Adventure.route,
      modifier = Modifier.padding(innerPadding)) {
        composable(route = LikedTagLevelDestination.Adventure.route) {
          LikedAdventureScreen(navController, mapViewModel)
        }
        composable(route = LikedTagLevelDestination.Shopping.route) {
          LikedShoppingScreen(navController, mapViewModel)
        }
        composable(route = LikedTagLevelDestination.SightSeeing.route) {
          LikedSightSeeingScreen(navController, mapViewModel)
        }
        composable(route = LikedTagLevelDestination.Drinks.route) {
          LikedDrinksScreen(navController, mapViewModel)
        }
      }
}
