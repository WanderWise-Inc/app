package com.github.wanderwise_inc.app.ui.map

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

/** @brief previews an itinerary */
@Composable
fun PreviewItineraryScreen(itinerary: Itinerary, mapViewModel: MapViewModel) {
  val userLocation by mapViewModel.getUserLocation().collectAsState(null)

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(itinerary.computeCenterOfGravity().toLatLng(), 13f)
  }

  LaunchedEffect(Unit) { mapViewModel.fetchPolylineLocations(itinerary) }
  val polylinePoints by mapViewModel.polylinePointsLiveData.observeAsState()

  Scaffold(
      bottomBar = { ItineraryBanner(itinerary = itinerary) },
      modifier = Modifier.testTag("Map screen"),
      floatingActionButton = {
        CenterButton(cameraPositionState = cameraPositionState, currentLocation = userLocation)
      },
      floatingActionButtonPosition = FabPosition.EndOverlay) { paddingValues ->
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
