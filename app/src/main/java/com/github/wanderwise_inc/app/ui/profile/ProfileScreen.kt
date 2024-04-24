package com.github.wanderwise_inc.app.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.testTag
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.itinerary.ItineraryBanner
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

const val PROFILE_SCREEN_TEST_TAG: String = "profile_screen"

@Composable
fun ProfileScreen(mapViewModel: MapViewModel, profileViewModel: ProfileViewModel) {
  val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
  val profile by profileViewModel.getProfile(currentUid).collectAsState(initial = null)

  if (profile != null) {
    Column(modifier = Modifier.testTag(PROFILE_SCREEN_TEST_TAG)) {
      Text(text = "Hello ${profile!!.displayName}!\nprofile picture: ")
      profilePicture(profileViewModel, profile!!)
      ItinerariesScrollable(mapViewModel, currentUid)
    }
  } else {
    Text("profile not found", modifier = Modifier.testTag(PROFILE_SCREEN_TEST_TAG))
  }
}

@Composable
fun profilePicture(profileViewModel: ProfileViewModel, profile: Profile) {
  val picture by profileViewModel.getProfilePicture(profile).collectAsState(initial = null)

  if (picture != null) {
    Image(
        painter = BitmapPainter(picture!!.asImageBitmap()), contentDescription = "Profile picture")
  } else {
    Text("No Picture")
  }
}

@Composable
fun ItinerariesScrollable(mapViewModel: MapViewModel, uid: String) {
  val itineraries by mapViewModel.getUserItineraries(uid).collectAsState(initial = emptyList())
  if (itineraries.isNotEmpty()) {
    Column {
      Text("Your Itineraries:")
      itineraries.forEach { itinerary -> ItineraryBanner(itinerary = itinerary){} }
    }
  } else {
    Text("You have not created any itineraries yet.")
  }
}
