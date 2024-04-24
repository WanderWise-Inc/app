package com.github.wanderwise_inc.app.ui.map

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.ui.itinerary.ItineraryBanner
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/** @brief previews an itinerary */
@Composable
fun PreviewItineraryScreen(
    itinerary: Itinerary,
    mapViewModel: MapViewModel
) {
    val userLocation by mapViewModel.getUserLocation().collectAsState(null)

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(itinerary.computeCenterOfGravity().toLatLng(), 13f)
  }

  Scaffold(
      bottomBar = { ItineraryBanner(itinerary = itinerary) },
      modifier = Modifier.testTag("Map screen"),
      floatingActionButton = {
          CenterButton(
              cameraPositionState = cameraPositionState,
              currentLocation = userLocation
          )
      },
      floatingActionButtonPosition = FabPosition.EndOverlay
  ) { paddingValues ->
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            cameraPositionState = cameraPositionState
        ) {
            userLocation?.let {
                Marker(
                    state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }
              itinerary.locations.map { location ->
                AdvancedMarker(
                    state = MarkerState(position = location.toLatLng()),
                    title = location.title ?: "",
                )
              }
            }
      }
}

@Composable
fun CenterButton(
    cameraPositionState: CameraPositionState,
    currentLocation: Location?
) {
    FloatingActionButton(
        onClick = {
            currentLocation?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
            }
        },
        containerColor = Color.White
    ) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = "Center on Me",
            tint = Color.DarkGray
        )
    }

}

/** @brief banner for display itinerary information */
/*@Composable
fun ItineraryBanner(itinerary: Itinerary) {
  var hide by remember { mutableStateOf(false) }

  Box(
      modifier =
      Modifier
          .background(MaterialTheme.colorScheme.background)
          .height(200.dp)
          .clip(RoundedCornerShape(16.dp)),
      contentAlignment = Alignment.Center,
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
          text = itinerary.title,
          color = MaterialTheme.colorScheme.primary,
          fontFamily = FontFamily.Monospace,
          fontSize = 30.sp,
          modifier = Modifier.padding(10.dp) // Adjust padding as needed
          )
      Text(
          text = itinerary.description ?: "",
          color = MaterialTheme.colorScheme.secondary,
          fontFamily = FontFamily.Monospace,
          fontSize = 15.sp,
          modifier = Modifier.padding(10.dp) // Adjust padding as needed
          )
    }
  }
}*/

/** Previews a hardcoded itinerary from a JSON file */
/*@Composable
fun DummyPreviewItinerary() {
  val placeReader = PlacesReader(null)
  val locations = placeReader.readFromString()

  val itinerary =
      Itinerary(
          userUid = "",
          locations = locations,
          title = "San Francisco Bike Itinerary",
          tags = listOf(ItineraryTags.CULTURAL, ItineraryTags.NATURE, ItineraryTags.BUDGET),
          description = "A 3-day itinerary to explore the best of San Francisco on a bike.",
          visible = true)

  PreviewItineraryScreen(itinerary, null)
}*/
