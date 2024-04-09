package com.github.wanderwise_inc.app.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.Location
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * @brief previews an itinerary
 */
@Composable
fun PreviewItineraryScreen(itinerary: Itinerary) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(itinerary.computeCenterOfGravity().toLatLng(), 13f)
    }

    Scaffold (
        bottomBar =  {
            ItineraryBanner(itinerary = itinerary)
        }
    ) { paddingValues ->
        GoogleMap (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            cameraPositionState = cameraPositionState
        ) {
            itinerary.locations.map { location ->
                AdvancedMarker(
                    state = MarkerState(position = location.toLatLng())
                )
            }
        }
    }
}

/**
 * @brief banner for display itinerary information
 */
@Composable
fun ItineraryBanner(itinerary: Itinerary) {
    var hide by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = itinerary.title,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace,
                fontSize = 30.sp,
                modifier = Modifier.padding(10.dp) // Adjust padding as needed
            )
            Text(
                text = itinerary.description ?: "",
                color = MaterialTheme.colorScheme.secondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 15.sp,
                modifier = Modifier.padding(10.dp) // Adjust padding as needed
            )
        }
    }
}

@Composable
fun DummyPreviewItinerary() {
    val beforeSpot = Location(46.52038535030108, 6.587104433969897)
    val balelecEntry = Location(46.52055514700082, 6.5684791757291885)

    val itinerary = Itinerary(
        userUid = "",
        locations = listOf(beforeSpot, balelecEntry),
        title = "Balelec",
        description = "Before at beach followed by balelec",
        tags = listOf(ItineraryTags.SOCIAL),
        visible = false
    )
    
    PreviewItineraryScreen(itinerary = itinerary)
}
