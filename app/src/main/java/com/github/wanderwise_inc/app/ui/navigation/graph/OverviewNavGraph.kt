package com.github.wanderwise_inc.app.ui.navigation.graph

// import com.github.wanderwise_inc.app.ui.overview.categories.OverviewAdventureScreen
// import com.github.wanderwise_inc.app.ui.overview.categories.OverviewDrinksScreen
// import com.github.wanderwise_inc.app.ui.overview.categories.OverviewShoppingScreen
// import com.github.wanderwise_inc.app.ui.overview.categories.OverviewSightSeeingScreen
// import com.github.wanderwise_inc.app.ui.overview.categories.OverviewTrendingScreen
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.wanderwise_inc.app.ui.UnimplementedScreen
import com.github.wanderwise_inc.app.ui.navigation.Destination
import com.github.wanderwise_inc.app.ui.navigation.Destination.OverviewTagLevelDestination
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

@Composable
fun OverviewNavGraph(
    mapViewModel: MapViewModel,
    navController: NavHostController,
    innerPadding: PaddingValues
) {
  NavHost(
      navController = navController,
      route = Graph.OVERVIEW,
      startDestination = Destination.OverviewTagLevelDestination.Trending.route,
      modifier = Modifier.padding(innerPadding)) {
        composable(route = OverviewTagLevelDestination.Trending.route) {
          // OverviewTrendingScreen(navController, mapViewModel)
          UnimplementedScreen()
        }
        composable(route = OverviewTagLevelDestination.Adventure.route) {
          UnimplementedScreen()
          // OverviewAdventureScreen(navController, mapViewModel)
        }
        composable(route = OverviewTagLevelDestination.Shopping.route) {
          UnimplementedScreen()
          // OverviewShoppingScreen(navController, mapViewModel)
        }
        composable(route = OverviewTagLevelDestination.SightSeeing.route) {
          UnimplementedScreen()
          // OverviewSightSeeingScreen(navController, mapViewModel)
        }
        composable(route = OverviewTagLevelDestination.Drinks.route) {
          UnimplementedScreen()
          // OverviewDrinksScreen(navController, mapViewModel)
        }
      }
}
