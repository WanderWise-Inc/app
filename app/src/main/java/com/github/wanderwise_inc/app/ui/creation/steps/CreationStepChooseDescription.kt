package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel

// @Preview(showBackground = true)
@Composable
fun CreationStepChooseDescriptionScreen(createItineraryViewModel: CreateItineraryViewModel) {
  var title by remember { mutableStateOf(createItineraryViewModel.getNewItinerary()?.title ?: "") }
  var description by remember {
    mutableStateOf(createItineraryViewModel.getNewItinerary()?.description ?: "")
  }

  LazyColumn(
      modifier =
          Modifier.padding(top = 10.dp)
              .background(color = MaterialTheme.colorScheme.background)
              .testTag(TestTags.CREATION_SCREEN_DESCRIPTION_TITLE),
      contentPadding = PaddingValues(24.dp),
      verticalArrangement = Arrangement.Absolute.spacedBy(52.dp)) {
        item {
          OutlinedTextField(
              modifier = Modifier.fillMaxSize().testTag(TestTags.CREATION_SCREEN_TITLE),
              value = title,
              onValueChange = {
                title = it
                createItineraryViewModel.getNewItinerary()?.title(it)
              },
              maxLines = 2,
              textStyle =
                  TextStyle(
                      fontSize = 22.sp,
                  ),
              label = {
                Text(
                    text = "Title",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp))
              })
        }

        item {
          OutlinedTextField(
              modifier = Modifier.fillMaxSize().testTag(TestTags.CREATION_SCREEN_DESCRIPTION),
              value = description,
              onValueChange = {
                description = it
                createItineraryViewModel.getNewItinerary()?.description(it)
              },
              textStyle =
                  TextStyle(
                      fontSize = 22.sp,
                  ),
              label = {
                Text(
                    "Description",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp))
              })
        }
      }
}
