package com.github.wanderwise_inc.app.ui.creation

import androidx.compose.runtime.Composable
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

@Composable
fun PreviewItinerary(mapViewModel: MapViewModel, profileViewModel: ProfileViewModel){
    val itinerary = Itinerary("", "", emptyList(), "True itinerary", emptyList(), "", true,)

    //build the itinerary, store it in focused

    PreviewItinerary(mapViewModel = mapViewModel, profileViewModel = profileViewModel)
}