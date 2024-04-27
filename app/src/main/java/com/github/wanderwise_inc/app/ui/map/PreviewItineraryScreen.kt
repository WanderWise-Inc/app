package com.github.wanderwise_inc.app.ui.map

import android.graphics.Bitmap
import android.location.Location
import android.util.Log
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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

  // LaunchedEffect(Unit) { mapViewModel.fetchPolylineLocations(itinerary) }
  // val polylinePoints by mapViewModel.getPolylinePointsLiveData().observeAsState()
  val polylinePoints = null

  Scaffold(
      bottomBar = { PreviewItineraryBanner(itinerary, mapViewModel, profileViewModel) },
      modifier = Modifier.testTag("Map screen"),
      floatingActionButton = {
        CenterButton(cameraPositionState = cameraPositionState, currentLocation = userLocation)
      },
      floatingActionButtonPosition = FabPosition.Start) { paddingValues ->
        GoogleMap(
            modifier = Modifier.fillMaxSize().padding(paddingValues).testTag("Google Maps"),
            cameraPositionState = cameraPositionState) {
              userLocation?.let {
                Marker(
                    state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
              }
              itinerary.locations.map { location ->
                AdvancedMarker(
                    state = MarkerState(position = location.toLatLng()),
                    title = location.title ?: "",
                )
                if (polylinePoints != null) {
                  Polyline(points = polylinePoints!!, color = Color.Red)
                }
              }
            }
      }
}

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

/** Banner displayed beneath the maps composable in `PreviewItineraryScreen` */
@Composable
private fun PreviewItineraryBannerMaximized(
    onMinimizedClick: () -> Unit,
    itinerary: Itinerary,
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel
) {

  val profile by profileViewModel.getProfile(itinerary.userUid).collectAsState(initial = null)
  val profilePicFlow: Flow<Bitmap?> =
      if (profile == null) flow { emit(null) } else profileViewModel.getProfilePicture(profile!!)

  val defaultProfilePic by
      profileViewModel.getDefaultProfilePicture().collectAsState(initial = null)

  val profilePic by profilePicFlow.collectAsState(initial = null)
  Log.d("PREVIEW_ITINERARY", "profilePic = $profilePic")

  val titleFontSize = 32.sp
  val innerFontSize = 16.sp

  ElevatedCard(
      colors =
          CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
          ),
      elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
      shape = RoundedCornerShape(13.dp),
      modifier = Modifier.testTag("Itinerary banner")) {
        Column(
            modifier =
                Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Row {
            // minimize button
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "minimize_button",
                modifier = Modifier.clickable { onMinimizedClick() })
            // Itinerary Title
            Text(
                text = itinerary.title,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
                fontSize = titleFontSize,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(2.dp),
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
                          modifier = Modifier.padding(2.dp),
                          textAlign = TextAlign.Center,
                      )
                    }

                Spacer(modifier = Modifier.width(10.dp))

                // DURATION
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                          painter = painterResource(id = R.drawable.trending_icon),
                          contentDescription = "duration",
                          tint = MaterialTheme.colorScheme.secondary,
                          modifier = Modifier.size(width = 20.dp, height = 20.dp))
                      Text(
                          text = "NULL hours",
                          color = MaterialTheme.colorScheme.secondary,
                          fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                          fontSize = innerFontSize,
                          fontWeight = FontWeight.Normal,
                          modifier = Modifier.padding(2.dp),
                          textAlign = TextAlign.Center,
                      )
                    }

                Spacer(modifier = Modifier.width(10.dp))

                // COST
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                          painter = painterResource(id = R.drawable.drinks_icon),
                          contentDescription = "cost",
                          tint = MaterialTheme.colorScheme.secondary,
                          modifier = Modifier.size(width = 20.dp, height = 20.dp))
                      Text(
                          text = "null - null $",
                          color = MaterialTheme.colorScheme.secondary,
                          fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                          fontSize = innerFontSize,
                          fontWeight = FontWeight.Normal,
                          modifier = Modifier.padding(2.dp),
                          textAlign = TextAlign.Center,
                      )
                    }
              }

          Spacer(modifier = Modifier.height(10.dp))

          // Profile details
          Row(
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.height(50.dp)) {
                ProfilePicture(profile = profile, profileViewModel = profileViewModel)
              }

          Spacer(modifier = Modifier.width(10.dp))

          Column(
              verticalArrangement = Arrangement.Center,
              horizontalAlignment = Alignment.Start,
              modifier = Modifier.fillMaxHeight()) {
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
                    // modifier = Modifier.padding(2.dp),
                    textAlign = TextAlign.Center,
                )
              }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = itinerary.description ?: "Looks like this Wander has no description!",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(2.dp),
            textAlign = TextAlign.Center)
      }
}

@Composable
fun ProfilePicture(profile: Profile?, profileViewModel: ProfileViewModel) {
  val defaultProfilePicture by
      profileViewModel.getDefaultProfilePicture().collectAsState(initial = null)
  if (profile == null) {
    if (defaultProfilePicture == null) {
      Icon(
          painter = painterResource(id = R.drawable.profile_icon),
          contentDescription = "profile_icon")
    } else {
      Image(
          painter = BitmapPainter(defaultProfilePicture!!.asImageBitmap()),
          contentDescription = "profile_pic",
          modifier = Modifier.size(30.dp))
    }
  } else {
    val profilePicture by profileViewModel.getProfilePicture(profile).collectAsState(initial = null)
    if (profilePicture != null) {
      Image(
          painter = BitmapPainter(profilePicture!!.asImageBitmap()),
          contentDescription = "profile_pic",
          modifier = Modifier.size(30.dp))
    } else if (defaultProfilePicture != null) {
      Image(
          painter = BitmapPainter(defaultProfilePicture!!.asImageBitmap()),
          contentDescription = "profile_pic",
          modifier = Modifier.size(30.dp))
    } else {
      Icon(
          painter = painterResource(id = R.drawable.profile_icon),
          contentDescription = "profile_icon")
    }
  }
}

@Composable
fun PreviewItineraryBannerMinimized(onMinimizedClick: () -> Unit, itinerary: Itinerary) {
  val titleFontSize = 32.sp

  ElevatedCard(
      colors =
          CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
          ),
      elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
      shape = RoundedCornerShape(13.dp),
      modifier = Modifier.testTag("Itinerary banner")) {
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
                modifier = Modifier.clickable { onMinimizedClick() })
            // Itinerary Title
            Text(
                text = itinerary.title,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
                fontSize = titleFontSize,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(2.dp),
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
      modifier = Modifier.testTag("Center Button"),
      containerColor = Color.White) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = "Center on Me",
            tint = Color.DarkGray)
      }
}
