package com.github.wanderwise_inc.app.ui.map

import android.location.Location
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.flow

/* helps us avoid typos / inconsistencies between the test files and this file */
object PreviewItineraryScreenTestTags {
  const val MAIN_SCREEN = "preview_itinerary_screen"
  const val GOOGLE_MAPS = "google_maps"
  const val MAXIMIZED_BANNER = "maximized_banner"
  const val MINIMIZED_BANNER = "minimized_banner"
  const val PROFILE_PIC = "profile_picture"
  const val CENTER_CAMERA_BUTTON = "center_camera_button"
  const val BANNER_BUTTON = "banner_button"
  const val ITINERARY_TITLE = "itinerary_title"
  const val ITINERARY_DESCRIPTION = "itinerary_description"
  const val USER_LOCATION = "user_location"
}

/** @brief previews an itinerary */
@Composable
fun PreviewItineraryScreen(
    itinerary: Itinerary,
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel
) {
  val userLocation by mapViewModel.getUserLocation().collectAsState(null)

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(itinerary.computeCenterOfGravity().toLatLng(), 13f)
  }

  LaunchedEffect(Unit) { mapViewModel.fetchPolylineLocations(itinerary) }
  val polylinePoints by mapViewModel.getPolylinePointsLiveData().observeAsState()

  Scaffold(
      bottomBar = { PreviewItineraryBanner(itinerary, mapViewModel, profileViewModel) },
      modifier = Modifier.testTag(PreviewItineraryScreenTestTags.MAIN_SCREEN),
      floatingActionButton = {
        CenterButton(cameraPositionState = cameraPositionState, currentLocation = userLocation)
      },
      floatingActionButtonPosition = FabPosition.Start) { paddingValues ->
        GoogleMap(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .testTag(PreviewItineraryScreenTestTags.GOOGLE_MAPS),
            cameraPositionState = cameraPositionState) {
              userLocation?.let {
                Marker(
                    tag = PreviewItineraryScreenTestTags.USER_LOCATION,
                    state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
              }
              itinerary.locations.map { location ->
                AdvancedMarker(
                    state = MarkerState(position = location.toLatLng()),
                    title = location.title ?: "",
                )
                if (polylinePoints != null) {
                  Polyline(points = polylinePoints!!, color = MaterialTheme.colorScheme.primary)
                }
              }
            }
      }
}

/**
 * @brief displayed beneath `Maps` composable in `PreviewItineraryScreen`. Variable height with
 *   different modes depending on whether it is minimized or maximized
 */
@Composable
fun PreviewItineraryBanner(
    itinerary: Itinerary,
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel
) {

  var isMinimized by remember { mutableStateOf(false) }
  val onMinimizedClick = { isMinimized = !isMinimized }

  if (isMinimized)
      PreviewItineraryBannerMinimized(onMinimizedClick = onMinimizedClick, itinerary = itinerary)
  else
      PreviewItineraryBannerMaximized(
          onMinimizedClick = onMinimizedClick,
          itinerary = itinerary,
          mapViewModel = mapViewModel,
          profileViewModel = profileViewModel)
}

@Composable
private fun PreviewItineraryBannerMaximized(
    onMinimizedClick: () -> Unit,
    itinerary: Itinerary,
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel
) {
  val titleFontSize = 32.sp
  val innerFontSize = 16.sp

  val profile by profileViewModel.getProfile(itinerary.userUid).collectAsState(initial = null)

  Card(
      colors =
          CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
          ),
      modifier = Modifier.testTag(PreviewItineraryScreenTestTags.MAXIMIZED_BANNER)) {
        Column(
            modifier =
                Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
          Row {
            // minimize button
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "minimize_button",
                modifier =
                    Modifier.clickable { onMinimizedClick() }
                        .testTag(PreviewItineraryScreenTestTags.BANNER_BUTTON))
            // Itinerary Title
            Text(
                text = itinerary.title,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
                fontSize = titleFontSize,
                fontWeight = FontWeight.Normal,
                modifier =
                    Modifier.padding(2.dp).testTag(PreviewItineraryScreenTestTags.ITINERARY_TITLE),
                textAlign = TextAlign.Center)
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically) {

                // NUMBER OF LIKES
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                          painter = painterResource(id = R.drawable.liked_icon),
                          contentDescription = "likes",
                          tint = MaterialTheme.colorScheme.secondary,
                          modifier = Modifier.size(width = 20.dp, height = 20.dp))
                      Text(
                          text = "${itinerary.numLikes}",
                          color = MaterialTheme.colorScheme.secondary,
                          fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                          fontSize = innerFontSize,
                          fontWeight = FontWeight.Normal,
                          modifier = Modifier.padding(5.dp),
                          textAlign = TextAlign.Center,
                      )
                    }

                Spacer(modifier = Modifier.width(15.dp))

                // DURATION
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                          painter = painterResource(id = R.drawable.hourglass),
                          contentDescription = "duration",
                          tint = MaterialTheme.colorScheme.secondary,
                          modifier = Modifier.size(width = 25.dp, height = 25.dp))
                      Text(
                          text = "N/A hours",
                          color = MaterialTheme.colorScheme.secondary,
                          fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                          fontSize = innerFontSize,
                          fontWeight = FontWeight.Normal,
                          modifier = Modifier.padding(3.dp),
                          textAlign = TextAlign.Center,
                      )
                    }

                Spacer(modifier = Modifier.width(15.dp))

                // COST
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                          painter = painterResource(id = R.drawable.money),
                          contentDescription = "cost",
                          tint = MaterialTheme.colorScheme.secondary,
                          modifier = Modifier.size(width = 20.dp, height = 20.dp))
                      Text(
                          text = "\$N/A - \$N/A",
                          color = MaterialTheme.colorScheme.secondary,
                          fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                          fontSize = innerFontSize,
                          fontWeight = FontWeight.Normal,
                          modifier = Modifier.padding(3.dp),
                          textAlign = TextAlign.Center,
                      )
                    }
              }

          Spacer(modifier = Modifier.height(20.dp))

          // Profile details
          Row(
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.height(50.dp),
          ) {
            ProfilePicture(profile = profile, profileViewModel = profileViewModel)

            Spacer(modifier = Modifier.width(10.dp))

            // NAME + local WanderGuide label
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {
              Text(
                  text = profile?.displayName ?: "John Doe",
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                  fontSize = innerFontSize,
                  fontWeight = FontWeight.SemiBold,
                  // modifier = Modifier.padding(2.dp),
                  textAlign = TextAlign.Center,
              )

              Text(
                  text = "Local WanderGuide",
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                  fontSize = innerFontSize,
                  fontWeight = FontWeight.Light,
                  modifier = Modifier.padding(2.dp).fillMaxHeight(),
                  textAlign = TextAlign.Center,
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Itinerary Description
          Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.Top,
          ) {
            Text(
                text = itinerary.description ?: "Looks like this Wander has no description!",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier =
                    Modifier.padding(2.dp)
                        .testTag(PreviewItineraryScreenTestTags.ITINERARY_DESCRIPTION),
                textAlign = TextAlign.Center)
          }
        }
      }
}

@Composable
fun ProfilePicture(profile: Profile?, profileViewModel: ProfileViewModel) {
  val imageModifier =
      Modifier.clip(RoundedCornerShape(5.dp))
          .size(50.dp)
          .testTag(PreviewItineraryScreenTestTags.PROFILE_PIC)

  val defaultProfilePicture by
      profileViewModel.getDefaultProfilePicture().collectAsState(initial = null)

  val profilePictureFlow =
      if (profile != null) profileViewModel.getProfilePicture(profile) else flow { emit(null) }

  val profilePicture by profilePictureFlow.collectAsState(initial = null)

  val painter: Painter =
      if (profilePicture != null) BitmapPainter(profilePicture!!.asImageBitmap())
      else if (defaultProfilePicture != null) BitmapPainter(defaultProfilePicture!!.asImageBitmap())
      else painterResource(id = R.drawable.profile_icon)

  Image(painter = painter, contentDescription = "profile_icon", modifier = imageModifier)
}

@Composable
private fun PreviewItineraryBannerMinimized(onMinimizedClick: () -> Unit, itinerary: Itinerary) {
  val titleFontSize = 32.sp

  Card(
      colors =
          CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
          ),
      modifier = Modifier.testTag(PreviewItineraryScreenTestTags.MINIMIZED_BANNER)) {
        Column(
            modifier =
                Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                    .fillMaxWidth()
                    .aspectRatio(3f)
                    .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Row {
            // minimize button
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = "minimize_button",
                modifier =
                    Modifier.clickable { onMinimizedClick() }
                        .testTag(PreviewItineraryScreenTestTags.BANNER_BUTTON))
            // Itinerary Title
            Text(
                text = itinerary.title,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
                fontSize = titleFontSize,
                fontWeight = FontWeight.Normal,
                modifier =
                    Modifier.padding(2.dp).testTag(PreviewItineraryScreenTestTags.ITINERARY_TITLE),
                textAlign = TextAlign.Center)
          }
        }
      }
}

@Composable
fun CenterButton(cameraPositionState: CameraPositionState, currentLocation: Location?) {
  FloatingActionButton(
      onClick = {
        currentLocation?.let {
          val latLng = LatLng(it.latitude, it.longitude)
          cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
        }
      },
      modifier = Modifier.testTag(PreviewItineraryScreenTestTags.CENTER_CAMERA_BUTTON),
      containerColor = MaterialTheme.colorScheme.surfaceContainer) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = "Center on Me",
            tint = Color.DarkGray)
      }
}
