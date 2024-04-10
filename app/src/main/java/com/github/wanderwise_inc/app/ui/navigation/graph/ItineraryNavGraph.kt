package com.github.wanderwise_inc.app.ui.navigation.graph

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.wanderwise_inc.app.ui.itineraries.ItineraryScreen
import com.github.wanderwise_inc.app.ui.itineraries.categories.LikedItinerariesScreen
import com.github.wanderwise_inc.app.ui.itineraries.categories.PersonalItinerariesScreen
import com.github.wanderwise_inc.app.ui.itineraries.categories.PopularItinerariesScreen
import com.github.wanderwise_inc.app.ui.map.MapScreen
import com.github.wanderwise_inc.app.ui.navigation.Destination.ItineraryLevelDestination
import com.github.wanderwise_inc.app.ui.overview.OverviewScreen
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

@Composable
fun ItineraryNavGraph(
    mapViewModel: MapViewModel,
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        route = Graph.ITINERARY,
        startDestination = ItineraryLevelDestination.Itineraries_personal.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(route = ItineraryLevelDestination.Itineraries_personal.route) {
            PersonalItinerariesScreen()
        }
        composable(route = ItineraryLevelDestination.Itineraries_liked.route) {
            LikedItinerariesScreen()
        }
        composable(route = ItineraryLevelDestination.Itineraries_popular.route) {
            PopularItinerariesScreen()
        }
    }
}
