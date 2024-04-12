package com.github.wanderwise_inc.app.ui.map

import android.content.Context
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags

import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.ui.itinerary.ItineraryBanner
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

import com.github.wanderwise_inc.app.model.location.PlacesReader

import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/** @brief previews an itinerary */
@Composable
fun PreviewItineraryScreen(itinerary: Itinerary) {
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(itinerary.computeCenterOfGravity().toLatLng(), 13f)
  }

  Scaffold(
      bottomBar = { ItineraryBanner(itinerary = itinerary) },
      modifier = Modifier.testTag("Map screen")) { paddingValues ->
        GoogleMap(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            cameraPositionState = cameraPositionState) {
              itinerary.locations.map { location ->
                AdvancedMarker(
                    state = MarkerState(position = location.toLatLng()),
                    title = location.title ?: "",
                )
              }
            }
      }
}

/** @brief banner for display itinerary information */
@Composable
fun ItineraryBanner(itinerary: Itinerary) {
  var hide by remember { mutableStateOf(false) }

  Box(
      modifier =
          Modifier.background(MaterialTheme.colorScheme.background)
              .height(200.dp)
              .clip(RoundedCornerShape(16.dp)),
      contentAlignment = Alignment.Center,
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
fun DummyPreviewItinerary(context: Context) {
  val placeReader = PlacesReader(context)
  val locations = placeReader.readFromJson()

  /*listOf(
      Location(1.35, 103.87, "Marina Bay Sands", "10 Bayfront Ave, Singapore 018956", 4.7f),
      Location(1.2823, 103.8587, "Gardens by the Bay", "18 Marina Gardens Dr, Singapore 018953", 4.7f),
      Location(1.3521, 103.8198, "Singapore Zoo", "80 Mandai Lake Rd, Singapore 729826", 4.6f),
      Location(1.3036, 103.7852, "Jurong Bird Park", "2 Jurong Hill, Singapore 628925", 4.6f),
      Location(1.3521, 103.8198, "Night Safari", "80 Mandai Lake Rd, Singapore 729826", 4.6f),
      Location(1.2895, 103.8493, "Singapore Flyer", "30 Raffles Ave, Singapore 039803", 4.5f),
      Location(1.2892, 103.8456, "Merlion Park", "1 Fullerton Rd, Singapore 049213", 4.6f),
      Location(1.2819, 103.8631, "ArtScience Museum", "6 Bayfront Ave, Singapore 018974", 4.6f),
      Location(1.2897, 103.8501, "Esplanade", "1 Esplanade Dr, Singapore 038981", 4.6f),
      Location(1.2929, 103.8463, "Clarke Quay", "3 River Valley Rd, Singapore 179024", 4.5f),
      Location(1.2829, 103.8544, "Chinatown", "Singapore", 4.5f),
      Location(1.3007, 103.8553, "Little India", "Singapore", 4.5f),
      Location(1.3147, 103.7652, "Pulau Ubin", "Singapore", 4.6f),
      Location(1.3521, 103.8198, "River Safari", "80 Mandai Lake Rd, Singapore 729826", 4.6f)
  )*/

  val itinerary =
      Itinerary(
          userUid = "",
          locations = locations,
          title = "San Francisco Bike Itinerary",
          tags = listOf(ItineraryTags.CULTURAL, ItineraryTags.NATURE, ItineraryTags.BUDGET),
          description = "A 3-day itinerary to explore the best of San Francisco on a bike.",
          visible = true)

  PreviewItineraryScreen(itinerary = itinerary)
}

