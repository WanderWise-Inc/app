package com.github.wanderwise_inc.app.ui.navigation.graph

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.ui.home.HomeScreen
import com.github.wanderwise_inc.app.viewmodel.HomeViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

@Composable
fun RootNavigationGraph(
    context: Context,
    profileViewModel: ProfileViewModel,
    homeViewModel: HomeViewModel,
    mapViewModel: MapViewModel,
    imageRepository: ImageRepository,
    navController: NavHostController
) {
  NavHost(
      navController = navController, route = Graph.ROOT, startDestination = Graph.AUTHENTICATION) {
        authNavGraph(context, profileViewModel, navController)
        composable(route = Graph.HOME) { HomeScreen(imageRepository, homeViewModel, mapViewModel, profileViewModel) }
      }
}

object Graph {
  const val ROOT = "root_graph"
  const val AUTHENTICATION = "auth_graph"
  const val HOME = "home_graph"
  const val OVERVIEW = "overview_graph"
  const val LIKED = "liked_graph"
}
