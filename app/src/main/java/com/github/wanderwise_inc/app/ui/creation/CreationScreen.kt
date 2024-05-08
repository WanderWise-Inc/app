package com.github.wanderwise_inc.app.ui.creation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.navigation.ItineraryCreationNavigationMenu
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.ui.navigation.graph.CreationNavGraph
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

@Composable
fun CreationScreen(
    navController: NavHostController = rememberNavController(),
    mapViewModel: MapViewModel
) {
  Scaffold(
      topBar = {
        ItineraryCreationNavigationMenu(navigationActions = NavigationActions(navController))
      },
      modifier = Modifier.testTag(TestTags.CREATION_SCREEN)) { padding ->
        CreationNavGraph(navController = navController, padding = padding)
      }
  /*Text(
  text = "Welcome, here you will be able to create a new itinerary",
  modifier = Modifier.testTag(TestTags.CREATION_SCREEN))*/
}
