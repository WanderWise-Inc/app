package com.github.wanderwise_inc.app.ui.itinerary

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.model.location.Itinerary

/**
 * @param onLikeClick callback that is called when the like button is pressed
 * @brief pretty display for an itinerary
 */
@Composable
fun ItineraryBanner(
    onLikeClick: (Itinerary, Boolean) -> Unit,
    isLikedInitially: Boolean,
    itinerary: Itinerary,
) {
  Box(
      modifier =
          Modifier.background(MaterialTheme.colorScheme.background)
              .height(200.dp)
              .clip(RoundedCornerShape(16.dp)),
      contentAlignment = Alignment.Center,
  ) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
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
          Column(
              verticalArrangement =
                  Arrangement.Bottom) { // Must change arrangement when adding more info
                var liked by remember { mutableStateOf(isLikedInitially) }
                var numLikes by remember { mutableIntStateOf(itinerary.numLikes) }

                OutlinedButton(
                    onClick = {
                      onLikeClick(itinerary, liked)

                      if (!liked) { // The Itinerary is already liked by the current user
                        numLikes++
                        Log.d("LIKES_INCREMENTED", "number of likes = $numLikes")
                      } else {
                        numLikes--
                        Log.d("LIKES_DECREMENTED", "number of likes = $numLikes")
                      }
                      liked = !liked
                    },
                    border = BorderStroke(0.dp, MaterialTheme.colorScheme.background)) {
                      Box(modifier = Modifier.size(30.dp), contentAlignment = Alignment.Center) {
                        val alpha =
                            if (liked) {
                              1f
                            } else {
                              0.5f
                            }
                        Image(
                            painter = painterResource(id = R.drawable.liked_icon),
                            contentDescription = "Liked button fow Itinerary",
                            alpha = alpha)
                        Text(text = numLikes.toString(), modifier = Modifier.alpha(alpha))
                      }
                    }
              }
        }
  }
}
