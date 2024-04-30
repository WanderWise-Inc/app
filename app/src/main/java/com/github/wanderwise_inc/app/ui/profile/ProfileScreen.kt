package com.github.wanderwise_inc.app.ui.profile

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.DEFAULT_USER_UID
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.list_itineraries.ItinerariesListScrollable
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

const val PROFILE_SCREEN_TEST_TAG: String = "profile_screen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel,
    imageRepository: ImageRepository,
    navHostController: NavHostController
) {
  // val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
  val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: DEFAULT_USER_UID

  val profile by profileViewModel.getProfile(currentUid).collectAsState(initial = null)

  val userItineraries by
      mapViewModel.getUserItineraries(currentUid).collectAsState(initial = emptyList())

  val profilePictureModifier = Modifier.size(100.dp)

  if (profile != null) {
    androidx.compose.material.Scaffold(
        /*Top bar composable*/
        topBar = {
          TopAppBar(
              title = { Text("Profile") },
              actions = {
                Row {
                  FloatingActionButton(onClick = { /*Go to Edit Profile Screen*/}) {
                    Image(
                        painter = painterResource(id = R.drawable.settings_icon),
                        contentDescription = "Edit Profile",
                        modifier = Modifier.requiredWidth(35.dp).requiredHeight(35.dp))
                  }
                  FloatingActionButton(onClick = { /*Go to Edit Profile Screen*/}) {
                    Image(
                        painter = painterResource(id = R.drawable.settings_icon),
                        contentDescription = "Edit Profile",
                        modifier = Modifier.requiredWidth(35.dp).requiredHeight(35.dp))
                  }
                }
              },
              colors =
                  TopAppBarDefaults.topAppBarColors(
                      containerColor = MaterialTheme.colorScheme.primaryContainer,
                      titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                      navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                      actionIconContentColor = MaterialTheme.colorScheme.onSecondary),
              modifier = Modifier.padding(bottom = 20.dp))
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
      Box(
          modifier = Modifier.padding(innerPadding).fillMaxSize(),
          contentAlignment = Alignment.TopCenter) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              ProfilePicture(profile, profileViewModel, profilePictureModifier)
              Button(
                  onClick = {
                    Intent(Intent.ACTION_GET_CONTENT).also {
                      it.type = "image/*"
                      imageRepository.launchActivity(it)
                    }
                  }) {
                    Text(text = "SEARCH PHOTO")
                  }
              Button(
                  onClick = {
                    imageRepository.uploadImageToStorage("profilePicture/${profile!!.userUid}")
                  }) {
                    Text(text = "UPLOAD PHOTO")
                  }
              Username(profile!!, modifier = Modifier.padding(100.dp))
              WanderScore(profile!!)
              ItinerariesListScrollable(
                  itineraries = userItineraries,
                  mapViewModel = mapViewModel,
                  profileViewModel = profileViewModel,
                  paddingValues = PaddingValues(8.dp),
                  navController = navHostController)
            }
          }
    }
  }
}

@Composable
fun Username(profile: Profile, modifier: Modifier) {
  Text(
      text = profile.displayName,
      modifier = Modifier.padding(8.dp),
      style = MaterialTheme.typography.headlineSmall)
}

@Composable
fun WanderScore(profile: Profile) {
  Box(modifier = Modifier.background(MaterialTheme.colorScheme.secondary)) {
    Text(text = "WanderScore: Not Implemented Yet")
  }
}

@Composable
fun ItineraryCard() {
  Card(modifier = Modifier.padding(8.dp)) /*onClick = { TODO* OPEN PREVIEW OF ITINERARY}*/ {
    Column {
      Text("Itinerary Name")
      Text("Itinerary Description")
    }
  }
}
