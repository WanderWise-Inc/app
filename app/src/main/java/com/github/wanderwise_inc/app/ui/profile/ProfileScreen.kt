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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Scaffold
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

// Opt-in annotation for experimental Material 3 API usage.
@OptIn(ExperimentalMaterial3Api::class)

// Declare a composable function for the profile screen.
@Composable
fun ProfileScreen(
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel,
    imageRepository: ImageRepository
) {
  val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: DEFAULT_USER_UID

  val profile by profileViewModel.getProfile(currentUid).collectAsState(initial = null)

  val userItineraries by
      mapViewModel.getUserItineraries(currentUid).collectAsState(initial = emptyList())

  if (profile != null) {
    Scaffold(
        topBar = {
          TopAppBar(
              title = { Text("Profile") },
              actions = {
                Row {
                  // Floating action button to navigate to the Edit Profile Screen.
                  FloatingActionButton(onClick = { /* Go to Edit Profile Screen */}) {
                    Image(
                        painter = painterResource(id = R.drawable.settings_icon),
                        contentDescription = "Edit Profile",
                        modifier = Modifier.requiredWidth(35.dp).requiredHeight(35.dp))
                  }
                }
              },
              // Define color scheme for the top app bar using MaterialTheme.
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
      // Box container to manage padding and alignment of the profile content.
      Box(
          modifier = Modifier.padding(innerPadding).fillMaxSize(),
          contentAlignment = Alignment.TopCenter) {
            // Column to layout profile details vertically and centered.
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              // Display the user's profile picture.
              ProfilePicture(profileViewModel, profile!!, imageRepository)
              // Display the user's username with top padding.
              Username(profile!!, modifier = Modifier.padding(100.dp))
              // Display the user's "Wander Score".
              WanderScore(profile!!)
              // Display badges the user has earned.
              WanderBadges()
              // List scrollable itineraries associated with the user.
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
            Modifier.size(100.dp)
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
          modifier =
              Modifier.clip(MaterialTheme.shapes.medium).size(100.dp).clickable {
                isProfilePictureChangeDropdownOpen = true
              })
      Log.d("CRASHED", "PICTURE DISPLAYED")
    } else {
      Image(
          painter = BitmapPainter(bitmap!!.asImageBitmap()),
          contentDescription = null,
          modifier =
              Modifier.clip(MaterialTheme.shapes.medium).size(100.dp).clickable {
                isProfilePictureChangeDropdownOpen = true
              })
      Log.d("CRASHED", "PICTURE IS NULL")
    }
    ProfilePictureChangeDropdownMenu(
        expanded = isProfilePictureChangeDropdownOpen,
        onDismissRequest = { isProfilePictureChangeDropdownOpen = false },
        imageRepository = imageRepository,
        profile = profile) {}
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
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .height(175.dp)
              .padding(8.dp)
              .clip(MaterialTheme.shapes.extraLarge)
              .background(MaterialTheme.colorScheme.primaryContainer)) {
        Text(
            text = "WanderBadges",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp))
      }
}

@Composable
fun WanderScore(profile: Profile) {
  Box(
      modifier =
          Modifier.clip(MaterialTheme.shapes.extraLarge)
              .background(MaterialTheme.colorScheme.primaryContainer)
              .border(
                  BorderStroke(1.dp, MaterialTheme.colorScheme.inverseOnSurface),
                  shape = MaterialTheme.shapes.extraLarge)) {
        Text(
            text = "369 WanderPoints",
            modifier = Modifier.padding(8.dp),
            fontWeight = FontWeight.Bold)
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

// Composable function for displaying a dropdown menu for changing the profile picture.
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
) {

  DropdownMenu(
      expanded = expanded,
      onDismissRequest = onDismissRequest,
      offset = offset,
      properties = properties,
      modifier = modifier) {
        // Dropdown menu item for searching and selecting a photo from the device.
        DropdownMenuItem(
            text = { Text("SEARCH PHOTO") },
            onClick = {
              // Intent for selecting an image from the device storage.
              Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*" // Set type to any image format.
                imageRepository.launchActivity(it) // Launch activity to select an image.
              }
            })

        DropdownMenuItem(
            text = { Text("UPLOAD PHOTO") },
            onClick = {
              // Call to upload image to storage with a specific path.
              imageRepository.uploadImageToStorage("profilePicture/${profile.userUid}")
            })
      }
}
