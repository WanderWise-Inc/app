package com.github.wanderwise_inc.app.ui.liked

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.ui.navigation.Route
import com.github.wanderwise_inc.app.ui.navigation.TopNavigationMenu
import com.github.wanderwise_inc.app.ui.navigation.graph.LikedNavGraph
import com.github.wanderwise_inc.app.ui.navigation.graph.OverviewNavGraph
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

@Composable
fun LikedScreen(
    mapViewModel : MapViewModel,
    navController: NavHostController = rememberNavController()
) {
    //Text(text = "Welcome to the itinerary screen", Modifier.testTag("Itinerary Screen"))
    val navigator = NavigationActions(navController)
    Scaffold(
        topBar = {
            TopNavigationMenu(navigator, Route.LIKED)
        }
    ) {innerPadding ->
        LikedNavGraph(
            mapViewModel = mapViewModel,
            navController = navController,
            innerPadding = innerPadding
        )
    }
}