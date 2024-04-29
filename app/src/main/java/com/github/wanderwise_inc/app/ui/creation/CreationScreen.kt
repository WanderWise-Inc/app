package com.github.wanderwise_inc.app.ui.creation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

object CreationScreenTestTags {
  const val SCREEN = "Creation Screen"
}

@Composable
fun CreationScreen(
    // navController: NavHostController,
    mapViewModel: MapViewModel
) {
  Text(
      text = "Welcome, here you will be able to create a new itinerary",
      modifier = Modifier.testTag(CreationScreenTestTags.SCREEN))
}
