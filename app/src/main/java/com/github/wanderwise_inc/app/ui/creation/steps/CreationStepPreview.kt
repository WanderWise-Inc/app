package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckBox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.navigation.Destination.CreationPreviewOptionsDestinations
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.ui.navigation.graph.Graph
import com.github.wanderwise_inc.app.ui.popup.HintPopup
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import java.lang.StringBuilder

@Composable
fun CreationStepPreview(
    navController: NavHostController = rememberNavController(),
    createItineraryViewModel: CreateItineraryViewModel,
    profileViewModel: ProfileViewModel,
    onFinished: () -> Unit,
) {
  var selected by remember { mutableStateOf(PreviewOption.Banner) }
  val navigator = NavigationActions(navController)

  var notSetValues by remember { mutableStateOf(listOf<String>()) }

  createItineraryViewModel.setFocusedItinerary(createItineraryViewModel.getNewItinerary()?.build())

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
                  modifier = Modifier.size(30.dp).padding(2.dp))
            }
      },
      modifier = Modifier.testTag(TestTags.CREATION_SCREEN_PREVIEW)) { padding ->
        Box() {
          CreationStepPreviewNav(navController, padding, createItineraryViewModel, profileViewModel)

          ExtendedFloatingActionButton(
              onClick = {
                notSetValues = createItineraryViewModel.notSetValues()
                if (notSetValues.isEmpty()) {
                  createItineraryViewModel.uploadNewItinerary()
                  onFinished()
                }
              },
              icon = {
                Icon(
                    Icons.Outlined.CheckBox,
                    contentDescription = "finished",
                    modifier = Modifier.size(32.dp))
              },
              text = { Text(text = "Finish", fontSize = 24.sp) },
              containerColor = MaterialTheme.colorScheme.secondaryContainer,
              contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
              modifier =
                  Modifier.align(Alignment.TopCenter)
                      .padding(12.dp)
                      .testTag(TestTags.CREATION_FINISH_BUTTON))

          if (notSetValues.isNotEmpty()) {
            PopUpValuesToSet(attributes = notSetValues) { notSetValues = listOf() }
          }
        }
      }
}

@Composable
fun CreationStepPreviewNav(
    navController: NavHostController,
    padding: PaddingValues,
    createItineraryViewModel: CreateItineraryViewModel,
    profileViewModel: ProfileViewModel
) {
  NavHost(
      navController = navController,
      route = Graph.CREATION_PREVIEW,
      startDestination = CreationPreviewOptionsDestinations.PreviewBanner.route,
      modifier = Modifier.padding(padding)) {
        composable(route = CreationPreviewOptionsDestinations.PreviewBanner.route) {
          CreationStepPreviewBanner(createItineraryViewModel = createItineraryViewModel)
        }
        composable(route = CreationPreviewOptionsDestinations.PreviewItinerary.route) {
          CreationStepPreviewItinerary(
              createItineraryViewModel = createItineraryViewModel,
              profileViewModel = profileViewModel)
        }
      }
}

enum class PreviewOption {
  Banner,
  Itinerary
}

@Composable
fun PopUpValuesToSet(attributes: List<String>, onDismiss: () -> Unit) {
  val message = StringBuilder("Please fill in the following fields before submitting: ")
  for (a in attributes) {
    message.append(a).append(", ")
  }
  HintPopup(message = message.toString()) { onDismiss() }
}
