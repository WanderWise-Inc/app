package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.wanderwise_inc.app.ui.TestTags

@Composable
fun CreationStepChooseLocationsScreen() {
  Text(
      text =
          "Welcome to the first step of creating your itinerary, here you will be able to " +
              "choose the list of locations in your itinerary",
      // IT IS IMPORTANT TO KEEP THIS TAG SOMEWHERE IN THE COMPOSABLE FOR THE TEST
      modifier = Modifier.testTag(TestTags.CREATION_SCREEN_LOCATIONS))
}
