package com.github.wanderwise_inc.app.ui.list_itineraries

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.DEFAULT_USER_UID
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.Tag
import com.github.wanderwise_inc.app.ui.TestTags
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
  const val CATEGORY_SELECTOR = "category selector"
}

@Composable
fun LikedScreen(
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel,
    navController: NavHostController,
    firebaseAuth: FirebaseAuth
) {
  val sliderPositionPriceState = remember { mutableStateOf(0f..100f) }
  val sliderPositionTimeState = remember { mutableStateOf(0f..24f) }
  DisplayLikedItineraries(
      mapViewModel = mapViewModel,
      profileViewModel = profileViewModel,
      navController = navController,
      sliderPositionPriceState = sliderPositionPriceState,
      sliderPositionTimeState = sliderPositionTimeState,
      firebaseAuth = firebaseAuth)
}

/** Displays itineraries liked by the user */
@Composable
fun DisplayLikedItineraries(
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel,
    navController: NavHostController,
    sliderPositionPriceState: MutableState<ClosedFloatingPointRange<Float>>,
    sliderPositionTimeState: MutableState<ClosedFloatingPointRange<Float>>,
    firebaseAuth: FirebaseAuth
) {

  /* the categories that can be selected by the user during filtering */
  val categoriesList =
      listOf(
          SearchCategory(ItineraryTags.ADVENTURE, R.drawable.adventure_icon, "Adventure"),
          SearchCategory(ItineraryTags.LUXURY, R.drawable.adventure_icon, "Shopping"),
          SearchCategory(ItineraryTags.PHOTOGRAPHY, R.drawable.sight_seeing_icon, "Sight Seeing"),
          SearchCategory(ItineraryTags.FOODIE, R.drawable.drinks_icon, "Drinks"),
      )

  val uid = firebaseAuth.currentUser?.uid ?: DEFAULT_USER_UID
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
            modifier = Modifier.fillMaxWidth().testTag(TestTags.LIKED_CATEGORY_COLLECTOR)) {
              SearchBar(
                  onSearchChange = { searchQuery = it },
                  onPriceChange = { priceRange = it },
                  sliderPositionPriceState = sliderPositionPriceState,
                  sliderPositionTimeState = sliderPositionTimeState)

              CategorySelector(
                  selectedIndex = selectedIndex,
                  categoriesList = categoriesList,
                  onCategorySelected = { selectedIndex = it })
            }
      },
      modifier = Modifier.testTag(TestTags.LIKED_SCREEN)) { innerPadding ->
        val filtered =
            itineraries
                .filter { itinerary -> itinerary.tags.contains(categoriesList[selectedIndex].tag) }
                .filter { itinerary ->
                  searchQuery.isBlank() ||
                      itinerary.title.contains(searchQuery, ignoreCase = true) ||
                      itinerary.description?.contains(searchQuery, ignoreCase = true) ?: false
                }
                .filter { itinerary ->
                  val price = itinerary.price.toFloat()
                  price in
                      sliderPositionPriceState.value.start..sliderPositionPriceState.value
                              .endInclusive
                }
                .filter { itinerary ->
                  val time = itinerary.time.toFloat()
                  time in
                      sliderPositionTimeState.value.start..sliderPositionTimeState.value
                              .endInclusive
                }
        ItinerariesListScrollable(
            itineraries = filtered,
            paddingValues = innerPadding,
            mapViewModel = mapViewModel,
            profileViewModel = profileViewModel,
            navController = navController,
            parent = ItineraryListParent.LIKED)
      }
}
