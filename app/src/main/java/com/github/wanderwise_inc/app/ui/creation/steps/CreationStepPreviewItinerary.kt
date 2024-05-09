package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.map.PreviewItineraryScreen
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

@Composable
fun CreationStepPreviewItinerary(
    createItineraryViewModel: CreateItineraryViewModel,
    profileViewModel: ProfileViewModel
) {
  // this will not be permanent, will need to add logic later.
  createItineraryViewModel.setFocusedItinerary(FakeItinerary.SWITZERLAND)
  Box(modifier = Modifier.fillMaxSize().testTag(TestTags.CREATION_SCREEN_PREVIEW_ITINERARY)) {
    PreviewItineraryScreen(
        itineraryViewModel = createItineraryViewModel, profileViewModel = profileViewModel)
  }
}

// modifier = Modifier.testTag(TestTags.CREATION_SCREEN_PREVIEW_ITINERARY)