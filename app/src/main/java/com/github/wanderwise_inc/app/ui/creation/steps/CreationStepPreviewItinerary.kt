package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.map.PreviewItineraryScreen
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

/**
 * @brief preview the itinerary with the map
 */
@Composable
fun CreationStepPreviewItinerary(
    createItineraryViewModel: CreateItineraryViewModel,
    imageRepository: ImageRepository,
    profileViewModel: ProfileViewModel,
    navController: NavHostController
) {
  // this will not be permanent, will need to add logic later.
  Box(modifier = Modifier.fillMaxSize().testTag(TestTags.CREATION_SCREEN_PREVIEW_ITINERARY)) {
    PreviewItineraryScreen(
        itineraryViewModel = createItineraryViewModel,
        profileViewModel = profileViewModel,
        imageRepository = imageRepository,
        navController = navController,
        creationMode = true)
  }
}
