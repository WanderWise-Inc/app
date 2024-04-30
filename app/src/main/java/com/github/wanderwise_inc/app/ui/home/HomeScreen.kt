package com.github.wanderwise_inc.app.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.ui.navigation.BottomNavigationMenu
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.ui.navigation.graph.HomeNavGraph
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    imageRepository: ImageRepository,
    mapViewModel: MapViewModel,
    bottomNavigationViewModel: BottomNavigationViewModel,
    profileViewModel: ProfileViewModel,
    navController: NavHostController = rememberNavController(),
    firebaseAuth: FirebaseAuth,
) {
  val navigationActions = NavigationActions(navController)
  Scaffold(
      topBar = {},
      bottomBar = { BottomNavigationMenu(navigationActions, bottomNavigationViewModel) }) {
          innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
          HomeNavGraph(
              navController,
              mapViewModel,
              profileViewModel,
              bottomNavigationViewModel,
              imageRepository,
              firebaseAuth)
        }
      }
}
