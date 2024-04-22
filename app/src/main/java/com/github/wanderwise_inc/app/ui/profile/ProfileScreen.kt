package com.github.wanderwise_inc.app.ui.profile

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ImageRepositoryImpl
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.itinerary.ItineraryBanner
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

const val PROFILE_SCREEN_TEST_TAG: String = "profile_screen"

@Composable
fun ProfileScreen(mapViewModel: MapViewModel, profileViewModel: ProfileViewModel, imageRepository: ImageRepository) {
  val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
  val profile by profileViewModel.getProfile(currentUid).collectAsState(initial = null)
  Log.d("CRASHED", "IN PROFILESCREEN")

  if (profile != null) {
    Column(modifier = Modifier.testTag(PROFILE_SCREEN_TEST_TAG)) {
      
      Button(onClick = {
        Intent(Intent.ACTION_GET_CONTENT).also {
          // we will get only images (filter)
          it.type = "image/*"
          imageRepository.launchActivity(it)
        }
      }) {
        Text(text = "SEARCH PHOTO")
      }
      
      Button(onClick = {
        imageRepository.uploadImageToStorage("profilePicture/${profile!!.userUid}")
      }) {
        Text(text = "UPLOAD PHOTO")
      }
      
      Text(text = "Hello ${profile!!.displayName}!\nprofile picture: ")
      Log.d("CRASHED", "BEFORE PROFILE PICTURE")
      ProfilePicture(profileViewModel, profile!!, imageRepository)
      ItinerariesScrollable(mapViewModel, currentUid)
    }
  } else {
    Log.d("CRASHED", "PROFILE NUL")
  }
}

@Composable
fun ProfilePicture(profileViewModel: ProfileViewModel, profile: Profile, imageRepository: ImageRepository) {
  // We will first fetch the default picture that is stored in the storage
  val bitmap by imageRepository
    .fetchImage("profilePicture/defaultProfilePicture.jpg")
    .collectAsState(
    initial = null
  )

  if (bitmap != null) {
    // When the default picture is fetched, we will try to get the profilePicture of the user.
    // If the profile picture isn't present in the Storage, then the default picture will be loaded instead
    val picture by profileViewModel.getProfilePicture(profile).collectAsState(initial = null)
    if (picture != null) {
      Image(painter = BitmapPainter(picture!!.asImageBitmap()), contentDescription = null, modifier = Modifier.size(100.dp))
      Log.d("CRASHED", "PICTURE DISPLAYED")
    } else {
      Image(painter = BitmapPainter(bitmap!!.asImageBitmap()), contentDescription = null, modifier = Modifier.size(100.dp))
      Log.d("CRASHED", "PICTURE IS NULL")
    }
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
