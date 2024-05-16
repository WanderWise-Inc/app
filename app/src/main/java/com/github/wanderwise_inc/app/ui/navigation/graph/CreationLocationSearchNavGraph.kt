package com.github.wanderwise_inc.app.ui.navigation.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.wanderwise_inc.app.ui.creation.steps.CreateItineraryMapWithSelector
import com.github.wanderwise_inc.app.ui.map.SearchLocation
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel

@Composable
fun CreationLocationSearchNavGraph(
    createItineraryViewModel: CreateItineraryViewModel,
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = "ChooseLocationOverview") {
        composable("ChooseLocationOverview") {
            CreateItineraryMapWithSelector(createItineraryViewModel, navController)
        }
        composable("ChooseLocationSearch") {
            SearchLocation(createItineraryViewModel, navController)
        }
    }
}