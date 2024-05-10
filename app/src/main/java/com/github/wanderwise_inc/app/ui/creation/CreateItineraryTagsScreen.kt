package com.github.wanderwise_inc.app.ui.creation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun NewItineraryPropertiesScreen() {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.secondaryContainer),
      verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ItineraryImageBanner(modifier = Modifier.padding(all = 10.dp))
        PriceEstimationTextBox()
        TimeDurationEstimation()
        ReleventTags()
        IsPublicSwitchButton()
      }
}

@Composable
fun ItineraryImageBanner(modifier: Modifier = Modifier) {
  // on click upload image
  // default image = Please Upload Image
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .padding(all = 10.dp)
              .height(120.dp)
              .clip(RoundedCornerShape(10))
              .background(MaterialTheme.colorScheme.surface),
      contentAlignment = Alignment.Center) {
        Text("Itinerary Banner Please Upload Image")
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceEstimationTextBox() {
  // simple text box to estimate price, give the option to choose currency
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .padding(all = 10.dp)
              .height(100.dp)
              .background(MaterialTheme.colorScheme.surface)
              .clip(RoundedCornerShape(10))) {
        val currencies =
            listOf("USD", "EUR", "JPY", "GBP", "AUD", "CAD", "CHF", "CNY", "SEK", "NZD")
        var currenciesMenuExpanded by remember { mutableStateOf(false) }
        var selectedCurrency by remember { mutableStateOf(currencies[0]) }

        ExposedDropdownMenuBox(
            expanded = currenciesMenuExpanded,
            onExpandedChange = { currenciesMenuExpanded = !currenciesMenuExpanded }) {
              TextField(
                  modifier = Modifier.menuAnchor(),
                  value = selectedCurrency,
                  onValueChange = {},
                  readOnly = true,
                  trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = currenciesMenuExpanded)
                  })

              ExposedDropdownMenu(
                  expanded = currenciesMenuExpanded,
                  onDismissRequest = { currenciesMenuExpanded = false },
                  modifier = Modifier.exposedDropdownSize()) {
                    currencies.forEach { currency ->
                      DropdownMenuItem(
                          text = { currency },
                          onClick = {
                            selectedCurrency = currency
                            currenciesMenuExpanded = false
                          },
                          contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                    }
                  }
            }
      }
}

@Composable
fun TimeDurationEstimation() {
  // simple time input box to give and estimate on the time duration to follow the itinerary
}

@Composable
fun ReleventTags() {
  // tags that are relevant to the itinerary
  // drop down list
  // adds at most 3 tags
}

@Composable
fun IsPublicSwitchButton() {
  // switch button to make the itinerary public or private
}
