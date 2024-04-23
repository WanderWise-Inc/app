import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.ui.home.SearchBar
import com.github.wanderwise_inc.app.ui.list_itineraries.CategorySelector
import com.github.wanderwise_inc.app.ui.list_itineraries.ItinerariesListScrollable
import com.github.wanderwise_inc.app.ui.list_itineraries.SearchCategory
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

@Composable
fun OverviewScreen(
    mapViewModel: MapViewModel,
) {
  DisplayOverviewItineraries(mapViewModel = mapViewModel)
}

/** Displays global itineraries filtered on some predicates */
@Composable
fun DisplayOverviewItineraries(mapViewModel: MapViewModel) {

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
  val itineraryAdventureAndLuxury =
      Itinerary(
          uid = "0",
          userUid = "0",
          locations = listOf(),
          title = "Shopping then adventure",
          tags = listOf(ItineraryTags.ADVENTURE, ItineraryTags.LUXURY),
          description = "gucci",
          visible = true,
      )

  val itineraryAdventure =
      Itinerary(
          uid = "1",
          userUid = "0",
          locations = listOf(),
          title = "Hike",
          tags = listOf(ItineraryTags.ADVENTURE),
          description = null,
          visible = true,
      )

  val itineraries = listOf(itineraryAdventure, itineraryAdventureAndLuxury)

  androidx.compose.material.Scaffold(
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
        val filtered =
            itineraries
                .filter { itinerary -> itinerary.tags.contains(categoriesList[selectedIndex].tag) }
                .filter { itinerary ->
                  searchQuery.isBlank() ||
                      itinerary.title.contains(searchQuery, ignoreCase = true) ||
                      itinerary.description?.contains(searchQuery, ignoreCase = true) ?: false
                }
        ItinerariesListScrollable(itineraries = filtered, paddingValues = innerPadding)
      }
}
