package com.github.wanderwise_inc.app.ui.list_itineraries

import android.util.Log
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.SearchCategory
import com.github.wanderwise_inc.app.model.profile.DEFAULT_OFFLINE_PROFILE
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.itinerary.ItineraryBanner
import com.github.wanderwise_inc.app.ui.navigation.Destination
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.ui.popup.HintPopup
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

/** @brief reusable UI elements for displaying a list of itineraries */

/** @brief a scrollable list of itineraries */
@Composable
fun ItinerariesListScrollable(
    itineraries: List<Itinerary>,
    itineraryViewModel: ItineraryViewModel,
    profileViewModel: ProfileViewModel,
    navController: NavHostController,
    paddingValues: PaddingValues,
    parent: ItineraryListParent,
    imageRepository: ImageRepository
) {
  if (itineraries.isEmpty()) {
    val parentVerb =
        when (parent) {
          ItineraryListParent.OVERVIEW -> "searched"
          ItineraryListParent.LIKED -> "liked"
          ItineraryListParent.PROFILE -> "created"
        }
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier.padding(paddingValues)
                .testTag(TestTags.ITINERARY_LIST_NULL)
                .fillMaxWidth()
                .height(100.dp)) {
          Text(
              text = "You have not $parentVerb any itineraries yet",
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(5.dp, 10.dp))
        }
  } else {
    val likedItineraries by
        profileViewModel
            .getLikedItineraries(profileViewModel.getUserUid())
            .collectAsState(initial = emptyList())

    LazyColumn(
        modifier =
            Modifier.padding(
                    start = 10.dp,
                    top = paddingValues.calculateTopPadding(),
                    end = 10.dp,
                    bottom = paddingValues.calculateBottomPadding())
                .testTag(TestTags.ITINERARY_LIST_SCROLLABLE),
        verticalArrangement = spacedBy(8.dp)) {

          // simply adding the spacer with zero padding creates a large gap
          item { Spacer(Modifier.padding(0.dp)) }

          this.items(itineraries, key = { it.uid }) { itinerary ->
            val uid = profileViewModel.getUserUid()
            var isLikedInitially by remember {
              mutableStateOf(likedItineraries.contains(itinerary.uid))
            }
            LaunchedEffect(uid) {
              isLikedInitially = profileViewModel.checkIfItineraryIsLiked(uid, itinerary.uid)
              Log.d("ItineraryList", "isLiked = $isLikedInitially")
            }

            val onLikeButtonClick = { it: Itinerary, isLiked: Boolean ->
              // liking should have no effect for the offline profile!
              if (profileViewModel.getUserUid() != DEFAULT_OFFLINE_PROFILE.userUid) {
                if (isLiked) {
                  itineraryViewModel.decrementItineraryLikes(it)
                  profileViewModel.removeLikedItinerary(uid, it.uid)
                } else {
                  itineraryViewModel.incrementItineraryLikes(it)
                  profileViewModel.addLikedItinerary(uid, it.uid)
                }
              }
            }
            val navigationActions = NavigationActions(navController)
            val onBannerClick = { it: Itinerary ->
              itineraryViewModel.setFocusedItinerary(it)
              navigationActions.navigateTo(Destination.TopLevelDestination.Map)
            }
            ItineraryBanner(
                itinerary = itinerary,
                onLikeButtonClick = onLikeButtonClick,
                onBannerClick = onBannerClick,
                isLikedInitially = isLikedInitially,
                imageRepository = imageRepository)
          }

          item { Spacer(Modifier.padding(20.dp)) }
        }
  }
  var offlineHintVisible by remember {
    mutableStateOf(profileViewModel.getUserUid() == DEFAULT_OFFLINE_PROFILE.userUid)
  }
  if (offlineHintVisible) {
    HintPopup(message = "You are currently offline!") { offlineHintVisible = false }
  }
}

/** @brief a bar that allows for selecting a category and updating parent state */
@Composable
fun CategorySelector(
    selectedIndex: Int,
    categoriesList: List<SearchCategory>,
    onCategorySelected: (Int) -> Unit
) {
  ScrollableTabRow(
      selectedTabIndex = selectedIndex,
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      edgePadding = (0).dp, // set to -90.dp to hide "all" tag.
  ) {
    categoriesList.forEachIndexed { index, category ->
      Tab(
          selected = index == selectedIndex,
          onClick = {
            if (index == selectedIndex) {
              onCategorySelected(0)
            } else {
              onCategorySelected(index)
            }
          },
          text = {
            Text(
                text = category.tag,
                modifier = Modifier.padding(0.dp, 2.dp),
                style =
                    TextStyle(
                        fontSize = 9.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight(600),
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.5.sp,
                    ))
          },
          selectedContentColor = MaterialTheme.colorScheme.outline,
          unselectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
          icon = {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                modifier = Modifier.size(30.dp).padding(2.dp))
          },
          modifier = Modifier.testTag("${TestTags.CATEGORY_SELECTOR_TAB}_${index}"))
    }
  }
}

enum class ItineraryListParent {
  OVERVIEW,
  LIKED,
  PROFILE
}
