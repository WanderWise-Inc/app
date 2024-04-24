package com.github.wanderwise_inc.app.ui.itinerary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.PlacesReader


@Preview(showBackground = true)
@Composable
fun ItineraryBanner() {
    val placeReader = PlacesReader(null)
    val locations = placeReader.readFromString()

    val itinerary =
        Itinerary(
            userUid = "",
            locations = locations,
            title = "San Francisco Bike Itinerary",
            tags = listOf(ItineraryTags.CULTURAL, ItineraryTags.NATURE, ItineraryTags.BUDGET),
            description = "A 3-day itinerary to explore the best of San Francisco on a bike.",
            visible = true)

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .size(width = 240.dp, height = 100.dp)
    ) {
        Text(
            text = "Elevated",
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )
    }


//    var hide by remember { mutableStateOf(false) }
//
//
//    Box(
//      modifier =
//          Modifier.background(MaterialTheme.colorScheme.background)
//              .height(200.dp)
//              .clip(RoundedCornerShape(16.dp)),
//      contentAlignment = Alignment.Center,
//  ) {
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//      Text(
//          text = itinerary.title,
//          color = MaterialTheme.colorScheme.primary,
//          fontFamily = FontFamily.Monospace,
//          fontSize = 30.sp,
//          modifier = Modifier.padding(10.dp) // Adjust padding as needed
//          )
//      Text(
//          text = itinerary.description ?: "",
//          color = MaterialTheme.colorScheme.secondary,
//          fontFamily = FontFamily.Monospace,
//          fontSize = 15.sp,
//          modifier = Modifier.padding(10.dp) // Adjust padding as needed
//          )
//    }
//  }
}
