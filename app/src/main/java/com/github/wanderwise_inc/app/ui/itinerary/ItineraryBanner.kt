package com.github.wanderwise_inc.app.ui.itinerary

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.model.location.Itinerary

@Composable
fun ItineraryBanner(
    itinerary: Itinerary,
    onLikeButtonClick: (Itinerary, Boolean) -> Unit,
    onBannerClick: () -> Unit,
    isLikedInitially: Boolean = false
) {

  val imageId = R.drawable.underground_2725336_1280

  var isLiked by remember { mutableStateOf(isLikedInitially) }
  var numLikes by remember { mutableIntStateOf(itinerary.numLikes) }

  ElevatedCard(
      colors =
          CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant,
          ),
      elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
      shape = RoundedCornerShape(13.dp),
      onClick = { onBannerClick() }) {
        Column(
            modifier =
                Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                    .fillMaxWidth()
                    .aspectRatio(1.34f),
            horizontalAlignment = Alignment.CenterHorizontally) {

              // Image of the itinerary
              Image(
                  painter = painterResource(id = imageId),
                  contentDescription = itinerary.description,
                  modifier =
                      Modifier.fillMaxWidth().fillMaxHeight(0.55f).clip(RoundedCornerShape(13.dp)),
                  contentScale = ContentScale.Crop,
                  alignment = Alignment.TopCenter)

              Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier =
                        Modifier.fillMaxHeight().weight(0.6f).padding(10.dp, 8.dp, 4.dp, 15.dp),
                    verticalArrangement = Arrangement.SpaceAround) {

                      // Primary Text Field
                      Text(
                          text = itinerary.title,
                          color = MaterialTheme.colorScheme.primary,
                          fontFamily = FontFamily.Monospace,
                          fontSize = 16.sp,
                          fontWeight = FontWeight.Bold,
                          modifier = Modifier.padding(5.dp)
                          // textDecoration = TextDecoration.Underline
                          )

                      // Secondary indicator fields
                      Text(
                          text = "Wandered by - ",
                          color = MaterialTheme.colorScheme.secondary,
                          fontFamily = FontFamily.Monospace,
                          fontSize = 12.sp,
                          modifier = Modifier.padding(10.dp, 0.dp))

                      Text(
                          text = "Estimated time : - hours",
                          color = MaterialTheme.colorScheme.secondary,
                          fontFamily = FontFamily.Monospace,
                          fontSize = 12.sp,
                          modifier = Modifier.padding(10.dp, 0.dp))

                      Text(
                          text = "Average Expense : -",
                          color = MaterialTheme.colorScheme.secondary,
                          fontFamily = FontFamily.Monospace,
                          fontSize = 12.sp,
                          modifier = Modifier.padding(10.dp, 0.dp))
                    }
                Column(
                    modifier =
                        Modifier.weight(0.3f).fillMaxSize().padding(4.dp, 8.dp, 10.dp, 15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween) {
                      LazyHorizontalStaggeredGrid(
                          rows = StaggeredGridCells.Adaptive(20.dp),
                          horizontalItemSpacing = 10.dp,
                          verticalArrangement = Arrangement.spacedBy(4.dp),
                          content = {
                            items(itinerary.tags) {
                              Card(
                                  colors =
                                      CardDefaults.cardColors(
                                          containerColor = MaterialTheme.colorScheme.secondary,
                                      ),
                                  modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max)) {
                                    Text(
                                        text = it,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(4.dp))
                                  }
                            }
                          },
                          modifier = Modifier.fillMaxWidth().weight(0.4f).padding(2.dp))

                      Column(
                          modifier = Modifier.fillMaxWidth().weight(0.5f),
                          horizontalAlignment = Alignment.CenterHorizontally) {
                            // Like Icon
                            Icon(
                                painter = painterResource(id = R.drawable.liked_icon),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier =
                                    Modifier.size(width = 40.dp, height = 40.dp)
                                        .clickable(
                                            onClick = {
                                              if (isLiked) numLikes-- else numLikes++
                                              onLikeButtonClick(itinerary, isLiked)
                                              isLiked = !isLiked
                                            }))

                            Text(
                                text = "$numLikes Likes",
                                color = MaterialTheme.colorScheme.secondary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(2.dp))
                          }
                    }
              }
            }
      }
}

/**
 * @param onLikeClick callback that is called when the like button is pressed
 * @brief pretty display for an itinerary
 */
@Composable
@Deprecated("Old implementation")
fun ItineraryBanner__(
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
