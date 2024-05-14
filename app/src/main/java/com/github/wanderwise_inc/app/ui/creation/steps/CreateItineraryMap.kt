package com.github.wanderwise_inc.app.ui.creation.steps

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.popup.HintPopup
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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun CreateItineraryMapWithSelector(
    createItineraryViewModel: CreateItineraryViewModel,
) {
  Scaffold(bottomBar = { LocationSelector(createItineraryViewModel) }) { innerPadding ->
    CreateItineraryMap(
        createItineraryViewModel = createItineraryViewModel, innerPaddingValues = innerPadding)
  }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun CreateItineraryMap(
    createItineraryViewModel: CreateItineraryViewModel,
    innerPaddingValues: PaddingValues
) {
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

  var isHintPopupOpen by remember { mutableStateOf(true) }

  if (userLocation != null) {
    val userLocationLatLng = LatLng(userLocation!!.latitude, userLocation!!.longitude)
    val cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.fromLatLngZoom(userLocationLatLng, 13f)
    }
    val polylinePoints by createItineraryViewModel.getPolylinePointsLiveData().observeAsState()
    GoogleMap(
        modifier =
        Modifier
            .padding(paddingValues = innerPaddingValues)
            .testTag(TestTags.MAP_GOOGLE_MAPS),
        onMapClick = {
          itineraryBuilder.addLocation(Location.fromLatLng(it))
          locations.add(Location.fromLatLng(it))
          locationsCtr++ // force a redraw
        },
        cameraPositionState = cameraPositionState) {
          userLocation?.let {
            Marker(
                tag = TestTags.MAP_USER_LOCATION,
                state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                contentDescription = TestTags.MAP_USER_LOCATION)
          }

          locations.map { location ->
            AdvancedMarker(
                state = MarkerState(position = location.toLatLng()),
                title = location.title,
            )
          }
          if (polylinePoints != null)
              Polyline(points = polylinePoints!!, color = MaterialTheme.colorScheme.primary)
        }
    if (isHintPopupOpen) {
      HintPopup(message = "Try pressing your screen to add waypoints!") { isHintPopupOpen = false }
    }
  } else {
    Column(
        modifier =
        Modifier
            .testTag(TestTags.MAP_NULL_ITINERARY)
            .fillMaxSize()
            .padding(innerPaddingValues)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
          Text("Loading your location...", modifier = Modifier.testTag(TestTags.MAP_NULL_ITINERARY))
        }
  }
}

@Composable
fun LocationSelector(createItineraryViewModel: CreateItineraryViewModel) {
  var location1 by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
  var location2 by remember { mutableStateOf("") }
    var isClicked by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf<android.location.Location?>(null) }
if (!isClicked) {
  BottomAppBar(
      modifier = Modifier
          .height(250.dp)
          .fillMaxWidth(),
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      contentColor = MaterialTheme.colorScheme.primary,
  ) {
    Column {
        Button(onClick = {
            isClicked = true

            coroutineScope.launch {
                location = createItineraryViewModel.getUserLocation().first()

                Log.d("LOCATION ENTERED", "Location: ${location?.latitude}, ${location?.longitude}")
            }

        }) {
            Log.d("LOCATION BUTTON", "Location: ${location?.latitude}, ${location?.longitude}")
            location?.let {loc ->
                Log.d("LOCATION MARKER", "Location: ${loc.latitude}, ${loc.longitude}")
                Marker(
                    state = MarkerState(position = LatLng(loc.latitude, loc.longitude)),
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),

                    )
            }
            Text(text = "Track me")
        }


      /*Text(
          modifier = Modifier.padding(10.dp),
          textAlign = TextAlign.Center,
          text = "Create a new itinerary",
      )*/
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Filled.LocationOn,
            contentDescription = "Location 1",
            modifier = Modifier.padding(start = 10.dp))
        OutlinedTextField(
            value = location1,
            onValueChange = { location1 = it },
            label = { Text("location 1...") },
            placeholder = { Text("location 1...") },
            modifier = Modifier
                .padding(start = 25.dp)
                .testTag(TestTags.FIRST_LOCATION),
            shape = RoundedCornerShape(20.dp))
      }

      Icon(
          Icons.Filled.MoreVert,
          contentDescription = "more",
          modifier = Modifier.padding(start = 10.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Filled.LocationOn,
            contentDescription = "Location 2",
            modifier = Modifier.padding(start = 10.dp))
        OutlinedTextField(
            value = location2,
            onValueChange = { location2 = it },
            label = { Text("location 2...") },
            placeholder = { Text("location 2...") },
            modifier = Modifier
                .padding(start = 25.dp)
                .testTag(TestTags.SECOND_LOCATION),
            shape = RoundedCornerShape(20.dp))
      }
    }
  }
}
}
