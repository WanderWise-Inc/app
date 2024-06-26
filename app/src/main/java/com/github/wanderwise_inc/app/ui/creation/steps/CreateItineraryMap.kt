package com.github.wanderwise_inc.app.ui.creation.steps

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.popup.HintPopup
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.LocationService
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun CreateItineraryMapWithSelector(
    createItineraryViewModel: CreateItineraryViewModel,
    navController: NavHostController
) {
  var showLocationSelector = remember {
    mutableStateOf(createItineraryViewModel.createItineraryManually)
  }
  var showLiveCreation = remember {
    mutableStateOf(createItineraryViewModel.createItineraryByTracking)
  }

  val itineraryBuilder = createItineraryViewModel.getNewItinerary()!!

  var locations by remember { mutableStateOf(itineraryBuilder.locations.toList()) }

  val onMapClick = { latLng: LatLng ->
    itineraryBuilder.addLocation(Location.placeLocation(latLng))
    locations += Location.placeLocation(latLng)
  }

  val resetLocations = {
    itineraryBuilder.resetLocations()
    locations = emptyList()
  }

  val addLiveLocation = { location: Location -> locations += location }

  Scaffold(
      bottomBar = {
        if (!showLocationSelector.value && !showLiveCreation.value) {
          // CreateLiveItinerary()
          ChooseYourWayOfCreation(createItineraryViewModel, showLocationSelector, showLiveCreation)
        } else if (showLiveCreation.value) {
          CreateLiveItinerary(showLiveCreation, createItineraryViewModel, addLiveLocation)
        } else {
          LocationSelector(showLocationSelector, locations, resetLocations, navController)
        }
      }) { innerPadding ->
        CreateItineraryMap(createItineraryViewModel, onMapClick, locations, innerPadding)
      }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun CreateItineraryMap(
    createItineraryViewModel: CreateItineraryViewModel,
    onMapClick: (LatLng) -> Unit,
    locations: List<Location>,
    innerPaddingValues: PaddingValues
) {

  val userLocation by createItineraryViewModel.getUserLocation().collectAsState(initial = null)

  var isHintPopupOpen by remember { mutableStateOf(true) }

  if (userLocation != null) {
    val userLocationLatLng = userLocation!!.toLatLng()
    val cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.fromLatLngZoom(userLocationLatLng, 13f)
    }
    GoogleMap(
        modifier =
            Modifier.padding(paddingValues = innerPaddingValues).testTag(TestTags.MAP_GOOGLE_MAPS),
        onMapClick = { onMapClick(it) },
        cameraPositionState = cameraPositionState) {
          userLocation?.let {
            Marker(
                tag = TestTags.MAP_USER_LOCATION,
                state = MarkerState(position = it.toLatLng()),
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                contentDescription = TestTags.MAP_USER_LOCATION)
          }

          locations.map { location ->
            AdvancedMarker(
                state = MarkerState(position = location.toLatLng()),
                title = location.title,
            )
          }
        }
    if (isHintPopupOpen) {
      HintPopup(message = "Try pressing your screen to add waypoints!") { isHintPopupOpen = false }
    }
  } else {
    Column(
        modifier =
            Modifier.testTag(TestTags.MAP_NULL_USER_LOCATION)
                .fillMaxSize()
                .padding(innerPaddingValues)
                .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
          Text("Loading your location...", modifier = Modifier.testTag(TestTags.MAP_NULL_ITINERARY))
        }
  }
}
/**
 * This function provides the user with two options for creating an itinerary. The user can either
 * create a known itinerary or create a live itinerary.
 *
 * @param createItineraryViewModel ViewModel for creating an itinerary.
 * @param showLocationSelector MutableState<Boolean> to show the location selector.
 * @param showLiveCreation MutableState<Boolean> to show the live creation.
 */
@Composable
fun ChooseYourWayOfCreation(
    createItineraryViewModel: CreateItineraryViewModel,
    showLocationSelector: MutableState<Boolean>,
    showLiveCreation: MutableState<Boolean>
) {

  BottomAppBar(
      modifier = Modifier.height(200.dp).fillMaxWidth(),
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      contentColor = MaterialTheme.colorScheme.primary,
  ) {
    Column(
        verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
              text = "Start creating an Itinerary!",
              style = MaterialTheme.typography.bodyLarge,
              modifier = Modifier.padding(5.dp))
          Spacer(modifier = Modifier.height(50.dp))
          Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = {
                  createItineraryViewModel.createItineraryManually = true
                  showLocationSelector.value = true
                },
                modifier =
                    Modifier.fillMaxWidth(0.5f)
                        .testTag(TestTags.CREATE_ITINERARY_MANUALLY_BUTTON)) {
                  Text("Create a known itinerary", textAlign = TextAlign.Center)
                }
            Button(
                onClick = {
                  createItineraryViewModel.createItineraryManually = true
                  showLiveCreation.value = true
                },
                modifier =
                    Modifier.fillMaxWidth().testTag(TestTags.CREATE_ITINERARY_BY_TRACKING_BUTTON)) {
                  Text("Create a live itinerary", textAlign = TextAlign.Center)
                }
          }
          Spacer(modifier = Modifier.height(50.dp))
        }
  }
}

/**
 * This function displays a bottom bar with options to add a location or restart the itinerary. It
 * also displays a list of locations added by the user.
 *
 * @param showLocationSelector MutableState<Boolean> to control the visibility of the location
 *   selector.
 * @param locations List<Location> containing the locations added by the user.
 * @param resetLocations Function to reset the locations.
 * @param navController NavHostController for navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelector(
    showLocationSelector: MutableState<Boolean>,
    locations: List<Location>,
    resetLocations: () -> Unit,
    navController: NavHostController,
) {
  BottomAppBar(
      modifier = Modifier.height(250.dp),
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      contentColor = MaterialTheme.colorScheme.primary,
  ) {
    Column(
        modifier = Modifier.fillMaxSize().testTag(TestTags.CREATE_ITINERARY_MANUALLY_SCREEN),
        verticalArrangement = Arrangement.SpaceBetween) {
          Button(onClick = { showLocationSelector.value = false }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
          }

          Box(modifier = Modifier.fillMaxWidth().height(128.dp)) {
            if (locations.isEmpty()) {
              Text(
                  modifier = Modifier.fillMaxWidth().padding(8.dp),
                  text = "You have no waypoints yet, try adding some")
            } else {
              LazyColumn(horizontalAlignment = Alignment.Start) {
                items(locations) { loc ->
                  Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier =
                          Modifier.padding(4.dp)
                              .fillMaxWidth()
                              .border(
                                  1.dp,
                                  MaterialTheme.colorScheme.outline,
                                  shape = RoundedCornerShape(4.dp))
                              .testTag("${TestTags.CREATE_ITINERARY_LOCATION}_${loc.title}")) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = "Location 2",
                            modifier = Modifier.padding(start = 10.dp))
                        Text(text = "${loc.title}, ${loc.address}", fontSize = 15.sp, maxLines = 1)
                      }
                }
              }
            }
          }
          Row(
              modifier = Modifier.fillMaxWidth().padding(8.dp),
              horizontalArrangement = Arrangement.SpaceEvenly) {
                OutlinedButton(
                    onClick = { navController.navigate("ChooseLocationSearch") },
                    modifier = Modifier.testTag(TestTags.ADD_LOCATION_BUTTON)) {
                      Row {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                        Text("Add Location")
                      }
                    }
                OutlinedButton(
                    onClick = { resetLocations() },
                    modifier = Modifier.testTag(TestTags.RESTART_ITINERARY_BUTTON)) {
                      Row {
                        Icon(imageVector = Icons.Filled.RestartAlt, contentDescription = null)
                        Text("Restart itinerary")
                      }
                    }
              }
        }
  }
}

const val LOCATION_UPDATE_INTERVAL_MILLIS: Long = 1_000 // 1 second
/**
 * This function provides the user with the ability to create a live itinerary. It starts tracking
 * the user's location and adds it to the itinerary as the user moves.
 *
 * @param showLiveCreation MutableState<Boolean> to control the visibility of the live creation.
 * @param createItineraryViewModel ViewModel for creating an itinerary.
 * @param addLiveLocations Function to add live locations to the itinerary.
 */
@Composable
fun CreateLiveItinerary(
    showLiveCreation: MutableState<Boolean>,
    createItineraryViewModel: CreateItineraryViewModel,
    addLiveLocations: (Location) -> Unit,
) {

  var isStarted by remember { mutableStateOf(false) }
  BottomAppBar(
      modifier =
          Modifier.height(250.dp)
              .fillMaxWidth()
              .testTag(TestTags.CREATE_ITINERARY_BY_TRACKING_SCREEN),
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      contentColor = MaterialTheme.colorScheme.primary,
  ) {
    Column {
      Button(
          onClick = { showLiveCreation.value = false },
          modifier = Modifier.testTag(TestTags.BACK_BUTTON)) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
          }

      // Center the Row within the BottomAppBar
      Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        val context = LocalContext.current

        Row {
          // Button to start
          Button(
              onClick = {
                isStarted = true
                createItineraryViewModel.startLocationTracking(
                    LOCATION_UPDATE_INTERVAL_MILLIS, addLiveLocations)
                Intent(context, LocationService::class.java).apply {
                  action = LocationService.ACTION_START
                  context.startService(this)
                }
                Log.d("CreateLiveItinerary", "currently creating live itinerary")
              },
              enabled = !isStarted, // Button is disabled if isStarted is true
              colors =
                  ButtonDefaults.buttonColors(
                      if (!isStarted) MaterialTheme.colorScheme.primary
                      else MaterialTheme.colorScheme.surfaceVariant),
              modifier = Modifier.testTag(TestTags.START_BUTTON)) {
                Text("Start")
              }
          Spacer(Modifier.width(8.dp)) // Add space between buttons

          // Button to stop
          Button(
              onClick = {
                isStarted = false
                createItineraryViewModel.stopLocationTracking()
                Intent(context, LocationService::class.java).apply {
                  action = LocationService.ACTION_STOP
                  context.startService(this)
                }
                Log.d("CreateLiveItinerary", "currently stopping live itinerary")
              },
              enabled = isStarted, // Button is disabled if isStarted is false
              colors =
                  ButtonDefaults.buttonColors(
                      if (isStarted) MaterialTheme.colorScheme.error
                      else MaterialTheme.colorScheme.surfaceVariant),
              modifier = Modifier.testTag(TestTags.STOP_BUTTON)) {
                Text("Stop")
              }
        }
      }
    }
  }
}
