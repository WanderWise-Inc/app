package com.github.wanderwise_inc.app.ui.profile

import android.util.Log
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
import coil.compose.AsyncImage
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
  Log.d("CRASHED", "IN PROFILESCREEN")

  if (profile != null) {
    Column(modifier = Modifier.testTag(PROFILE_SCREEN_TEST_TAG)) {
      Text(text = "Hello ${profile!!.displayName}!\nprofile picture: ")
      Log.d("CRASHED", "BEFORE PROFILE PICTURE")
      ProfilePicture(profileViewModel, profile!!)
      ItinerariesScrollable(mapViewModel, currentUid)
    }
  } else {
    Log.d("CRASHED", "PROFILE NUL")
  }
}

@Composable
fun ProfilePicture(profileViewModel: ProfileViewModel, profile: Profile) {
  val picture by profileViewModel.getBitMap(profile).collectAsState(initial = null)
  if (picture != null) {
    Image(painter = BitmapPainter(picture!!.asImageBitmap()), contentDescription = null)
    Log.d("CRASHED", "PICTURE DISPLAYED")
  } else {
    Log.d("CRASHED", "PICTURE IS NULL")
  }
}

@Composable
fun ItinerariesScrollable(mapViewModel: MapViewModel, uid: String) {
  val itineraries by mapViewModel.getUserItineraries(uid).collectAsState(initial = emptyList())
  if (itineraries.isNotEmpty()) {
    Column {
      Text("Your Itineraries:")
      itineraries.forEach { itinerary -> ItineraryBanner(itinerary = itinerary) }
    }
  } else {
    Text("You have not created any itineraries yet.")
  }
}
