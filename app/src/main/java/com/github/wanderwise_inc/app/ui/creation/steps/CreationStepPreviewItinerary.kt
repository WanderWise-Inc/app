package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.map.PreviewItineraryScreen
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

@Composable
fun CreationStepPreviewItinerary(createItineraryViewModel: CreateItineraryViewModel, profileViewModel: ProfileViewModel) {
    createItineraryViewModel.setFocusedItinerary(
        (if (createItineraryViewModel.getFocusedItinerary() == null)
            null
        else
            createItineraryViewModel.getNewItinerary()?.build())
    )

    PreviewItineraryScreen(itineraryViewModel = createItineraryViewModel, profileViewModel = profileViewModel)
}

//modifier = Modifier.testTag(TestTags.CREATION_SCREEN_PREVIEW_ITINERARY)
