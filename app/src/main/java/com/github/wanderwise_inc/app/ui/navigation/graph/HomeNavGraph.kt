package com.github.wanderwise_inc.app.ui.navigation.graph

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.ui.navigation.TopLevelDestination
import androidx.navigation.compose.composable

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = TopLevelDestination.Overview.route
    ) {
        composable(route = TopLevelDestination.Overview.route) {
            // TODO: add overview composable
        }
        composable(route = TopLevelDestination.Map.route) {
            // TODO: add Map composable
        }
    }
}