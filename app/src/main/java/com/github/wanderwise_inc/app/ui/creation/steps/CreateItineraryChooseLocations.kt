package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.ui.map.SearchLocation
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel

@Composable
fun CreateItineraryChooseLocations(
    createItineraryViewModel: CreateItineraryViewModel,
    navController: NavHostController = rememberNavController()
) {
  CreationLocationSearchNavGraph(createItineraryViewModel, navController)
}

@Composable
fun CreationLocationSearchNavGraph(
    createItineraryViewModel: CreateItineraryViewModel,
    navController: NavHostController
) {
  NavHost(navController = navController, startDestination = "ChooseLocationOverview") {
    composable("ChooseLocationOverview") {
      CreateItineraryMapWithSelector(createItineraryViewModel, navController)
    }
    composable("ChooseLocationSearch") { SearchLocation(createItineraryViewModel, navController) }
  }
}
