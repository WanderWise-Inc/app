package com.github.wanderwise_inc.app.ui.home

import android.content.Context
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.ui.navigation.BottomNavigationMenu
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.ui.navigation.TopLevelDestination
import com.github.wanderwise_inc.app.ui.navigation.graph.HomeNavGraph
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.github.wanderwise_inc.app.viewmodel.UserViewModel

@Composable
fun HomeScreen(
    userViewModel: UserViewModel,
    context:Context,
    navController: NavHostController = rememberNavController()
) {
    val navigator = NavigationActions(navController)
    Scaffold(
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = {screen -> navigator.navigateTo(screen) },
                selectedItem = TopLevelDestination.Overview
            )
        }
    ) {innerPadding ->

        HomeNavGraph(navController = navController, innerPadding = innerPadding, context, userViewModel)
    }
}