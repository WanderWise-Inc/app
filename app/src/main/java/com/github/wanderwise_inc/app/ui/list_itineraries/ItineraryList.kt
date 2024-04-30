package com.github.wanderwise_inc.app.ui.list_itineraries

import android.util.Log
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.DEFAULT_USER_UID
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.ui.itinerary.ItineraryBanner
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

/** @brief reusable UI elements for displaying a list of itineraries */

/** @brief a scrollable list of itineraries */
@Composable
fun ItinerariesListScrollable(
    itineraries: List<Itinerary>,
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel,
    paddingValues: PaddingValues,
    parent: ItineraryListParent
) {
  if (itineraries.isEmpty()){
      val parentVerb = when(parent) {
          ItineraryListParent.OVERVIEW -> "searched"
          ItineraryListParent.LIKED -> "liked"
          ItineraryListParent.PROFILE -> "created"
      }
      Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.padding(paddingValues).fillMaxWidth().height(100.dp)) {
          Text(
              text = "You have not $parentVerb any itineraries yet",
              // color = MaterialTheme.colorScheme.
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(5.dp, 10.dp))
      } // PaModifier.padding(5.dp, 10.dp))
  } else {
      LazyColumn(
          modifier = Modifier.padding(paddingValues).testTag("Scrollable itineraries"),
          verticalArrangement = spacedBy(15.dp)
      ) {
          this.items(itineraries) { itinerary ->
              val uid = FirebaseAuth.getInstance().uid ?: DEFAULT_USER_UID
              val isLikedInitially = profileViewModel.checkIfItineraryIsLiked(uid, itinerary.uid)
              val onLikeButtonClick = { it: Itinerary, isLiked: Boolean ->
                  if (isLiked) {
                      mapViewModel.decrementItineraryLikes(it)
                      profileViewModel.removeLikedItinerary(uid, it.uid)
                  } else {
                      mapViewModel.incrementItineraryLikes(it)
                      profileViewModel.addLikedItinerary(uid, it.uid)
                  }
              }
              val onBannerClick = {}
              ItineraryBanner(
                  itinerary = itinerary,
                  onLikeButtonClick = onLikeButtonClick,
                  onBannerClick = onBannerClick,
                  isLikedInitially = isLikedInitially
              )
          }
      }
  }
}

/** @brief a bar that allows for selecting a category and updating parent state */
@Composable
fun CategorySelector(
    selectedIndex: Int,
    categoriesList: List<SearchCategory>,
    onCategorySelected: (Int) -> Unit
) {
  TabRow(
      selectedTabIndex = selectedIndex,
      containerColor = MaterialTheme.colorScheme.surfaceVariant,
  ) {
    categoriesList.forEachIndexed { index, category ->
      Tab(
          selected = index == selectedIndex,
          onClick = { onCategorySelected(index) },
          text = {
            Text(
                text = category.title,
                modifier = Modifier.padding(0.dp, 2.dp),
                style =
                    TextStyle(
                        fontSize = 9.sp,
                        lineHeight = 16.sp,
                        // fontFamily = FontFamily(Font(R.font.roboto)),
                        fontWeight = FontWeight(600),
                        color = Color(0xFF191C1E),
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.5.sp,
                    ))
          },
          icon = {
            Icon(
                painter = painterResource(id = category.icon),
                contentDescription = null,
                tint = Color(0xFF191C1E),
                modifier = Modifier.size(30.dp).padding(2.dp))
          })
    }
  }
}


enum class ItineraryListParent{
    OVERVIEW, LIKED, PROFILE
}