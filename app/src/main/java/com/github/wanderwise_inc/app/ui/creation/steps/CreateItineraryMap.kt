package com.github.wanderwise_inc.app.ui.creation.steps

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun CreateItineraryMapWithSelector(
    createItineraryViewModel: CreateItineraryViewModel,
) {
    val showLocationSelector = remember { mutableStateOf(false) }
    val showLiveCreation = remember { mutableStateOf(false) }
    Scaffold(
        bottomBar = {
            if (!showLocationSelector.value && !showLiveCreation.value) {
                // CreateLiveItinerary()
                ChooseYourWayOfCreation(createItineraryViewModel, showLocationSelector, showLiveCreation)
            } else if (showLiveCreation.value) {
                CreateLiveItinerary(showLiveCreation, createItineraryViewModel)
            } else {
                LocationSelector(createItineraryViewModel, showLocationSelector)
            }
        }) { innerPadding ->
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
            Modifier.padding(paddingValues = innerPaddingValues).testTag(TestTags.MAP_GOOGLE_MAPS),
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
            Modifier.testTag(TestTags.MAP_NULL_ITINERARY)
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
fun ChooseYourWayOfCreation(
    createItineraryViewModel: CreateItineraryViewModel,
    showLocationSelector: MutableState<Boolean>,
    showLiveCreation: MutableState<Boolean>
) {

    BottomAppBar(
        modifier = Modifier.height(250.dp).fillMaxWidth(),
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
                    onClick = { showLocationSelector.value = true },
                    modifier = Modifier.fillMaxWidth(0.5f)) {
                    Text("Create a known itinerary", textAlign = TextAlign.Center)
                }
                Button(
                    onClick = { showLiveCreation.value = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Create a live itinerary", textAlign = TextAlign.Center)
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun LocationSelector(
    createItineraryViewModel: CreateItineraryViewModel,
    showLocationSelector: MutableState<Boolean>
) {
    var location1 by remember { mutableStateOf("") }

    var location2 by remember { mutableStateOf("") }

    BottomAppBar(
        modifier = Modifier.height(250.dp).fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.primary,
    ) {
        Column {
            Button(onClick = { showLocationSelector.value = false }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
            }
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
                    modifier = Modifier.padding(start = 25.dp).testTag(TestTags.FIRST_LOCATION),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true)
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
                    modifier = Modifier.padding(start = 25.dp).testTag(TestTags.SECOND_LOCATION),
                    shape = RoundedCornerShape(20.dp))
            }
        }
    }
}

const val LOCATION_UPDATE_INTERVAL_MILLIS: Long = 1_000 // 1 second

@Composable
fun CreateLiveItinerary(
    showLiveCreation: MutableState<Boolean>,
    createItineraryViewModel: CreateItineraryViewModel
) {

    var isStarted by remember { mutableStateOf(false) }
    BottomAppBar(
        modifier = Modifier.height(250.dp).fillMaxWidth().testTag(TestTags.LIVE_ITINERARY),
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
                Row {
                    // Button to start
                    Button(
                        onClick = {
                            isStarted = true
                            createItineraryViewModel.startLocationTracking(LOCATION_UPDATE_INTERVAL_MILLIS)
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