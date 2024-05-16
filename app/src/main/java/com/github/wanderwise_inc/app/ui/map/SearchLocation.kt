package com.github.wanderwise_inc.app.ui.map

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.currentCameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchLocation(
    itineraryViewModel: ItineraryViewModel
) {
    var focusedLocation: Location? by remember { mutableStateOf(null) }
    
    val keyboard = LocalSoftwareKeyboardController.current
    
    val onSearch = { query: String ->
        itineraryViewModel.fetchPlaces(query)
        keyboard?.hide() // hide keyboard once search concludes
        focusedLocation = null // remove marker and focus on new search propositions
    }
    
    val focusOnLocation = { location: Location? ->
        focusedLocation = location
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 1f)
    }

    // updates camera position to center on searched item
    LaunchedEffect(focusedLocation) {
        if (focusedLocation != null) {
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(
                    focusedLocation!!.toLatLng(),
                    13f
                )
            )
        }
    }
    
    Scaffold(
        topBar = { LocationSearchBar(onSearch, focusOnLocation, itineraryViewModel) },
        floatingActionButton = { AddWaypointButton() },
        floatingActionButtonPosition = FabPosition.Center
    ) {padding ->
        GoogleMap(
            modifier = Modifier.padding(padding),
            cameraPositionState = cameraPositionState
        ) {
            if (focusedLocation != null) {
                AdvancedMarker(
                    state = MarkerState(position = focusedLocation!!.toLatLng()),
                    title = focusedLocation!!.title
                )
            }
        }        
    }
}

@Composable
fun AddWaypointButton() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .size(200.dp, 40.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { }
    ) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = "add waypoint icon")
        Text(text = "Add Waypoint")
    }
}
