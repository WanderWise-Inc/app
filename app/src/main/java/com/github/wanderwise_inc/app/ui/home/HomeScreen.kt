package com.github.wanderwise_inc.app.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.ui.navigation.BottomNavigationMenu
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.ui.navigation.graph.HomeNavGraph
import com.github.wanderwise_inc.app.viewmodel.HomeViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

@Composable
fun HomeScreen(
    imageRepository: ImageRepository,
    homeViewModel: HomeViewModel,
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel,
    navController: NavHostController = rememberNavController(),
) {
  val navigationActions = NavigationActions(navController)
  Scaffold(topBar = {}, bottomBar = { BottomNavigationMenu(navigationActions) }) { innerPadding ->
    Box(modifier = Modifier.padding(innerPadding)) {
      HomeNavGraph(mapViewModel, navController, profileViewModel, imageRepository)
    }
  }
}
