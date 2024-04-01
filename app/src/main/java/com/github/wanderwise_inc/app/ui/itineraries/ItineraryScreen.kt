package com.github.wanderwise_inc.app.ui.itineraries

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun ItineraryScreen() {
    Text(text = "Welcome to the itinerary screen", Modifier.testTag("Itinerary Screen"))
}