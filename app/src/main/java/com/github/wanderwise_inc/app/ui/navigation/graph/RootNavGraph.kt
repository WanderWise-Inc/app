package com.github.wanderwise_inc.app.ui.navigation.graph

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.ui.home.HomeScreen
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RootNavigationGraph(
    context: Context,
    profileViewModel: ProfileViewModel,
    mapViewModel: MapViewModel,
    bottomNavigationViewModel: BottomNavigationViewModel,
    imageRepository: ImageRepository,
    navController: NavHostController,
    firebaseAuth: FirebaseAuth
) {
  NavHost(
      navController = navController, route = Graph.ROOT, startDestination = Graph.AUTHENTICATION) {
        authNavGraph(context, profileViewModel, navController)
        composable(route = Graph.HOME) {
          HomeScreen(imageRepository, mapViewModel, bottomNavigationViewModel, profileViewModel,)
          HomeScreen(
              imageRepository = imageRepository,
              mapViewModel = mapViewModel,
              bottomNavigationViewModel = bottomNavigationViewModel,
              profileViewModel = profileViewModel,
              firebaseAuth = firebaseAuth)
        }
      }
}

object Graph {
  const val ROOT = "root_graph"
  const val AUTHENTICATION = "auth_graph"
  const val HOME = "home_graph"
}
