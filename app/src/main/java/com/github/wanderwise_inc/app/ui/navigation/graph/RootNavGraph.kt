package com.github.wanderwise_inc.app.ui.navigation.graph

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.github.wanderwise_inc.app.ui.home.HomeScreen
import com.github.wanderwise_inc.app.viewmodel.HomeViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.UserViewModel

@Composable
fun RootNavigationGraph(
    userViewModel: UserViewModel,
    homeViewModel: HomeViewModel,
    mapViewModel: MapViewModel,
    navController: NavHostController
) {
    //val navigator = NavigationActions(navController)
    //var selectedScreen by remember { mutableStateOf(TOP_LEVEL_DESTINATIONS[1]) }
    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.AUTHENTICATION
    ) {
        authNavGraph(userViewModel, navController)
        composable(route = Graph.HOME) {
            HomeScreen(
                homeViewModel,
                mapViewModel
            )
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
    const val OVERVIEW = "overview_graph"
    const val LIKED = "liked_graph"
}