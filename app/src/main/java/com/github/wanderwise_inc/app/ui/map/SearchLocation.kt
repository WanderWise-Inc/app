package com.github.wanderwise_inc.app.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchLocation(
    createItineraryViewModel: CreateItineraryViewModel,
    navController: NavHostController
) {
  val itineraryBuilder = createItineraryViewModel.getNewItinerary()!!

  var focusedLocation: Location? by remember { mutableStateOf(null) }

  val keyboard = LocalSoftwareKeyboardController.current

  val onSearch = { query: String ->
    createItineraryViewModel.fetchPlaces(query)
    keyboard?.hide() // hide keyboard once search concludes
    focusedLocation = null // remove marker and focus on new search propositions
  }

  val focusOnLocation = { location: Location? -> focusedLocation = location }

  val userLocation by createItineraryViewModel.getUserLocation().collectAsState(initial = null)
  // val userLocationLatLng = LatLng(userLocation!!.latitude, userLocation!!.longitude)

  Scaffold(
      topBar = { LocationSearchBar(onSearch, focusOnLocation, createItineraryViewModel) },
      floatingActionButton = {
        if (focusedLocation != null) {
          ExtendedFloatingActionButton(
              onClick = {
                itineraryBuilder.addLocation(focusedLocation!!)
                // createItineraryViewModel.fetchPolylineLocations(itineraryBuilder.build()) // Not
                // sure if this is necessary
                navController.popBackStack()
              },
              icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) },
              text = { Text("Add Location") })
        }
      },
      floatingActionButtonPosition = FabPosition.Center) { padding ->
        if (userLocation != null) {
          val userLocationLatLng = LatLng(userLocation!!.lat, userLocation!!.long)
          val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(userLocationLatLng, 13f)
          }

          // updates camera position to center on searched item
          LaunchedEffect(focusedLocation) {
            if (focusedLocation == null) {
              cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(userLocationLatLng, 13f))
            } else {
              cameraPositionState.move(
                  CameraUpdateFactory.newLatLngZoom(focusedLocation!!.toLatLng(), 13f))
            }
          }
          GoogleMap(
              modifier = Modifier.padding(padding), cameraPositionState = cameraPositionState) {
                userLocation?.let {
                  Marker(
                      tag = TestTags.MAP_USER_LOCATION,
                      state = MarkerState(position = LatLng(it.lat, it.long)),
                      icon =
                          BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                      contentDescription = TestTags.MAP_USER_LOCATION)
                }

                if (focusedLocation != null) {
                  AdvancedMarker(
                      state = MarkerState(position = focusedLocation!!.toLatLng()),
                      title = focusedLocation!!.title)
                }
              }
        } else {
          Column(
              modifier =
                  Modifier.testTag(TestTags.MAP_NULL_ITINERARY)
                      .fillMaxSize()
                      .padding(padding)
                      .background(MaterialTheme.colorScheme.background),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center) {
                Text(
                    "Loading your location...",
                    modifier = Modifier.testTag(TestTags.MAP_NULL_ITINERARY))
              }
        }
      }
}
