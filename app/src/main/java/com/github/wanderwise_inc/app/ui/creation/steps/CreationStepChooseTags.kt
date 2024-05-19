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
import androidx.core.text.isDigitsOnly
import com.github.wanderwise_inc.app.model.location.Tag
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
        ItineraryImageBanner(
            createItineraryViewModel = createItineraryViewModel, Modifier.padding(all = 10.dp))
        PriceEstimationTextBox(createItineraryViewModel)
        TimeDurationEstimation(createItineraryViewModel)
        RelevantTags(createItineraryViewModel)
        IsPublicSwitchButton(createItineraryViewModel)
      }
}

@Composable
fun ItineraryImageBanner(
    createItineraryViewModel: CreateItineraryViewModel,
    modifier: Modifier = Modifier
) {
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
  var priceEstimateDisplay by remember {
    mutableStateOf(createItineraryViewModel.getNewItinerary()!!.price?.toString() ?: "")
  }

  val regex = "^[0-9]*\\.?[0-9]{2}$".toRegex()

  Row(Modifier.fillMaxWidth().padding(all = 10.dp).clip(MaterialTheme.shapes.medium)) {
    TextField(
        label = { Text("Price Estimate") },
        value = priceEstimateDisplay,
        modifier = Modifier.testTag(TestTags.ITINERARY_CREATION_PRICE_ESTIMATION),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        onValueChange = { input ->
          // ensures that we can only set digits and points (and don't crash with input.toFloat())
          if (input.isNotBlank() && input.matches(regex)) {
            createItineraryViewModel.getNewItinerary()!!.price(input.toFloat())
          }
          priceEstimateDisplay = input
        })

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
  var timeEstimateDisplay by remember {
    mutableStateOf(createItineraryViewModel.getNewItinerary()?.time?.toString() ?: "")
  }
  TextField(
      label = { Text("Time Estimate (in hours)") },
      value = timeEstimateDisplay,
      modifier = Modifier.testTag(TestTags.ITINERARY_CREATION_TIME_ESTIMATION),
      keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
      onValueChange = { input ->
        if (input.isNotBlank() && input.isDigitsOnly())
            createItineraryViewModel.getNewItinerary()?.time(input.toInt())
        timeEstimateDisplay = input
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

  var selectedTags = mutableListOf<Tag>()
  for (tag in createItineraryViewModel.getNewItinerary()?.tags!!) {
    selectedTags.add(tag)
  }
  val dispTags by remember(selectedTags) { mutableStateOf(selectedTags) }
  var triedToAddMoreThan3Tags by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
    Text("Selected Tags: ${dispTags.joinToString(", ")}")
    if (triedToAddMoreThan3Tags) {
      Text(
          "You can only select up to 3 tags",
          color = MaterialTheme.colorScheme.error,
          modifier = Modifier.padding(top = 50.dp))
    }
  }

  Button(
      modifier = Modifier.testTag(TestTags.ITINERARY_CREATION_TAGS),
      onClick = { isTagsDDM = true }) {
        Text("Add Tags")
      }

  DropdownMenu(expanded = isTagsDDM, onDismissRequest = { isTagsDDM = false }) {
    allTags.forEach { tag ->
      DropdownMenuItem(
          text = { Text(tag) },
          modifier = Modifier.testTag("${TestTags.ITINERARY_CREATION_TAGS}_$tag"),
          onClick = {
            isTagsDDM = false
            if (!selectedTags.contains(tag) && selectedTags.size < 3) {
              selectedTags.add(tag)
              createItineraryViewModel.getNewItinerary()!!.tags.clear()
              createItineraryViewModel.getNewItinerary()!!.tags.addAll(selectedTags)
            } else if (selectedTags.size >= 3) {
              triedToAddMoreThan3Tags = true
            }
          })
    }
  }
}

@Composable
fun IsPublicSwitchButton(createItineraryViewModel: CreateItineraryViewModel) {
  var isVisible by remember {
    mutableStateOf(createItineraryViewModel.getNewItinerary()?.visible ?: false)
  }

  Switch(
      checked = isVisible,
      onCheckedChange = {
        isVisible = it
        createItineraryViewModel.getNewItinerary()!!.visible(isVisible)
      },
      modifier = Modifier.padding(top = 20.dp))
  if (isVisible) {
    Text("This itinerary is public", fontWeight = FontWeight.Bold)
  } else {
    Text("This itinerary is private", fontWeight = FontWeight.Bold)
  }
}
