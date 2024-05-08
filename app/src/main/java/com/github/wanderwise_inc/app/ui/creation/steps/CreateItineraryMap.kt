package com.github.wanderwise_inc.app.ui.creation.steps

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("MutableCollectionMutableState")
@Composable
fun CreateItineraryMap(
    mapViewModel: MapViewModel,
) {
  val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: "NULL"
  var itineraryBuilder = mapViewModel.getNewItinerary()!!
  val itinerary = itineraryBuilder.build()
  val locations = remember { mutableStateListOf<Location>() }
  for (location in itinerary.locations) {
    locations += location
  }

  var ctr = 0
  val cameraPositionState = rememberCameraPositionState {
    CameraPosition.fromLatLngZoom(itinerary.computeCenterOfGravity().toLatLng(), 13f)
  }
  Box(modifier = Modifier.testTag(TestTags.CREATION_SCREEN_LOCATIONS)) {
    GoogleMap(
        modifier = Modifier.fillMaxSize().testTag(TestTags.MAP_GOOGLE_MAPS),
        cameraPositionState = cameraPositionState) {
          locations.map { location ->
            AdvancedMarker(
                state = MarkerState(position = location.toLatLng()),
                title = location.title ?: "",
            )
          }
        }
    Button(
        onClick = {
          itineraryBuilder.addLocation(Location.fromLatLng(LatLng(ctr.toDouble(), ctr.toDouble())))
          locations.add(Location.fromLatLng(LatLng(ctr.toDouble(), ctr.toDouble())))
          ctr += 1
        }) {
          Text("Add a new location")
        }
  }
}
