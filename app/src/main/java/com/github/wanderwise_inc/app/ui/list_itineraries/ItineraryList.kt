package com.github.wanderwise_inc.app.ui.list_itineraries

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.model.location.Itinerary

/** @brief reusable UI elements for displaying a list of itineraries */

/** @brief a scrollable list of itineraries */
@Composable
fun ItinerariesListScrollable(itineraries: List<Itinerary>, paddingValues: PaddingValues) {
  LazyColumn(modifier = Modifier.padding(paddingValues)) {
    this.items(itineraries) { itinerary ->
      Text(text = "${itinerary.title}, tags = ${itinerary.tags}")
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
                modifier = Modifier.size(30.dp).padding(2.dp))
          })
    }
  }
}
