package com.github.wanderwise_inc.app.ui.navigation.graph

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.ui.navigation.Destination.TopLevelDestination
import androidx.navigation.compose.composable
import com.github.wanderwise_inc.app.ui.itineraries.ItineraryScreen
import com.github.wanderwise_inc.app.ui.map.MapScreen
import com.github.wanderwise_inc.app.ui.overview.OverviewScreen
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

@Composable
fun HomeNavGraph(
    mapViewModel: MapViewModel,
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = TopLevelDestination.Overview.route,
        //modifier = Modifier.padding(innerPadding)
    ) {
        composable(route = TopLevelDestination.Overview.route) {
            OverviewScreen()
        }
        composable(route = TopLevelDestination.Itineraries.route) {
            ItineraryScreen(
                mapViewModel
            )
        }
        composable(route = TopLevelDestination.Map.route) {
            MapScreen()
        }
    }
}