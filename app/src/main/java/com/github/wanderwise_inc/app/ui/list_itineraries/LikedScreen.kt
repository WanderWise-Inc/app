package com.github.wanderwise_inc.app.ui.list_itineraries

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.home.SearchBar
import com.github.wanderwise_inc.app.ui.popup.HintPopup
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

@Composable
fun LikedScreen(
    itineraryViewModel: ItineraryViewModel,
    profileViewModel: ProfileViewModel,
    navController: NavHostController,
    imageRepository: ImageRepository
) {
  val sliderPositionPriceState = remember { mutableStateOf(0f..100f) }
  val sliderPositionTimeState = remember { mutableStateOf(0f..24f) }
  DisplayLikedItineraries(
      itineraryViewModel = itineraryViewModel,
      profileViewModel = profileViewModel,
      navController = navController,
      sliderPositionPriceState = sliderPositionPriceState,
      sliderPositionTimeState = sliderPositionTimeState,
      imageRepository = imageRepository)
}

/** Displays itineraries liked by the user */
@Composable
fun DisplayLikedItineraries(
    itineraryViewModel: ItineraryViewModel,
    profileViewModel: ProfileViewModel,
    navController: NavHostController,
    sliderPositionPriceState: MutableState<ClosedFloatingPointRange<Float>>,
    sliderPositionTimeState: MutableState<ClosedFloatingPointRange<Float>>,
    imageRepository: ImageRepository
) {

  /* the categories that can be selected by the user during filtering */
  val categoriesList = ItineraryTags.toSearchCategoryList()

  val uid = profileViewModel.getUserUid()
  var selectedIndex by remember { mutableIntStateOf(0) }
  var searchQuery by remember { mutableStateOf("") }
  var priceRange by remember { mutableFloatStateOf(0f) }

  val itineraryUids by
      profileViewModel.getLikedItineraries(uid).collectAsState(initial = emptyList())
  val itineraries by
      itineraryViewModel.getItineraryFromUids(itineraryUids).collectAsState(initial = emptyList())

  Scaffold(
      topBar = {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().testTag(TestTags.CATEGORY_SELECTOR)) {
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
      floatingActionButton = {
        FloatingActionButton(
            onClick = { itineraryViewModel.saveItineraries(itineraries) },
            containerColor = MaterialTheme.colorScheme.secondaryContainer) {
              Icon(
                  imageVector = Icons.Outlined.Save,
                  contentDescription = "Save Itineraries",
                  tint = MaterialTheme.colorScheme.onSecondaryContainer,
                  modifier = Modifier.size(40.dp).padding(2.dp))
            }
      },
      modifier = Modifier.testTag(TestTags.LIKED_SCREEN)) { innerPadding ->
        val filtered =
            itineraries
                .filter { itinerary ->
                  if (selectedIndex == 0) {
                    true
                  } else {
                    itinerary.tags.contains(categoriesList[selectedIndex].tag)
                  }
                }
                .filter { itinerary ->
                  searchQuery.isBlank() ||
                      itinerary.title.contains(searchQuery, ignoreCase = true) ||
                      itinerary.description?.contains(searchQuery, ignoreCase = true) ?: false
                }
                .filter { itinerary ->
                  val price = itinerary.price
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
            itineraryViewModel = itineraryViewModel,
            profileViewModel = profileViewModel,
            navController = navController,
            paddingValues = innerPadding,
            parent = ItineraryListParent.LIKED,
            imageRepository = imageRepository)
      }
  var isHintVisible by remember { mutableStateOf(true) }
  if (isHintVisible) {
    HintPopup(message = "Press the floppy disk button to save your liked itineraries for later!") {
      isHintVisible = false
    }
  }
}
