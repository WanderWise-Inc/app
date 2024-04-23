package com.github.wanderwise_inc.app.ui.itinerary

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

@Composable
fun ItineraryBanner(
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel,
    userUid: String, 
    itinerary: Itinerary
) {
  var hide by remember { mutableStateOf(false) }

  Box(
      modifier =
      Modifier
          .background(MaterialTheme.colorScheme.background)
          .height(200.dp)
          .clip(RoundedCornerShape(16.dp)),
      contentAlignment = Alignment.Center,
  ) {
      Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
      ) {
          // Left side text (title, description, time, ...)
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                  text = itinerary.title,
                  color = MaterialTheme.colorScheme.primary,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 20.sp,
                  modifier = Modifier.padding(10.dp) // Adjust padding as needed
              )
              Text(
                  text = itinerary.description ?: "",
                  color = MaterialTheme.colorScheme.secondary,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  modifier = Modifier.padding(10.dp) // Adjust padding as needed
              )
          }
          
          // Right side information (stars, likes, tags)
          Column(verticalArrangement = Arrangement.Bottom) { // Must change arrangement when adding more info
              var liked by remember { mutableStateOf(profileViewModel.checkIfItineraryIsLiked(userUid, itinerary.uid)) }
              var numLikes by remember { mutableIntStateOf(itinerary.numLikes) }
              OutlinedButton(
                  onClick = {
                      if (!liked) { // The Itinerary is already liked by the current user
                          mapViewModel.incrementItineraryLikes(itinerary)
                          profileViewModel.addLikedItinerary(userUid, itinerary.uid)
                          numLikes++
                          Log.d("LIKES_INCREMENTED", "number of likes = $numLikes")
                      } else {
                          mapViewModel.decrementItineraryLikes(itinerary)
                          profileViewModel.removeLikedItinerary(userUid, itinerary.uid)
                          numLikes--
                          Log.d("LIKES_DECREMENTED", "number of likes = $numLikes")
                      }
                      liked = !liked
                  },
                  border = BorderStroke(0.dp, MaterialTheme.colorScheme.background)
                  
              ) {
                  Box(modifier = Modifier.size(30.dp),
                      contentAlignment = Alignment.Center
                  ) {    
                      var alpha = if (liked) { 1f } else { 0.5f }
                      Image(
                          painter = painterResource(id = R.drawable.liked_icon),
                          contentDescription = "Liked button fow Itinerary",
                          alpha = alpha
                      )
                      Text(text = numLikes.toString(), modifier = Modifier.alpha(alpha))
                  }
              }
          }
      }
  }
}