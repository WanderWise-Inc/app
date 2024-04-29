package com.github.wanderwise_inc.app.ui.itinerary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
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
    onBannerClick: (Itinerary) -> Unit,
    isLikedInitially: Boolean = false
) {

  val imageId = R.drawable.underground_2725336_1280

  var isLiked by remember { mutableStateOf(isLikedInitially) }
  var numLikes by remember { mutableIntStateOf(itinerary.numLikes) }

  ElevatedCard(
      colors =
          CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
          ),
      elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
      shape = RoundedCornerShape(13.dp),
      onClick = { onBannerClick(itinerary) },
      modifier = Modifier.testTag("Itinerary banner")) {
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
              // Primary Text Field
              Text(
                  text = itinerary.title,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(2.dp)
                  // textDecoration = TextDecoration.Underline
                  )

              Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier =
                        Modifier.fillMaxHeight().weight(0.7f).padding(10.dp, 4.dp, 4.dp, 15.dp),
                    verticalArrangement = Arrangement.SpaceAround) {
                      // Secondary indicator fields
                      Text(
                          text = "Wandered by - ",
                          color = MaterialTheme.colorScheme.secondary,
                          fontFamily = FontFamily.Monospace,
                          fontSize = 12.sp,
                          modifier = Modifier.padding(4.dp, 0.dp))

                      Text(
                          text = "Estimated time : - hours",
                          color = MaterialTheme.colorScheme.secondary,
                          fontFamily = FontFamily.Monospace,
                          fontSize = 12.sp,
                          modifier = Modifier.padding(4.dp, 0.dp))

                      Text(
                          text = "Average Expense : -",
                          color = MaterialTheme.colorScheme.secondary,
                          fontFamily = FontFamily.Monospace,
                          fontSize = 12.sp,
                          modifier = Modifier.padding(4.dp, 0.dp))
                    }
                Column(
                    modifier = Modifier.weight(0.3f).fillMaxSize().padding(4.dp, 2.dp, 10.dp, 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                  LazyRow(
                      modifier = Modifier.fillMaxWidth().weight(0.3f).padding(2.dp),
                      contentPadding = PaddingValues(horizontal = 2.dp, vertical = 5.dp),
                      horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(itinerary.tags) {
                          OutlinedCard(
                              colors =
                                  CardDefaults.cardColors(
                                      containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                  ),
                              modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = it,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                              }
                        }
                      }

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
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(2.dp))
                      }
                }
              }
            }
      }
}
