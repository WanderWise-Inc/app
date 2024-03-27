package com.github.wanderwise_inc.app.ui.navigation.graph

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.github.wanderwise_inc.app.ui.home.HomeScreen

@Composable
fun RootNavigationGraph(
    navController: NavHostController
) {
    //val navigator = NavigationActions(navController)
    //var selectedScreen by remember { mutableStateOf(TOP_LEVEL_DESTINATIONS[1]) }
    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.HOME
    ) {
        authNavGraph(navController = navController)
        composable(route = Graph.HOME) {
            HomeScreen()
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
}