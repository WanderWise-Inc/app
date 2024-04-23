package com.github.wanderwise_inc.app.ui.liked

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.Tag
import com.github.wanderwise_inc.app.ui.home.SearchBar
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.flow

/** @brief Search categories displayed on the top bar. */
data class SearchCategory(
    val tag: Tag,
    val icon: Int,
    val title: String,
)

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

  var selectedIndex by remember { mutableIntStateOf(0) }
  var searchQuery by remember { mutableStateOf("") }

  // TODO fetch liked itineraries from profileViewModel

  // for testing purposes
  val itineraryAdventureAndLuxury = Itinerary(
    uid = "0",
    userUid = "0",
    locations = listOf(),
    title = "Shopping then adventure",
    tags = listOf(ItineraryTags.ADVENTURE, ItineraryTags.LUXURY),
    description = "gucci",
    visible = true,
  )

  val itineraryAdventure = Itinerary(
    uid = "1",
    userUid = "0",
    locations = listOf(),
    title = "Hike",
    tags = listOf(ItineraryTags.ADVENTURE),
    description = null,
    visible = true,
  )

  val itineraries = listOf(itineraryAdventure, itineraryAdventureAndLuxury)

  Scaffold(
      topBar = {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()) {
              SearchBar(onSearchChange = { searchQuery = it })
              CategorySelector(
                  selectedIndex = selectedIndex,
                  categoriesList = categoriesList,
                  onCategorySelected = { selectedIndex = it })
            }
      },
      modifier = Modifier.testTag("Liked screen")) { innerPadding ->
        val filtered = itineraries.filter { itinerary ->
          itinerary.tags.contains(categoriesList[selectedIndex].tag)
        }.filter { itinerary ->
          searchQuery.isBlank() ||
                  itinerary.title.contains(searchQuery, ignoreCase = true) ||
                  itinerary.description?.contains(searchQuery, ignoreCase = true) ?: false
        }
        ItinerariesListScrollable(itineraries = filtered, paddingValues = innerPadding)
      }
}

/**
 * @brief top bar of the liked screen that allows for category selection
 */
@Composable
fun CategorySelector(
    selectedIndex: Int,
    categoriesList: List<SearchCategory>,
    onCategorySelected: (Int) -> Unit
) {
  TabRow(
      selectedTabIndex = selectedIndex,
      backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
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
                modifier = Modifier
                  .size(30.dp)
                  .padding(2.dp))
          })
    }
  }
}

@Composable
fun ItinerariesListScrollable(itineraries: List<Itinerary>, paddingValues: PaddingValues) {
  LazyColumn (
    modifier = Modifier.padding(paddingValues)
  ) {
    this.items(itineraries) { itinerary ->
      Text(text = "${itinerary.title}, tags = ${itinerary.tags}")
    }
  }
}

@Composable
fun NoLikedItinerariesScreen() {
  Text(text = "Nothing to see here! Try liking some Wanders")
}
