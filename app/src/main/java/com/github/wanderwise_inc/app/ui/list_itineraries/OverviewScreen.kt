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
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.ui.home.SearchBar
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

@Composable
fun OverviewScreen(mapViewModel: MapViewModel, profileViewModel: ProfileViewModel, navController: NavHostController) {
  val sliderPositionPriceState = remember { mutableStateOf(0f..100f) }
  val sliderPositionTimeState = remember { mutableStateOf(0f..24f) }
  DisplayOverviewItineraries(
      mapViewModel = mapViewModel,
      profileViewModel = profileViewModel,
      navController = navController,
      sliderPositionPriceState = sliderPositionPriceState,
      sliderPositionTimeState = sliderPositionTimeState)
}

/** Displays global itineraries filtered on some predicates */
@Composable
fun DisplayOverviewItineraries(
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel,
    navController: NavHostController,
    sliderPositionPriceState: MutableState<ClosedFloatingPointRange<Float>>,
    sliderPositionTimeState: MutableState<ClosedFloatingPointRange<Float>>
) {

  /* the categories that can be selected by the user during filtering */
  val categoriesList =
      listOf(
          SearchCategory(ItineraryTags.ADVENTURE, R.drawable.adventure_icon, "Adventure"),
          SearchCategory(ItineraryTags.LUXURY, R.drawable.adventure_icon, "Shopping"),
          SearchCategory(ItineraryTags.PHOTOGRAPHY, R.drawable.sight_seeing_icon, "Sight Seeing"),
          SearchCategory(ItineraryTags.FOODIE, R.drawable.drinks_icon, "Drinks"),
      )

  var selectedIndex by remember { mutableIntStateOf(0) }
  var searchQuery by remember { mutableStateOf("") }
  var priceRange by remember { mutableStateOf(0f) }

  val itineraries by mapViewModel.getAllPublicItineraries().collectAsState(initial = listOf())

  Scaffold(
      topBar = {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().testTag("Overview screen")) {
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
      modifier = Modifier.testTag("Liked screen")) { innerPadding ->
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
                  price in sliderPositionPriceState.value.start..sliderPositionPriceState.value.endInclusive
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
            navController = navController)
      }
}
