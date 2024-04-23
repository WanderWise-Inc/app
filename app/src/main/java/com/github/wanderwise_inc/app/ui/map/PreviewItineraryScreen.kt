package com.github.wanderwise_inc.app.ui.map

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.PlacesReader
import com.github.wanderwise_inc.app.ui.itinerary.ItineraryBanner
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

/** @brief previews an itinerary */
@Composable
fun PreviewItineraryScreen(itinerary: Itinerary, mapViewModel: MapViewModel) {
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(itinerary.computeCenterOfGravity().toLatLng(), 13f)
  }

  LaunchedEffect(Unit) { mapViewModel.fetchPolylineLocations(itinerary) }
  val polylinePoints by mapViewModel.polylinePointsLiveData.observeAsState()

  Scaffold(
      bottomBar = { ItineraryBanner(itinerary = itinerary) },
      modifier = Modifier.testTag("Map screen")) { paddingValues ->
        GoogleMap(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            cameraPositionState = cameraPositionState) {
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

/** @brief banner for display itinerary information */
@Composable
fun ItineraryBanner(itinerary: Itinerary) {
  var hide by remember { mutableStateOf(false) }

  Box(
      modifier =
          Modifier.background(MaterialTheme.colorScheme.background)
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
}

/** Previews a hardcoded itinerary from a JSON file */
@Composable
fun DummyPreviewItinerary(mapViewModel: MapViewModel) {
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

  PreviewItineraryScreen(itinerary = itinerary, mapViewModel = mapViewModel)
}
