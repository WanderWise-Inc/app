package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.creation.CreationFinishButton
import com.github.wanderwise_inc.app.ui.navigation.Destination.CreationPreviewOptionsDestinations
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.ui.navigation.graph.Graph
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

@Composable
fun CreationStepPreview(navController: NavHostController = rememberNavController(),
                        createItineraryViewModel: CreateItineraryViewModel,
                        profileViewModel: ProfileViewModel,
                        onFinished: () -> Unit,) {
  var selected by remember { mutableStateOf(PreviewOption.Banner) }
  val navigator = NavigationActions(navController)

  Scaffold(
      floatingActionButton = {
        FloatingActionButton(
            onClick = {
              if (selected == PreviewOption.Banner) {
                selected = PreviewOption.Itinerary
                navigator.navigateTo(CreationPreviewOptionsDestinations.PreviewItinerary)
              } else {
                selected = PreviewOption.Banner
                navigator.navigateTo(CreationPreviewOptionsDestinations.PreviewBanner)
              }
            },
            modifier = Modifier.testTag(TestTags.CREATION_SCREEN_PREVIEW_BUTTON)) {
              Icon(
                  painter =
                      painterResource(
                          id =
                              if (selected == PreviewOption.Banner) {
                                CreationPreviewOptionsDestinations.PreviewBanner.icon
                              } else {
                                CreationPreviewOptionsDestinations.PreviewItinerary.icon
                              }),
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier
                      .size(30.dp)
                      .padding(2.dp))
            }
      },
      modifier = Modifier.testTag(TestTags.CREATION_SCREEN_PREVIEW)) { padding ->
      Box(modifier = Modifier.padding(16.dp)) {
          Card(onClick = { onFinished() },
              colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.secondaryContainer)
          ){
              Row(){
                  Icon(Icons.Outlined.CheckBox, "finished")
                  Text("Finish")
              }
          }
      }

        CreationStepPreviewNav(navController, padding, createItineraryViewModel, profileViewModel)
      }
}

@Composable
fun CreationStepPreviewNav(navController: NavHostController, padding: PaddingValues, createItineraryViewModel: CreateItineraryViewModel, profileViewModel: ProfileViewModel) {
  NavHost(
      navController = navController,
      route = Graph.CREATION_PREVIEW,
      startDestination = CreationPreviewOptionsDestinations.PreviewBanner.route,
      modifier = Modifier.padding(padding)) {
        composable(route = CreationPreviewOptionsDestinations.PreviewBanner.route) {
          CreationStepPreviewBanner(createItineraryViewModel = createItineraryViewModel)
        }
        composable(route = CreationPreviewOptionsDestinations.PreviewItinerary.route) {
          CreationStepPreviewItinerary(createItineraryViewModel = createItineraryViewModel, profileViewModel = profileViewModel)
        }
      }
}

enum class PreviewOption {
  Banner,
  Itinerary
}
