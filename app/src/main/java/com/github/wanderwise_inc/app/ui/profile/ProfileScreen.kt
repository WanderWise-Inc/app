package com.github.wanderwise_inc.app.ui.profile

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
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
    imageRepository: ImageRepository
) {
  // val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
  val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: DEFAULT_USER_UID

  val profile by profileViewModel.getProfile(currentUid).collectAsState(initial = null)

  val userItineraries by
      mapViewModel.getUserItineraries(currentUid).collectAsState(initial = emptyList())



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
                        modifier = Modifier
                            .requiredWidth(35.dp)
                            .requiredHeight(35.dp))
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
          modifier = Modifier
              .padding(innerPadding)
              .fillMaxSize(),
          contentAlignment = Alignment.TopCenter) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              ProfilePicture(profileViewModel, profile!!, imageRepository)
              Username(profile!!, modifier = Modifier.padding(100.dp))
              WanderScore(profile!!)
              WanderBadges()
              ItinerariesListScrollable(
                  itineraries = userItineraries,
                  mapViewModel = mapViewModel,
                  profileViewModel = profileViewModel,
                  paddingValues = PaddingValues(8.dp))
            }
          }
    }
  }
}

@Composable
fun ProfilePictureStatic(profileViewModel: ProfileViewModel, profile: Profile, modifier: Modifier) {
  val picture by profileViewModel.getProfilePicture(profile).collectAsState(initial = null)

  if (picture != null) {
    Image(
        painter = BitmapPainter(picture!!.asImageBitmap()),
        contentDescription = "Profile picture",
        modifier =
        Modifier
            .size(100.dp)
            .clip(MaterialTheme.shapes.small)
            .border(BorderStroke(1.dp, Color.Black)),
        contentScale = ContentScale.FillBounds)
  } else {
    Text("No Picture")
  }
}


@Composable
fun ProfilePicture(
    profileViewModel: ProfileViewModel,
    profile: Profile,
    imageRepository: ImageRepository
) {

  var isProfilePictureChangeDropdownOpen by remember { mutableStateOf(false) }

  // We will first fetch the default picture that is stored in the storage
  val bitmap by
      imageRepository
          .fetchImage("profilePicture/defaultProfilePicture.jpg")
          .collectAsState(initial = null)

  if (bitmap != null) {
    // When the default picture is fetched, we will try to get the profilePicture of the user.
    // If the profile picture isn't present in the Storage, then the default picture will be loaded
    // instead
    val picture by profileViewModel.getProfilePicture(profile).collectAsState(initial = null)
    if (picture != null) {
      Image(
          painter = BitmapPainter(picture!!.asImageBitmap()),
          contentDescription = null,
          modifier = Modifier
              .clip(MaterialTheme.shapes.medium)
              .size(100.dp)
              .clickable { isProfilePictureChangeDropdownOpen = true })
      Log.d("CRASHED", "PICTURE DISPLAYED")
    } else {
      Image(
          painter = BitmapPainter(bitmap!!.asImageBitmap()),
          contentDescription = null,
          modifier = Modifier
              .clip(MaterialTheme.shapes.medium)
              .size(100.dp)
              .clickable { isProfilePictureChangeDropdownOpen = true })
      Log.d("CRASHED", "PICTURE IS NULL")
    }
      ProfilePictureChangeDropdownMenu(expanded = isProfilePictureChangeDropdownOpen, onDismissRequest = { isProfilePictureChangeDropdownOpen = false }, imageRepository = imageRepository, profile = profile) {

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
fun WanderBadges() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(175.dp)
        .padding(8.dp)
        .clip(MaterialTheme.shapes.extraLarge)
        .background(MaterialTheme.colorScheme.primaryContainer)) {
        Text(text = "WanderBadges", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun WanderScore(profile: Profile) {
  Box(modifier = Modifier
      .clip(MaterialTheme.shapes.extraLarge)
      .background(MaterialTheme.colorScheme.primaryContainer)
      .border(
          BorderStroke(1.dp, MaterialTheme.colorScheme.inverseOnSurface),
          shape = MaterialTheme.shapes.extraLarge
      )) {
    Text(text = "369 WanderPoints", modifier = Modifier.padding(8.dp), fontWeight = FontWeight.Bold)
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

@Composable
fun ProfilePictureChangeDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    imageRepository: ImageRepository,
    profile: Profile,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    properties: PopupProperties = PopupProperties(focusable = true),
    content: @Composable ColumnScope.() -> Unit
){
   DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {
       DropdownMenuItem(text = {Text("SEARCH PHOTO")}, onClick = {
           Intent(Intent.ACTION_GET_CONTENT).also {
           it.type = "image/*"
           imageRepository.launchActivity(it)}
       })
       DropdownMenuItem(text = {Text("UPLOAD PHOTO")}, onClick = {
           imageRepository.uploadImageToStorage("profilePicture/${profile.userUid}")
       })
   }
}
