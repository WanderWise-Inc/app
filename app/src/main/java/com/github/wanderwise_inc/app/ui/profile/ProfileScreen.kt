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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.list_itineraries.ItinerariesListScrollable
import com.github.wanderwise_inc.app.ui.list_itineraries.ItineraryListParent
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

const val WANDER_POINTS_SCORE_MULTIPLIER = 10

@OptIn(ExperimentalMaterial3Api::class)
// Declare a composable function for the profile screen.
@Composable
fun ProfileScreen(
    itineraryViewModel: ItineraryViewModel,
    profileViewModel: ProfileViewModel,
    imageRepository: ImageRepository,
    navHostController: NavHostController,
) {
  imageRepository.setIsItineraryImage(false)
  Log.d("ProfileScreen", "ProfileScreen")
  val profile = profileViewModel.getActiveProfile()
  val currentUid = profileViewModel.getUserUid()

  val userItineraries by
      itineraryViewModel.getUserItineraries(currentUid).collectAsState(initial = emptyList())
  var ctr = remember { mutableIntStateOf(0) }

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
        modifier = Modifier.fillMaxSize().testTag(TestTags.PROFILE_SCREEN),
    ) { innerPadding ->
      // Box container to manage padding and alignment of the profile content.
      Box(
          modifier = Modifier.padding(innerPadding).fillMaxSize(),
          contentAlignment = Alignment.TopCenter) {
            // Column to layout profile details vertically and centered.
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              // Display the user's profile picture.
              ProfilePictureWithDropDown(profile, profileViewModel, imageRepository, ctr)
              // Display the user's username with top padding.
              Username(profile!!, modifier = Modifier.padding(100.dp))
              // Display the user's "Wander Score".
              WanderScore(profile!!, itineraryViewModel)
              // Display badges the user has earned.
              WanderBadges()
              // List scrollable itineraries associated with the user.
              ItinerariesListScrollable(
                  itineraries = userItineraries,
                  itineraryViewModel = itineraryViewModel,
                  profileViewModel = profileViewModel,
                  navController = navHostController,
                  paddingValues = PaddingValues(8.dp),
                  parent = ItineraryListParent.PROFILE,
                  imageRepository = imageRepository)
            }
          }
    }
  }
}

@Composable
fun ProfilePictureWithDropDown(
    profile: Profile?,
    profileViewModel: ProfileViewModel,
    imageRepository: ImageRepository,
    ctr: MutableIntState
) {
  var isProfilePictureChangeDropdownOpen by remember { mutableStateOf(false) }
  val profilePictureModifier =
      Modifier.size(100.dp).clip(CircleShape).clickable {
        isProfilePictureChangeDropdownOpen = true
      }
  ProfilePicture(
      profile = profile,
      profileViewModel = profileViewModel,
      modifier = profilePictureModifier,
      ctr = ctr)
  ProfilePictureChangeDropdownMenu(
      expanded = isProfilePictureChangeDropdownOpen,
      onDismissRequest = { isProfilePictureChangeDropdownOpen = false },
      imageRepository = imageRepository,
      profile = profile,
      ctr = ctr) {}
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
fun WanderScore(profile: Profile, itineraryViewModel: ItineraryViewModel) {
  Box(
      modifier =
          Modifier.clip(MaterialTheme.shapes.extraLarge)
              .background(MaterialTheme.colorScheme.primaryContainer)
              .border(
                  BorderStroke(1.dp, MaterialTheme.colorScheme.inverseOnSurface),
                  shape = MaterialTheme.shapes.extraLarge)) {
        val ownItineraries by
            itineraryViewModel
                .getUserItineraries(profile.userUid)
                .collectAsState(initial = emptyList())
        var score = 0
        for (itinerary in ownItineraries) {
          score += itinerary.numLikes * WANDER_POINTS_SCORE_MULTIPLIER
        }
        Text(
            text = "$score WanderPoints",
            modifier = Modifier.padding(8.dp),
            fontWeight = FontWeight.Bold)
      }
}

// Composable function for displaying a dropdown menu for changing the profile picture.
@Composable
fun ProfilePictureChangeDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    imageRepository: ImageRepository,
    ctr: MutableIntState,
    profile: Profile?,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 105.dp),
    properties: PopupProperties = PopupProperties(focusable = true),
    content: @Composable ColumnScope.() -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  if (profile != null) {
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
                coroutineScope.launch {
                  imageRepository.uploadImageToStorage("profilePicture/${profile.userUid}")
                  ctr.intValue++
                  imageRepository.setCurrentFile(null)
                }
              })
        }
  }
}
