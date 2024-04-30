package com.github.wanderwise_inc.app.ui.map

import android.location.Location
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.profile.ProfilePicture
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

/** @brief previews an itinerary */
@Composable
fun PreviewItineraryScreen(mapViewModel: MapViewModel, profileViewModel: ProfileViewModel) {
  val userLocation by mapViewModel.getUserLocation().collectAsState(null)
  val itinerary = mapViewModel.getFocusedItinerary()

  if (itinerary == null) {
    NullItinerary(userLocation)
  } else {
    val cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.fromLatLngZoom(itinerary.computeCenterOfGravity().toLatLng(), 13f)
    }

    LaunchedEffect(Unit) { mapViewModel.fetchPolylineLocations(itinerary) }
    val polylinePoints by mapViewModel.getPolylinePointsLiveData().observeAsState()

  Scaffold(
      bottomBar = { PreviewItineraryBanner(itinerary, mapViewModel, profileViewModel) },
      modifier = Modifier.testTag(TestTags.MAP_PREVIEW_ITINERARY_SCREEN),
      floatingActionButton = {
        CenterButton(cameraPositionState = cameraPositionState, currentLocation = userLocation)
      },
      floatingActionButtonPosition = FabPosition.Start) { paddingValues ->
        GoogleMap(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .testTag(TestTags.MAP_GOOGLE_MAPS),
            cameraPositionState = cameraPositionState) {
              userLocation?.let {
                Marker(
                    tag = TestTags.MAP_USER_LOCATION,
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

  val profilePictureModifier =
      Modifier.clip(RoundedCornerShape(5.dp))
          .size(50.dp)
          .testTag(TestTags.MAP_PROFILE_PIC)

  val profile by profileViewModel.getProfile(itinerary.userUid).collectAsState(initial = null)

  Card(
      colors =
          CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
          ),
      modifier = Modifier.testTag(TestTags.MAP_MAXIMIZED_BANNER)) {
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
                        .testTag(TestTags.MAP_BANNER_BUTTON))
            // Itinerary Title
            Text(
                text = itinerary.title,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
                fontSize = titleFontSize,
                fontWeight = FontWeight.Normal,
                modifier =
                    Modifier.padding(2.dp).testTag(TestTags.MAP_ITINERARY_TITLE),
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
            ProfilePicture(
                profile = profile,
                profileViewModel = profileViewModel,
                modifier = profilePictureModifier)

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
                        .testTag(TestTags.MAP_ITINERARY_DESCRIPTION),
                textAlign = TextAlign.Center)
          }
        }
      }
}

@Composable
private fun PreviewItineraryBannerMinimized(onMinimizedClick: () -> Unit, itinerary: Itinerary) {
  val titleFontSize = 32.sp

  Card(
      colors =
          CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
          ),
      modifier = Modifier.testTag(TestTags.MAP_MINIMIZED_BANNER)) {
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
                        .testTag(TestTags.MAP_BANNER_BUTTON))
            // Itinerary Title
            Text(
                text = itinerary.title,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
                fontSize = titleFontSize,
                fontWeight = FontWeight.Normal,
                modifier =
                    Modifier.padding(2.dp).testTag(TestTags.MAP_ITINERARY_TITLE),
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
      modifier = Modifier.testTag(TestTags.MAP_CENTER_CAMERA_BUTTON),
      containerColor = MaterialTheme.colorScheme.surfaceContainer) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = "Center on Me",
            tint = Color.DarkGray)
      }
}

@Composable
fun NullItinerary(userLocation: Location?) {
  // for now doesn't do anything, would be nice to center on location
  if (userLocation == null) {
    Column(
        modifier =
            Modifier.testTag(PreviewItineraryScreenTestTags.NULL_ITINERARY)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
          Text(
              "Loading...",
              modifier = Modifier.testTag(PreviewItineraryScreenTestTags.NULL_ITINERARY))
        }
  } else {
    val userLocationLatLng = LatLng(userLocation!!.latitude, userLocation!!.longitude)
    val cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.fromLatLngZoom(userLocationLatLng, 13f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize().testTag(PreviewItineraryScreenTestTags.NULL_ITINERARY),
        cameraPositionState = cameraPositionState) {
          Marker(
              tag = PreviewItineraryScreenTestTags.USER_LOCATION,
              state = MarkerState(position = userLocationLatLng),
              icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        }
  }
}
