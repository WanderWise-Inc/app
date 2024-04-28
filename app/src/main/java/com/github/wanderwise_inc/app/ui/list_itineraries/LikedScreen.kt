package com.github.wanderwise_inc.app.ui.list_itineraries

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.wanderwise_inc.app.DEFAULT_USER_UID
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.Tag
import com.github.wanderwise_inc.app.ui.home.SearchBar
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

/** @brief Search categories displayed on the top bar. */
data class SearchCategory(
    val tag: Tag,
    val icon: Int,
    val title: String,
)

object LikedScreenTestTags {
    const val SCREEN = "Liked Screen"
    const val CATEGORY_SELECTOR = "category selector"
}

@Composable
fun LikedScreen(mapViewModel: MapViewModel, profileViewModel: ProfileViewModel) {
  DisplayLikedItineraries(mapViewModel = mapViewModel, profileViewModel = profileViewModel)
}

/** Displays itineraries liked by the user */
@Composable
fun DisplayLikedItineraries(mapViewModel: MapViewModel, profileViewModel: ProfileViewModel) {

  /* the categories that can be selected by the user during filtering */
  val categoriesList =
      listOf(
          SearchCategory(ItineraryTags.ADVENTURE, R.drawable.adventure_icon, "Adventure"),
          SearchCategory(ItineraryTags.LUXURY, R.drawable.adventure_icon, "Shopping"),
          SearchCategory(ItineraryTags.PHOTOGRAPHY, R.drawable.sight_seeing_icon, "Sight Seeing"),
          SearchCategory(ItineraryTags.FOODIE, R.drawable.drinks_icon, "Drinks"),
      )

  val uid = FirebaseAuth.getInstance().uid ?: DEFAULT_USER_UID
  var selectedIndex by remember { mutableIntStateOf(0) }
  var searchQuery by remember { mutableStateOf("") }
  var priceRange by remember { mutableStateOf(0f) }

  val itineraryUids by profileViewModel.getLikedItineraries(uid).collectAsState(initial = listOf())
  val itineraries by
      mapViewModel.getItineraryFromUids(itineraryUids).collectAsState(initial = listOf())

  Scaffold(
      topBar = {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().testTag(LikedScreenTestTags.CATEGORY_SELECTOR)) {
              SearchBar(onSearchChange = { searchQuery = it }, onPriceChange = { priceRange = it })

              CategorySelector(
                  selectedIndex = selectedIndex,
                  categoriesList = categoriesList,
                  onCategorySelected = { selectedIndex = it })
            }
      },
      modifier = Modifier.testTag(LikedScreenTestTags.SCREEN)) { innerPadding ->
        val filtered =
            itineraries
                .filter { itinerary -> itinerary.tags.contains(categoriesList[selectedIndex].tag) }
                .filter { itinerary ->
                  searchQuery.isBlank() ||
                      itinerary.title.contains(searchQuery, ignoreCase = true) ||
                      itinerary.description?.contains(searchQuery, ignoreCase = true) ?: false
                }

        if (filtered.isEmpty()) {
          Log.d("EMPTY_ITINERARIES", "there are no liked itineraries")
          Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier.padding(innerPadding).fillMaxWidth().height(100.dp)
          ) {
              Text(
                  text = "You have not liked any corresponding itineraries yet",
                  //color = MaterialTheme.colorScheme.
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(5.dp, 10.dp)
              )
          } //PaModifier.padding(5.dp, 10.dp))
        } else {
            ItinerariesListScrollable(
                itineraries = filtered,
                paddingValues = innerPadding,
                mapViewModel = mapViewModel,
                profileViewModel = profileViewModel
            )
        }
      }
}
