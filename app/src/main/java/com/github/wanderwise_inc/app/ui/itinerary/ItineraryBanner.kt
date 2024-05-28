package com.github.wanderwise_inc.app.ui.itinerary

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Payments
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.github.wanderwise_inc.app.model.profile.Profile
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
  if (inCreation) imageRepository.setIsItineraryImage(true)
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
  var isLiked = isLikedInitially
  var numLikes by remember { mutableIntStateOf(itinerary.numLikes) }
  val user by profileViewModel.getProfile(itinerary.userUid).collectAsState(initial = null)

  ElevatedCard(
      colors =
          CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.onTertiary,
          ),
      elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
      shape = RoundedCornerShape(9.dp),
      modifier = Modifier.testTag("${TestTags.ITINERARY_BANNER}_${itinerary.uid}"),
      onClick = { onBannerClick(itinerary) }) {
        Column(
            modifier = Modifier.fillMaxSize().aspectRatio(2f),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.73f)) {
                BannerImage(painter, itinerary)

                BannerLikeButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    isLiked = isLiked,
                    numLikes = numLikes,
                    itinerary = itinerary) {
                      if (isLiked) numLikes-- else numLikes++
                      onLikeButtonClick(itinerary, isLiked)
                      isLiked = !isLiked
                    }
                BannerTags(itinerary, Modifier.align(Alignment.BottomStart))
              }
              BannerTitle(itinerary)
              BannerAttributes(itinerary, numLikes)
            }
      }
}

/**
 * @brief composes the image of the banner
 * @param painter: the image to be drawn
 * @param itinerary: the itinerary of the banner for image description
 */
@Composable
fun BannerImage(painter: AsyncImagePainter, itinerary: Itinerary) {
  Image(
      painter = painter,
      contentDescription = itinerary.description,
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.Crop,
      alignment = Alignment.TopCenter)
  if (painter.state is AsyncImagePainter.State.Loading) {
    CircularProgressIndicator()
  }
}

/**
 * @brief composes the tags of the itinerary
 * @param itinerary: for getting the tags
 * @param modifier: for the placement of the tags on the banner image
 */
@Composable
fun BannerTags(itinerary: Itinerary, modifier: Modifier) {
  Row(
      modifier = modifier.fillMaxWidth().padding(10.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.Bottom) {
        for (tag in itinerary.tags) {
          OutlinedCard(
              shape = CircleShape,
              colors =
                  CardDefaults.cardColors(
                      containerColor = MaterialTheme.colorScheme.secondaryContainer,
                  ),
              modifier = Modifier) {
                Text(
                    text = tag,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 0.dp))
              }
        }
      }
}

/**
 * @brief writes the title of the image
 * @param itinerary: for fetching the title of the image
 */
@Composable
fun BannerTitle(itinerary: Itinerary) {
  Text(
      text = itinerary.title,
      color = MaterialTheme.colorScheme.onPrimaryContainer,
      fontFamily = FontFamily.Monospace,
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier)
}

/**
 * @brief writes the attribute of the itinerary
 * @param itinerary: for "price" and "time"
 * @param numLikes: for the number of likes
 */
@Composable
fun BannerAttributes(itinerary: Itinerary, numLikes: Int) {
  Row(modifier = Modifier.fillMaxHeight(), horizontalArrangement = Arrangement.SpaceBetween) {
    Time(itinerary.time)

    Spacer(Modifier.padding(10.dp))

    Price(itinerary.price)

    Spacer(Modifier.padding(10.dp))

    Likes(numLikes)
  }
}


/**
 * @brief write the style of the time attribute
 */
@Composable
fun Time(time: Int) {
  Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
    Icon(
        imageVector = Icons.Outlined.HourglassEmpty,
        contentDescription = "duration",
        tint = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.size(width = 16.dp, height = 16.dp))
    Text(
        text = "$time hours",
        color = MaterialTheme.colorScheme.secondary,
        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        modifier = Modifier.padding(start = 2.dp),
        textAlign = TextAlign.Center,
    )
  }
}
/**
 * @brief write the style of the price attribute
 */
@Composable
fun Price(price: Float) {
  Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
    Icon(
        imageVector = Icons.Outlined.Payments,
        contentDescription = "price",
        tint = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.size(width = 16.dp, height = 16.dp))
    Text(
        text = "${price}",
        color = MaterialTheme.colorScheme.secondary,
        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        modifier = Modifier.padding(start = 2.dp),
        textAlign = TextAlign.Center,
    )
  }
}

/**
 * @brief write the style of the likes attribute
 */
@Composable
fun Likes(numLikes: Int) {
  Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
    Icon(
        imageVector = Icons.Outlined.FavoriteBorder,
        contentDescription = "number of likes",
        tint = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.size(width = 16.dp, height = 16.dp))
    Text(
        text = "$numLikes likes",
        color = MaterialTheme.colorScheme.secondary,
        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        modifier = Modifier.padding(start = 2.dp),
        textAlign = TextAlign.Center,
    )
  }
}

/**
 * @brief the clickable like button to like an itinerary
 * @param modifier: for positioning the button on the Box composable
 * @param isLiked: a boolean that holds if the value is liked or not
 * @param numLikes: needed to have a parameter to be listened in order to recompose
 * @param itinerary: for the testTag
 * @param onClick: a lambda holding the like logic
 */
@Composable
fun BannerLikeButton(
    modifier: Modifier,
    isLiked: Boolean,
    numLikes: Int, // an argument is needed to be sent on change for it to recompose.
    itinerary: Itinerary,
    onClick: () -> Unit
) {
  // Like Icon
  Box(modifier = modifier.padding(5.dp)) {
    ElevatedCard(
        modifier =
            Modifier.size(35.dp, 35.dp)
                .testTag("${TestTags.ITINERARY_BANNER_LIKE_BUTTON}_${itinerary.uid}"),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        shape = CircleShape,
        onClick = { onClick() }) {
          Icon(
              painter =
                  painterResource(
                      id = if (isLiked) R.drawable.liked_icon_full else R.drawable.liked_icon),
              contentDescription = "like button heart icon",
              tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.secondary,
              modifier = Modifier.fillMaxSize().padding(7.dp))
        }
  }
}
