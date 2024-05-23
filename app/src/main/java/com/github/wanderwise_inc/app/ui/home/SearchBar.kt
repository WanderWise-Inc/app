package com.github.wanderwise_inc.app.ui.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.ui.TestTags
import java.time.format.TextStyle

const val MIN_PRICE = 0f
const val MAX_PRICE = Float.MAX_VALUE

const val MIN_TIME = 0

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
      trailingIcon = {
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
          // PriceFilter(sliderPositionPriceState)
          //            Spacer(Modifier.padding(10.dp))
          //            TimeTab()
          PriceSlider(sliderPositionPriceState = sliderPositionPriceState)
          Spacer(Modifier.padding(10.dp))
          TimeSlider(sliderPositionTimeState = sliderPositionTimeState)
        }
      }
}

@Composable
fun PriceFilter(sliderPositionPriceState: MutableState<ClosedFloatingPointRange<Float>>) {
  Text("How much do I want to spend ?")

  Column() {
    PriceSlider(sliderPositionPriceState)
    PriceTab(sliderPositionPriceState)
  }
}

@Composable
fun PriceTab(sliderPositionPriceState: MutableState<ClosedFloatingPointRange<Float>>) {
  var minPrice by remember { mutableStateOf(MIN_PRICE.toString()) }
  var maxPrice by remember { mutableStateOf("-") }

  Text("How much do I want to spend ?")

  Row(
      modifier = Modifier.fillMaxSize().padding(10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceAround) {
        Spacer(Modifier.padding(20.dp))
        OutlinedTextField(
            modifier = Modifier.size(100.dp, 60.dp),
            value = minPrice,
            onValueChange = { it ->
              if (onPriceChange(it, minPrice, maxPrice, true, sliderPositionPriceState)) {
                minPrice = it
              }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            maxLines = 1,
            textStyle =
                androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                ),
            label = {
              Text(
                  text = "Min price",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier)
            })

        Text(" - ")

        OutlinedTextField(
            modifier = Modifier.size(100.dp, 60.dp),
            value = maxPrice,
            onValueChange = { it ->
              if (onPriceChange(it, minPrice, maxPrice, false, sliderPositionPriceState)) {
                maxPrice = it
              }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            maxLines = 1,
            textStyle =
                androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                ),
            label = {
              Text(
                  text = "Max price",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier)
            })
        Spacer(Modifier.padding(20.dp))
      }
}

fun onPriceChange(
    newPrice: String,
    minPrice: String,
    maxPrice: String,
    isMin: Boolean,
    sliderPositionPriceState: MutableState<ClosedFloatingPointRange<Float>>
): Boolean {
  var newMin = minPrice.toFloatOrNull() ?: 0f
  var newMax =
      if (maxPrice == "-") {
        MAX_PRICE
      } else {
        maxPrice.toFloatOrNull() ?: 0f
      }

  Log.d("test", "you are here : $newMin : $newMax")

  val regex = "^[0-9]+(\\.[0-9]{0,2})?$".toRegex()

  // "-" means maximum value
  if (newPrice == "-" && !isMin) {
    newMax = MAX_PRICE
    // matches legal values
  } else if (newPrice.isBlank() || newPrice.matches(regex)) {
    if (isMin) {
      newMin = newPrice.toFloatOrNull() ?: 0f
    } else {
      newMax = newPrice.toFloatOrNull() ?: 0f
    }
  } else {
    return false
  }

  // min price has to be smaller than the max
  if (newMin > newMax) {
    return false
  }

  sliderPositionPriceState.value = newMin..newMax
  return true
}

@Composable // doesn't work
fun TimeTab() {
  var minTime by remember { mutableStateOf(MIN_TIME.toString()) }
  var maxTime by remember { mutableStateOf("-") }

  Text("How Long do I want to wander ?")

  Row(
      modifier = Modifier.fillMaxSize().padding(10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceAround) {
        Spacer(Modifier.padding(20.dp))
        OutlinedTextField(
            modifier = Modifier.size(100.dp, 60.dp),
            value = minTime,
            onValueChange = { it -> minTime = it },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            maxLines = 1,
            textStyle =
                androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                ),
            label = {
              Text(
                  text = "Min time",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier)
            })

        Text(" - ")

        OutlinedTextField(
            modifier = Modifier.size(100.dp, 60.dp),
            value = maxTime,
            onValueChange = { it -> maxTime = it },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            maxLines = 1,
            textStyle =
                androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                ),
            label = {
              Text(
                  text = "Max time",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier)
            })
        Spacer(Modifier.padding(20.dp))
      }
}

@Composable
fun PriceSlider(
    sliderPositionPriceState: MutableState<ClosedFloatingPointRange<Float>>,
) {
  Text("How much do I want to spend ?")

  Column() {
    RangeSlider(
        value = sliderPositionPriceState.value,
        steps = 50,
        onValueChange = { range -> sliderPositionPriceState.value = range },
        valueRange = 0f..100f, // Adjust this range according to your needs
        onValueChangeFinished = {
          // launch something
        },
        modifier = Modifier.testTag(TestTags.FILTER_PRICE_RANGE))
    Text(
        text =
            String.format(
                "%.2f - %.2f",
                sliderPositionPriceState.value.start,
                sliderPositionPriceState.value.endInclusive))
  }
}

@Composable
fun TimeSlider(sliderPositionTimeState: MutableState<ClosedFloatingPointRange<Float>>) {
  // Column (horizontalAlignment = Alignment.CenterHorizontally) {
  Text("How Long do I want to wander ?")

  Column() {
    RangeSlider(
        value = sliderPositionTimeState.value,
        steps = 25,
        onValueChange = { range -> sliderPositionTimeState.value = range },
        valueRange = 0f..24f, // Adjust this range according to your needs
        onValueChangeFinished = {
          // launch something
        },
        modifier = Modifier.testTag(TestTags.FILTER_TIME_RANGE))
    Text(
        text =
            String.format(
                "%.2f - %.2f",
                sliderPositionTimeState.value.start,
                sliderPositionTimeState.value.endInclusive))
  }
}
