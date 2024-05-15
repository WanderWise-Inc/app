package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel

@Composable
fun CreationStepChooseTagsScreen(createItineraryViewModel: CreateItineraryViewModel) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier =
          Modifier.fillMaxSize()
              .testTag(TestTags.CREATION_SCREEN_TAGS)
              .background(MaterialTheme.colorScheme.primaryContainer),
      verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ItineraryImageBanner(modifier = Modifier.padding(all = 10.dp))
        PriceEstimationTextBox(createItineraryViewModel)
        TimeDurationEstimation(createItineraryViewModel)
        RelevantTags(createItineraryViewModel)
        IsPublicSwitchButton()
      }
}

@Composable
fun ItineraryImageBanner(modifier: Modifier = Modifier) {
  // TODO: on click upload image using Context Drop Down Menu
  // default image = Please Upload Image

  Box(
      modifier =
          Modifier.fillMaxWidth()
              .padding(all = 10.dp)
              .height(120.dp)
              .clip(MaterialTheme.shapes.medium)
              .background(MaterialTheme.colorScheme.surface),
      contentAlignment = Alignment.Center) {
        Text("Itinerary Banner Please Upload Image")
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceEstimationTextBox(createItineraryViewModel: CreateItineraryViewModel) {
  // number text field to estimate price, give the option to choose currency
  val currencies = listOf("USD", "EUR", "JPY", "GBP", "AUD", "CAD", "CHF", "CNY", "SEK", "NZD")
  var isCurrenciesMenuExpanded by remember { mutableStateOf(false) }
  var selectedCurrency by remember { mutableStateOf(currencies[0]) }

  Row(Modifier.fillMaxWidth().padding(all = 10.dp).clip(MaterialTheme.shapes.medium)) {
    var priceEstimate by remember { mutableStateOf("") }

    TextField(
        label = { Text("Price Estimate") },
        value = priceEstimate,
        modifier = Modifier,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        onValueChange = {
          priceEstimate = it
          createItineraryViewModel.setNewItineraryPrice(it.toFloat())
        })

    /*Dropdown menu for currency selection, the logic for currency conversion isn't implemented yet as it will not contribute to our core features
    as of now, but it can be implemented in the future if needed. The selected currency will be stored in the `selectedCurrency` variable
    * */
    ExposedDropdownMenuBox(
        expanded = isCurrenciesMenuExpanded,
        onExpandedChange = { isCurrenciesMenuExpanded = it },
        modifier = Modifier.clip(MaterialTheme.shapes.medium).shadow(5.dp)) {
          TextField(
              modifier = Modifier.menuAnchor(),
              value = selectedCurrency,
              onValueChange = {},
              readOnly = true,
              trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCurrenciesMenuExpanded)
              })

          ExposedDropdownMenu(
              expanded = isCurrenciesMenuExpanded,
              onDismissRequest = { isCurrenciesMenuExpanded = false }) {
                currencies.forEachIndexed { index, text ->
                  DropdownMenuItem(
                      text = { Text(text) },
                      onClick = {
                        selectedCurrency = currencies[index]
                        isCurrenciesMenuExpanded = false
                      })
                }
              }
        }
  }
}

@Composable
fun TimeDurationEstimation(createItineraryViewModel: CreateItineraryViewModel) {
  var timeEstimate by remember { mutableStateOf("") }
  TextField(
      label = { Text("Time Estimate (in hours)") },
      value = timeEstimate,
      modifier = Modifier,
      keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
      onValueChange = {
        timeEstimate = it
        createItineraryViewModel.setNewItineraryTime(it.toFloat())
      })
}

@Composable
fun RelevantTags(createItineraryViewModel: CreateItineraryViewModel) {
  val allTags =
      listOf(
          "Adventure",
          "Food",
          "Culture",
          "Nature",
          "History",
          "Relaxation",
          "Shopping",
          "Nightlife")
  var isTagsDDM by remember { mutableStateOf(false) }
  val selectedTags = remember { mutableStateListOf<String>() }
  var triedToAddMoreThan3Tags by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
    Text("Selected Tags: ${selectedTags.joinToString(", ")}")
    if (triedToAddMoreThan3Tags) {
      Text(
          "You can only select up to 3 tags",
          color = MaterialTheme.colorScheme.error,
          modifier = Modifier.padding(top = 50.dp))
    }
  }

  Button(onClick = { isTagsDDM = true }) { Text("Add Tags") }

  DropdownMenu(expanded = isTagsDDM, onDismissRequest = { isTagsDDM = false }) {
    allTags.forEach { tag ->
      DropdownMenuItem(
          text = { Text(tag) },
          onClick = {
            isTagsDDM = false
            if (!selectedTags.contains(tag) && selectedTags.size < 3) {
              selectedTags.add(tag)
              createItineraryViewModel.setNewItineraryTags(selectedTags)
            } else if (selectedTags.size >= 3) {
              triedToAddMoreThan3Tags = true
            }
          })
    }
  }
}

@Composable
fun IsPublicSwitchButton() {
  var isPrivate by remember { mutableStateOf(false) }
  Switch(
      checked = isPrivate,
      onCheckedChange = { isPrivate = it },
      modifier = Modifier.padding(top = 20.dp))
  if (isPrivate) {
    Text("This itinerary is private", fontWeight = FontWeight.Bold)
  } else {
    Text("This itinerary is public", fontWeight = FontWeight.Bold)
  }
}
