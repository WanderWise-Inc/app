package com.github.wanderwise_inc.app.ui.navigation.graph

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.wanderwise_inc.app.ui.creation.steps.CreationStepChooseDescriptionScreen
import com.github.wanderwise_inc.app.ui.creation.steps.CreationStepChooseLocationsScreen
import com.github.wanderwise_inc.app.ui.creation.steps.CreationStepChooseTagsScreen
import com.github.wanderwise_inc.app.ui.creation.steps.CreationStepPreview
import com.github.wanderwise_inc.app.ui.navigation.Destination.CreationStepsDestinations

@Composable
fun CreationNavGraph(
    navController: NavHostController,
    padding: PaddingValues,
    // mapViewModel: MapViewModel,
    // profileViewModel: ProfileViewModel,
    // bottomNavigationViewModel: BottomNavigationViewModel,
    // imageRepository: ImageRepository,
    // firebaseAuth: FirebaseAuth
) {
  NavHost(
      navController = navController,
      route = Graph.CREATION,
      startDestination = CreationStepsDestinations.ChooseLocations.route,
      modifier = Modifier.padding(padding)) {
        composable(CreationStepsDestinations.ChooseLocations.route) {
          CreationStepChooseLocationsScreen()
        }
        composable(CreationStepsDestinations.ChooseDescription.route) {
          CreationStepChooseDescriptionScreen()
        }
        composable(CreationStepsDestinations.ChooseTags.route) { CreationStepChooseTagsScreen() }
        composable(CreationStepsDestinations.Preview.route) { CreationStepPreview() }
      }
}
