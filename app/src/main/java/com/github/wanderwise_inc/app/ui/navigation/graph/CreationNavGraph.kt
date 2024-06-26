package com.github.wanderwise_inc.app.ui.navigation.graph

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.ui.creation.steps.CreateItineraryChooseLocations
import com.github.wanderwise_inc.app.ui.creation.steps.CreationStepChooseDescriptionScreen
import com.github.wanderwise_inc.app.ui.creation.steps.CreationStepChooseTagsScreen
import com.github.wanderwise_inc.app.ui.creation.steps.CreationStepPreview
import com.github.wanderwise_inc.app.ui.navigation.Destination.CreationStepsDestinations
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

@Composable
fun CreationNavGraph(
    createItineraryViewModel: CreateItineraryViewModel,
    navController: NavHostController,
    padding: PaddingValues,
    profileViewModel: ProfileViewModel,
    onFinished: () -> Unit,
    imageRepository: ImageRepository,
) {
  NavHost(
      navController = navController,
      route = Graph.CREATION,
      startDestination = CreationStepsDestinations.ChooseLocations.route,
      modifier = Modifier.padding(padding)) {
        composable(CreationStepsDestinations.ChooseLocations.route) {
          CreateItineraryChooseLocations(createItineraryViewModel)
        }
        composable(CreationStepsDestinations.ChooseDescription.route) {
          CreationStepChooseDescriptionScreen(createItineraryViewModel)
        }
        composable(CreationStepsDestinations.ChooseTags.route) {
          CreationStepChooseTagsScreen(createItineraryViewModel, imageRepository)
        }
        composable(CreationStepsDestinations.Preview.route) {
          CreationStepPreview(
              createItineraryViewModel = createItineraryViewModel,
              profileViewModel = profileViewModel,
              onFinished = onFinished,
              imageRepository = imageRepository)
        }
      }
}
