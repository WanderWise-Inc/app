package com.github.wanderwise_inc.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.ui.TestTags

@Composable
fun SearchBar(
    onSearchChange: (String) -> Unit,
    onPriceChange: (Float) -> Unit,
    sliderPositionPriceState: MutableState<ClosedFloatingPointRange<Float>>,
    sliderPositionTimeState: MutableState<ClosedFloatingPointRange<Float>>
) {
  var query by remember { mutableStateOf("") }
  var isDropdownOpen by remember { mutableStateOf(false) }

  OutlinedTextField(
      value = query,
      onValueChange = { s: String ->
        query = s
        onSearchChange(s)
      },
      placeholder = {
        Text(text = "Wander where?", color = MaterialTheme.colorScheme.onPrimaryContainer)
      },
      leadingIcon = {
        Icon(
            painter = painterResource(id = R.drawable.les_controles),
            contentDescription = "search icon",
            tint = Color.Black,
            modifier =
                Modifier.clickable { isDropdownOpen = true }
                    .padding(2.dp)
                    .size(30.dp)
                    .testTag(TestTags.SEARCH_ICON))
      },
      singleLine = true,
      shape = RoundedCornerShape(30.dp),
      modifier =
          Modifier.background(MaterialTheme.colorScheme.primaryContainer)
              .fillMaxWidth()
              .padding(5.dp))
  //DropdownSearch(isDropdownOpen, sliderPositionPriceState, sliderPositionTimeState)


  DropdownMenu(
      expanded = isDropdownOpen,
      onDismissRequest = { isDropdownOpen = false },
      modifier = Modifier.testTag(TestTags.SEARCH_DROPDOWN).fillMaxWidth()) {
        Column {
          Text("How much do I want to spend ?", modifier = Modifier.testTag(TestTags.PRICE_SEARCH))
          RangeSlider(
              value = sliderPositionPriceState.value,
              steps = 50,
              onValueChange = { range -> sliderPositionPriceState.value = range },
              valueRange = 0f..100f, // Adjust this range according to your needs
              onValueChangeFinished = {
                // launch something
              },
              modifier = Modifier.testTag(TestTags.PRICE_SEARCH))
          Text(
              text =
                  String.format(
                      "%.2f - %.2f",
                      sliderPositionPriceState.value.start,
                      sliderPositionPriceState.value.endInclusive))
          // sliderPosition.contains()
        }

        Column {
          Text("How Long do I want to wander ?", modifier = Modifier.testTag(TestTags.TIME_SEARCH))
          RangeSlider(
              value = sliderPositionTimeState.value,
              steps = 24,
              onValueChange = { range -> sliderPositionTimeState.value = range },
              valueRange = 0f..24f, // Adjust this range according to your needs
              onValueChangeFinished = {
                // launch something
              },
              modifier = Modifier.testTag(TestTags.TIME_SEARCH))
          Text(
              text =
                  String.format(
                      "%.2f - %.2f",
                      sliderPositionTimeState.value.start,
                      sliderPositionTimeState.value.endInclusive))
        }
      }
}
