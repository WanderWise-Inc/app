package com.github.wanderwise_inc.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.ui.itinerary.ItineraryBanner
import com.github.wanderwise_inc.app.ui.navigation.BottomNavigationMenu
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.ui.navigation.graph.HomeNavGraph
import com.github.wanderwise_inc.app.viewmodel.HomeViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel,
    navController: NavHostController = rememberNavController(),
) {
  val navigationActions = NavigationActions(navController)
  Scaffold(topBar = {}, bottomBar = { BottomNavigationMenu(navigationActions) }) { innerPadding ->
    @Composable
    fun ScrollableList(viewModel: MapViewModel) {
      // Gets all itineraries
      val itineraries by viewModel.getAllPublicItineraries().collectAsState(initial = listOf())
      // Scrollable Column that only composes items on Screen
      LazyColumn(
          modifier =
              Modifier.fillMaxSize()
                  .background(MaterialTheme.colorScheme.background)
                  .padding(16.dp, 8.dp),
          verticalArrangement = Arrangement.spacedBy(15.dp),
      ) {
        items(itineraries, key = { it }) { itinerary ->
          ItineraryBanner(itinerary, onBannerClick = {}, onLikeButtonClick = { _, _ -> })
        }
      }
    }
    Box(modifier = Modifier.padding(innerPadding)) {
      HomeNavGraph(navController, mapViewModel, profileViewModel)
    }
  }
}
