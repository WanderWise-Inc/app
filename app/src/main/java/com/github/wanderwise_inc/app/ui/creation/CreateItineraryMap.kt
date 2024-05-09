package com.github.wanderwise_inc.app.ui.creation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    mapViewModel: MapViewModel, innerPadding : PaddingValues = PaddingValues(0.dp)
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
  GoogleMap(
      modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .testTag(TestTags.MAP_GOOGLE_MAPS),
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

@Composable
fun SelectLocation(mapViewModel: MapViewModel){
    var location1 by remember { mutableStateOf("") }
    var location2 by remember { mutableStateOf("") }

    Scaffold(

        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .height(250.dp)
                    .fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {

            Column {
                /*Text(
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center,
                    text = "Create a new itinerary",
                )*/
                Row(verticalAlignment = Alignment.CenterVertically){
                    Icon(Icons.Filled.LocationOn, contentDescription ="Location 1", modifier = Modifier.padding(start = 10.dp) )
                    OutlinedTextField(
                        value = location1,
                        onValueChange = { location1 = it },
                        label = { Text("location 1...") },
                        placeholder = { Text("location 1...") },
                        modifier = Modifier.padding(start = 25.dp),
                        shape = RoundedCornerShape(20.dp)
                    )
                }

                Icon(Icons.Filled.MoreVert, contentDescription ="more", modifier = Modifier.padding(start = 10.dp) )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = "Location 2", modifier = Modifier.padding(start = 10.dp))
                OutlinedTextField(
                    value = location2,
                    onValueChange = { location2 = it },
                    label = { Text("location 2...") },
                    placeholder = { Text("location 2...") },
                    modifier = Modifier.padding(start = 25.dp),
                    shape = RoundedCornerShape(20.dp)
                )

                }
            }


            }
        }
    ) { innerPadding ->

        //Modifier.fillMaxSize().padding(paddingValues).testTag(TestTags.MAP_GOOGLE_MAPS),
            CreateItineraryMap(mapViewModel = mapViewModel, innerPadding = innerPadding)
        }
    }



