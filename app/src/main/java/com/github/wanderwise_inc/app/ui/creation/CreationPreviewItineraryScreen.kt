package com.github.wanderwise_inc.app.ui.creation

import androidx.compose.runtime.Composable
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.ui.map.PreviewItineraryScreen
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

@Composable
fun PreviewItinerary(createItineraryViewModel: CreateItineraryViewModel , profileViewModel: ProfileViewModel){
    //val itinerary = Itinerary("", "", emptyList(), "True itinerary", emptyList(), "", true,)

    //build the itinerary and set it as the focused
    createItineraryViewModel.setFocusedItinerary(
        (if (createItineraryViewModel.getFocusedItinerary() == null)
            null
        else
            createItineraryViewModel.getNewItinerary()?.build())
    )

    PreviewItineraryScreen(itineraryViewModel = createItineraryViewModel, profileViewModel = profileViewModel)
}