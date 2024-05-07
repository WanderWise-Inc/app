package com.github.wanderwise_inc.app.ui.creation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CreationScreen(
    // navController: NavHostController,
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel
) {
  // TODO replace this when implemented in `ProfileViewModel`
  val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: "NULL"

  var isNewItineraryNull by remember { mutableStateOf(mapViewModel.getNewItinerary() == null) }

  if (isNewItineraryNull) {
    NoNewItinerary {
      mapViewModel.startNewItinerary(userUid)
      isNewItineraryNull = false
    }
  } else {
    CreateItineraryMap(mapViewModel = mapViewModel)
  }
}

/** the screen that is displayed when the user isn't actively creating a mapscreen */
@Composable
fun NoNewItinerary(onClick: () -> Unit) {
  Box(
      contentAlignment = Alignment.Center,
      modifier =
          Modifier.fillMaxSize().testTag(TestTags.CREATION_SCREEN).fillMaxWidth().height(100.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              Text(
                  text = "You aren't currently creating an itinerary...",
                  color = MaterialTheme.colorScheme.primary,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(5.dp, 10.dp))

              Spacer(modifier = Modifier.height(10.dp))

              Button(
                  onClick = onClick,
              ) {
                Text(
                    text = "Press here to get started!",
                    color = MaterialTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(5.dp, 10.dp))
              }
            }
      }
}
