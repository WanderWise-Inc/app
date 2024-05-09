package com.github.wanderwise_inc.app.ui.creation

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay

@SuppressLint("MutableCollectionMutableState")
@Composable
fun CreateItineraryMap(createItineraryViewModel: CreateItineraryViewModel) {
  val itineraryBuilder = createItineraryViewModel.getNewItinerary()!!
  val locations = remember { mutableStateListOf<Location>() }
  for (location in itineraryBuilder.locations) {
    locations += location
  }
  var locationsCtr by remember { mutableIntStateOf(0) }
  val userLocation by createItineraryViewModel.getUserLocation().collectAsState(initial = null)

  LaunchedEffect(locationsCtr) {
    createItineraryViewModel.fetchPolylineLocations(itineraryBuilder.build())
  }

  if (userLocation != null) {
    val userLocationLatLng = LatLng(userLocation!!.latitude, userLocation!!.longitude)
    val cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.fromLatLngZoom(userLocationLatLng, 13f)
    }
    val polylinePoints by createItineraryViewModel.getPolylinePointsLiveData().observeAsState()
    GoogleMap(
      modifier = Modifier
        .fillMaxSize()
        .testTag(TestTags.MAP_GOOGLE_MAPS),
      onMapClick = {
        itineraryBuilder.addLocation(Location.fromLatLng(it))
        locations.add(Location.fromLatLng(it))
        locationsCtr++ // force a redraw
      },
      cameraPositionState = cameraPositionState
    ) {
      userLocation?.let {
        Marker(
          tag = TestTags.MAP_USER_LOCATION,
          state = MarkerState(position = LatLng(it.latitude, it.longitude)),
          icon =
          BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
        )
      }

      locations.map { location ->
        AdvancedMarker(
          state = MarkerState(position = location.toLatLng()),
          title = location.title ?: "",
        )
      }

      if (polylinePoints != null)
        Polyline(points = polylinePoints!!, color = MaterialTheme.colorScheme.primary)
    }
  } else {
    Column(
      modifier =
      Modifier.testTag(TestTags.MAP_NULL_ITINERARY)
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
      Text("Loading your location...", modifier = Modifier.testTag(TestTags.MAP_NULL_ITINERARY))
    }
  }
}
