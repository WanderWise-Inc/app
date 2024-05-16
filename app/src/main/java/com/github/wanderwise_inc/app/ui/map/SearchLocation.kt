package com.github.wanderwise_inc.app.ui.map

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    var query by remember { mutableStateOf("") }

    var isSearching by remember { mutableStateOf(false) }
    val searchedLocations by itineraryViewModel.getPlacesLiveData().observeAsState(emptyList())
    val focusedLocation by remember { mutableStateOf(null) }
    
    val onSearch = { query: String ->
        itineraryViewModel.fetchPlaces(query)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 1f)
    }

    // updates camera position to center on searched item
    LaunchedEffect(searchedLocations) {
        if (searchedLocations.isNotEmpty()) {
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(
                    searchedLocations.first().toLatLng(),
                    13f
                )
            )
        }
    }

    SearchBar(
        placeholder = { Text("Search a location") },
        query = query,
        onQueryChange = { query = it },
        onSearch = { onSearch(query) },
        active = isSearching,
        onActiveChange = { isSearching = !isSearching },
    ) {
        if (searchedLocations.isNotEmpty()) {
            LazyColumn() {
                items(searchedLocations) {loc ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)) {
                        Text(text = loc.address?: "address undefined")
                    }
                }
                Log.d(
                    "LOCATIONS_LLIST",
                    "searchedLocations: ${searchedLocations}"
                )
            }
        }
        GoogleMap(
            //modifier = Modifier.padding(padding),
            cameraPositionState = cameraPositionState
        ) {
            searchedLocations.forEach { location ->
                AdvancedMarker(
                    state = MarkerState(position = location.toLatLng()),
                    title = location.title
                )
            }
        }
        /*if (searchedLocations.isEmpty()) {
            NullItinerary(userLocation)
        } else {
            val cameraPositionState = rememberCameraPositionState {
                CameraPosition.fromLatLngZoom(searchedLocations.first().toLatLng(), 13f)
            }
            
            GoogleMap(
                modifier = Modifier.padding(padding),
                cameraPositionState = cameraPositionState
            )
        }*/
    }
}