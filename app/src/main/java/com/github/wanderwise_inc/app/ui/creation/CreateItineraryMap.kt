package com.github.wanderwise_inc.app.ui.creation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("MutableCollectionMutableState")
@Composable
fun CreateItineraryMap(createItineraryViewModel: CreateItineraryViewModel) {
  val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: "NULL"
  var itineraryBuilder = createItineraryViewModel.getNewItinerary()!!
  val itinerary = itineraryBuilder.build()
  val locations = remember { mutableStateListOf<Location>() }
  for (location in itineraryBuilder.locations) {
    locations += location
  }
  var locationsCtr by remember { mutableIntStateOf(0) }

  LaunchedEffect(locationsCtr) {
    createItineraryViewModel.fetchPolylineLocations(itineraryBuilder.build())
  }
  val polylinePoints by createItineraryViewModel.getPolylinePointsLiveData().observeAsState()

  val cameraPositionState = rememberCameraPositionState {
    CameraPosition.fromLatLngZoom(itineraryBuilder.build().computeCenterOfGravity().toLatLng(), 13f)
  }
  GoogleMap(
      modifier = Modifier.fillMaxSize().testTag(TestTags.MAP_GOOGLE_MAPS),
      onMapClick = {
        itineraryBuilder.addLocation(Location.fromLatLng(it))
        locations.add(Location.fromLatLng(it))
        locationsCtr++
      },
      cameraPositionState = cameraPositionState) {
        locations.map { location ->
          AdvancedMarker(
              state = MarkerState(position = location.toLatLng()),
              title = location.title ?: "",
          )
        }
        if (polylinePoints != null)
            Polyline(points = polylinePoints!!, color = MaterialTheme.colorScheme.primary)
      }
}
