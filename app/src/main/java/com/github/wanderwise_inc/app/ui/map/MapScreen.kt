package com.github.wanderwise_inc.app.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

/** @brief if the itinerary passed as argument is non null, call preview
 * otherwise center on user */
@Composable
fun MapScren(
    itinerary: Itinerary?,
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel
) {
    Box(modifier = Modifier.testTag(TestTags.MAP_SCREEN)) {
        if (itinerary == null) {
            // center camera
        } else {
            PreviewItineraryScreen(
                itinerary = itinerary!!,
                mapViewModel = mapViewModel,
                profileViewModel = profileViewModel
            )
        }
    }
}