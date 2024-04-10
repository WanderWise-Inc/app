package com.github.wanderwise_inc.app.ui.itineraries

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.ui.navigation.TopNavigationMenu
import com.github.wanderwise_inc.app.ui.navigation.graph.ItineraryNavGraph
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

@Composable
fun ItineraryScreen(
    mapViewModel : MapViewModel,
    navController: NavHostController = rememberNavController()
) {
    //Text(text = "Welcome to the itinerary screen", Modifier.testTag("Itinerary Screen"))
    val navigator = NavigationActions(navController)
    Scaffold(
        topBar = {
            TopNavigationMenu(navigator)
        }
    ) {innerPadding ->  
        ItineraryNavGraph(
            mapViewModel = mapViewModel,
            navController = navController,
            innerPadding = innerPadding
        )       
    }
}