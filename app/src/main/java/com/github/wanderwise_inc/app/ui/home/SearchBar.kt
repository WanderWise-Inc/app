package com.github.wanderwise_inc.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
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
import androidx.compose.ui.unit.dp
import com.github.wanderwise_inc.app.ui.TestTags
import kotlin.math.round

const val MIN_PRICE = 0f
const val MAX_SLIDER_PRICE = 100f
const val MAX_PRICE = Float.MAX_VALUE - 1

const val MIN_TIME = 0f
const val MAX_SLIDER_TIME = 24f
const val MAX_TIME = Float.MAX_VALUE - 1

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
      leadingIcon = {
        Icon(
            imageVector = Icons.Outlined.Tune,
            contentDescription = "search icon",
            tint = Color.Black,
            modifier =
                Modifier.clickable { isDropdownOpen = true }
                    .padding(horizontal = 10.dp, vertical = 2.dp)
                    .size(25.dp)
                    .testTag(TestTags.SEARCH_ICON))
      },
      placeholder = {
        Text(
            text = "Wander where?",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
      },
      singleLine = true,
      shape = RoundedCornerShape(16.dp),
      modifier =
          Modifier.background(MaterialTheme.colorScheme.primaryContainer)
              .fillMaxWidth()
              .padding(5.dp)
              .testTag(TestTags.SEARCH_BAR))

  DropdownMenu(
      expanded = isDropdownOpen,
      onDismissRequest = { isDropdownOpen = false },
      modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 5.dp)) {
        Column() {
          PriceSlider(sliderPositionPriceState = sliderPositionPriceState)
          Spacer(Modifier.padding(10.dp))
          TimeSlider(sliderPositionTimeState = sliderPositionTimeState)
        }
      }
}

@Composable
fun PriceSlider(
    sliderPositionPriceState: MutableState<ClosedFloatingPointRange<Float>>,
) {
  val minSliderPrice = sliderPositionPriceState.value.start
  val maxSliderPrice =
      if (sliderPositionPriceState.value.endInclusive > MAX_SLIDER_PRICE) MAX_SLIDER_PRICE
      else sliderPositionPriceState.value.endInclusive

  val sliderPriceLocal = minSliderPrice..maxSliderPrice

  Text("How much do I want to spend ?")

  Column() {
    RangeSlider(
        value = sliderPriceLocal,
        onValueChange = { range ->
          if (range.endInclusive >= MAX_SLIDER_PRICE - 1E-2) {
            sliderPositionPriceState.value = range.start..MAX_PRICE
          } else {
            sliderPositionPriceState.value = range
          }
        },
        valueRange = MIN_PRICE..MAX_SLIDER_PRICE, // Adjust this range according to your needs
        modifier = Modifier.testTag(TestTags.FILTER_PRICE_RANGE))
    Text(
        text =
            if (sliderPriceLocal.endInclusive == MAX_SLIDER_PRICE) {
              String.format("%.2f - %.2f +", sliderPositionPriceState.value.start, MAX_SLIDER_PRICE)
            } else {
              String.format(
                  "%.2f - %.2f",
                  sliderPositionPriceState.value.start,
                  sliderPositionPriceState.value.endInclusive)
            })
  }
}

@Composable
fun TimeSlider(sliderPositionTimeState: MutableState<ClosedFloatingPointRange<Float>>) {
  val minSliderTime = sliderPositionTimeState.value.start
  val maxSliderTime =
      if (sliderPositionTimeState.value.endInclusive > MAX_SLIDER_TIME) MAX_SLIDER_TIME
      else sliderPositionTimeState.value.endInclusive

  val sliderPriceLocal = minSliderTime..maxSliderTime

  Text("How long do I want to wander ?")

  Column() {
    RangeSlider(
        value = sliderPositionTimeState.value,
        onValueChange = { range ->
          if (range.endInclusive >= MAX_SLIDER_TIME - 1E-2) {
            sliderPositionTimeState.value = round(range.start)..MAX_TIME
          } else {
            sliderPositionTimeState.value = round(range.start)..round(range.endInclusive)
          }
        },
        valueRange = MIN_TIME..MAX_SLIDER_TIME, // Adjust this range according to your needs
        modifier = Modifier.testTag(TestTags.FILTER_TIME_RANGE))
    Text(
        text =
            if (sliderPriceLocal.endInclusive == MAX_SLIDER_TIME) {
              String.format(
                  "%dh - %dh +",
                  sliderPositionTimeState.value.start.toInt(),
                  MAX_SLIDER_TIME.toInt())
            } else {
              String.format(
                  "%dh - %dh",
                  sliderPositionTimeState.value.start.toInt(),
                  sliderPositionTimeState.value.endInclusive.toInt())
            })
  }
}
