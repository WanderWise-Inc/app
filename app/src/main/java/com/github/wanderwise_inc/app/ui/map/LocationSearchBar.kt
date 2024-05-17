package com.github.wanderwise_inc.app.ui.map

import android.util.Log
import android.view.KeyEvent.KEYCODE_ENTER
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel

@Composable
fun LocationSearchBar(
    onSearch: (String) -> Unit,
    focusOnLocation: (Location?) -> Unit,
    itineraryViewModel: ItineraryViewModel
) {
  var query by remember { mutableStateOf("") }

  val searchedLocations by itineraryViewModel.getPlacesLiveData().observeAsState(emptyList())
  var searchOccurred by remember { mutableStateOf(false) }
  var focusedOnLocation by remember { mutableStateOf(false) }

  val onSearchExpanded = { query: String ->
    onSearch(query)
    focusedOnLocation = false
    searchOccurred = true
  }

  Column {
    OutlinedTextField(
        value = query,
        onValueChange = { s: String -> query = s },
        placeholder = {
          Text(text = "Search a location", color = MaterialTheme.colorScheme.onPrimaryContainer)
        },
        leadingIcon = {
          Icon(
              imageVector = Icons.Filled.Search,
              contentDescription = "search icon",
              tint = Color.Black,
              modifier =
              Modifier
                  .clickable { onSearchExpanded(query) }
                  .padding(2.dp)
                  .size(30.dp)
                  .testTag(TestTags.SEARCH_ICON))
        },
        singleLine = true,
        shape = RoundedCornerShape(30.dp),
        modifier =
        Modifier
            .testTag(TestTags.LOCATION_SEARCH_BAR)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxWidth()
            .padding(5.dp)
            .onKeyEvent {
                if (it.nativeKeyEvent.keyCode == KEYCODE_ENTER) { // overwrite enter key
                    onSearchExpanded(query)
                    true
                }
                false
            }
            .testTag(TestTags.SEARCH_BAR),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onSearchExpanded(query) }))

    Log.d("DEBUG_LOCATION_BAR", "searched locations: $searchedLocations")
    if (searchOccurred) {
      if (searchedLocations.isEmpty()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier =
            Modifier
                .testTag(TestTags.LOCATION_SEARCH_NO_RESULTS)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .height(50.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline)
                .padding(8.dp)) {
              Text("No results found")
            }
      } else {
        LazyColumn(
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer).testTag(TestTags.LOCATION_SEARCH_RESULTS)
        ) {
          items(searchedLocations) { loc ->
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start,
                modifier =
                    Modifier
                        .testTag("${TestTags.LOCATION_SEARCH_RESULTS}_${loc.title}")
                        .fillMaxWidth()
                        .height(46.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline)
                        .padding(6.dp)
                        .clickable {
                          focusOnLocation(loc)
                          focusedOnLocation = true
                        }) {
                  Text(
                      text = loc.address ?: "address undefined",
                      fontSize = 12.sp,
                      lineHeight = 18.sp,
                      maxLines = 2)
                }
          }
        }
      }
    }
  }
}
