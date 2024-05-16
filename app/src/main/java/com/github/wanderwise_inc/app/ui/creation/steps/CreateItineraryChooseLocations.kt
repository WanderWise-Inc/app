package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.ui.navigation.graph.CreationLocationSearchNavGraph
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel

@Composable
fun CreateItineraryChooseLocations(
    createItineraryViewModel: CreateItineraryViewModel,
    navController: NavHostController = rememberNavController()
) {
    CreationLocationSearchNavGraph(createItineraryViewModel, navController)
}