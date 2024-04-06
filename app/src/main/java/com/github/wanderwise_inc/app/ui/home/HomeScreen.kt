package com.github.wanderwise_inc.app.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.ui.navigation.BottomNavigationMenu
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.ui.navigation.TopLevelDestination
import com.github.wanderwise_inc.app.ui.navigation.graph.HomeNavGraph

@Composable
fun HomeScreen(
    navController: NavHostController = rememberNavController(),
) {
    val navigationActions = NavigationActions(navController)
    Scaffold(
        bottomBar = {
            /*
            BottomNavigationMenu(
                onTabSelect = { screen -> navigator.navigateTo(screen) },
                selectedItem = TopLevelDestination.Overview
            )
             */
            BottomNavigationMenu(navigationActions)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            HomeNavGraph(navController = navController, innerPadding = innerPadding)
        }
    }
}