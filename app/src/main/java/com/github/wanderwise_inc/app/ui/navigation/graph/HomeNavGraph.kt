package com.github.wanderwise_inc.app.ui.navigation.graph

import OverviewScreen
import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.ui.navigation.Destination.TopLevelDestination
import androidx.navigation.compose.composable
import com.github.wanderwise_inc.app.ui.liked.LikedScreen
import com.github.wanderwise_inc.app.ui.map.MapScreen
import com.github.wanderwise_inc.app.ui.profile.ProfileScreen
import com.github.wanderwise_inc.app.ui.search.SearchScreen
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.UserViewModel

@Composable
fun HomeNavGraph(
    mapViewModel: MapViewModel,
    navController: NavHostController,
    // innerPadding: PaddingValues,
    // context : Context,
    // userViewModel: UserViewModel
) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = TopLevelDestination.Overview.route,
        //modifier = Modifier.padding(innerPadding)
    ) {
        composable(route = TopLevelDestination.Overview.route) {
            OverviewScreen(mapViewModel)
        }
        composable(route = TopLevelDestination.Liked.route) {
            LikedScreen(mapViewModel)
        }
        composable(route = TopLevelDestination.Search.route) {
            SearchScreen(mapViewModel)
        }
        composable(route = TopLevelDestination.Map.route) {
            MapScreen()
        }
        composable(route = TopLevelDestination.Profile.route) {
            ProfileScreen(mapViewModel)
        }
    }
}