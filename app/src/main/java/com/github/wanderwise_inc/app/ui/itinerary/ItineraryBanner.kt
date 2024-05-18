package com.github.wanderwise_inc.app.ui.itinerary

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

@Composable
fun ItineraryBanner(
    itinerary: Itinerary,
    onLikeButtonClick: (Itinerary, Boolean) -> Unit,
    onBannerClick: (Itinerary) -> Unit,
    isLikedInitially: Boolean = false,
    profileViewModel: ProfileViewModel,
    imageRepository: ImageRepository,
    inCreation: Boolean = false
) {
  val currentImage = imageRepository.getCurrentFile()
  val imageFlow =
      remember(itinerary) { imageRepository.fetchImage("itineraryPictures/${itinerary.uid}") }
  val image by imageFlow.collectAsState(initial = null)
  val model =
      if (inCreation) {
        Log.d("ItineraryBanner", "In creation")
        ImageRequest.Builder(LocalContext.current)
            .data(currentImage)
            .error(R.drawable.underground)
            .crossfade(500)
            .build()
      } else {
        ImageRequest.Builder(LocalContext.current)
            .data(image)
            .error(R.drawable.underground)
            .crossfade(500)
            .build()
      }

  val painter = rememberAsyncImagePainter(model = model)
  if (painter.state is AsyncImagePainter.State.Loading) {
    CircularProgressIndicator()
  }
  var isLiked = isLikedInitially
  var numLikes by remember { mutableIntStateOf(itinerary.numLikes) }
  var prices by remember { mutableFloatStateOf(itinerary.price) }
  var times by remember { mutableIntStateOf(itinerary.time) }
  val user by profileViewModel.getProfile(itinerary.userUid).collectAsState(initial = null)

  ElevatedCard(
      colors =
          CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
          ),
      elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
      shape = RoundedCornerShape(13.dp),
      modifier = Modifier.testTag("${TestTags.ITINERARY_BANNER}_${itinerary.uid}"),
      onClick = { onBannerClick(itinerary) }) {
        Column(
            modifier =
                Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                    .fillMaxWidth()
                    .aspectRatio(1.34f),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Image(
                  painter = painter,
                  contentDescription = itinerary.description,
                  modifier =
                      Modifier.fillMaxWidth().fillMaxHeight(0.55f).clip(RoundedCornerShape(13.dp)),
                  contentScale = ContentScale.Crop,
                  alignment = Alignment.TopCenter)
              // Image of the itinerary
              // Primary Text Field
              Text(
                  text = itinerary.title,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(2.dp))

              Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier =
                        Modifier.fillMaxHeight().weight(0.7f).padding(10.dp, 4.dp, 4.dp, 15.dp),
                    verticalArrangement = Arrangement.SpaceAround) {
                      // Secondary indicator fields
                      val textUser = if (user != null) user!!.displayName else "-"
                      Text(
                          text = "Wandered by $textUser",
                          color = MaterialTheme.colorScheme.secondary,
                          fontFamily = FontFamily.Monospace,
                          fontSize = 12.sp,
                          modifier = Modifier.padding(4.dp, 0.dp))

                      Text(
                          text = "Estimated time: $times hours",
                          color = MaterialTheme.colorScheme.secondary,
                          fontFamily = FontFamily.Monospace,
                          fontSize = 12.sp,
                          modifier = Modifier.padding(4.dp, 0.dp))

                      Text(
                          text = "Average Expense: $prices",
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
                            painter =
                                painterResource(
                                    id =
                                        if (isLiked) R.drawable.liked_icon_full
                                        else R.drawable.liked_icon),
                            contentDescription = "like button heart icon",
                            tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.secondary,
                            modifier =
                                Modifier.testTag(
                                        "${TestTags.ITINERARY_BANNER_LIKE_BUTTON}_${itinerary.uid}")
                                    .size(width = 40.dp, height = 40.dp)
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
