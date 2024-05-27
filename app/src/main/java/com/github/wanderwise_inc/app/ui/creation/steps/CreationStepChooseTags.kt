package com.github.wanderwise_inc.app.ui.creation.steps

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.core.text.isDigitsOnly
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.SearchCategory
import com.github.wanderwise_inc.app.model.location.Tag
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import kotlinx.coroutines.launch

const val MAX_TAGS = 3

@Composable
fun CreationStepChooseTagsScreen(
    createItineraryViewModel: CreateItineraryViewModel,
    imageRepository: ImageRepository
) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier =
          Modifier.fillMaxSize()
              .testTag(TestTags.CREATION_SCREEN_TAGS)
              .background(MaterialTheme.colorScheme.background),
      verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ItineraryImageBanner(
            createItineraryViewModel = createItineraryViewModel,
            Modifier.padding(all = 10.dp),
            imageRepository)
        PriceEstimationTextBox(createItineraryViewModel)
        TimeDurationEstimation(createItineraryViewModel)
        RelevantTags(createItineraryViewModel)
        IsPublicSwitchButton(createItineraryViewModel)
      }
}

@Composable
fun ItineraryImageBanner(
    createItineraryViewModel: CreateItineraryViewModel,
    modifier: Modifier = Modifier,
    imageRepository: ImageRepository
) {
  imageRepository.setIsItineraryImage(true)
  val coroutineScope = rememberCoroutineScope()
  var imageUploaded by remember { mutableStateOf<Uri?>(null) }
  imageRepository.setOnImageSelectedListener { uri -> imageUploaded = uri }

  Box(
      modifier =
          Modifier.fillMaxWidth()
              .testTag(TestTags.CREATION_SCREEN_IMAGE_BANNER_BOX)
              .padding(all = 10.dp)
              .height(120.dp)
              .clip(MaterialTheme.shapes.medium)
              .background(MaterialTheme.colorScheme.surface)
              .clickable {
                coroutineScope.launch {
                  Intent(Intent.ACTION_GET_CONTENT).also {
                    it.type = "image/*" // Set type to any image format.
                    imageRepository.launchActivity(it) // Launch activity to select an image.
                  }
                }
              },
      contentAlignment = Alignment.Center) {
        imageUploaded = imageRepository.getCurrentFile()
        if (imageUploaded != null) {
          // display image
          AsyncImage(
              model =
                  ImageRequest.Builder(LocalContext.current)
                      .data(imageUploaded)
                      .crossfade(500)
                      .build(),
              contentDescription = "itinerary_image",
              contentScale = ContentScale.Crop,
              modifier = Modifier.testTag(TestTags.CREATION_SCREEN_IMAGE_BANNER).fillMaxSize())
        } else {
          Text("Itinerary Banner Please Upload Image")
        }
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceEstimationTextBox(createItineraryViewModel: CreateItineraryViewModel) {
  var priceEstimateDisplay by remember {
    mutableStateOf(createItineraryViewModel.getNewItinerary()!!.price?.toString() ?: "")
  }

  val regex = "^[0-9]+(\\.[0-9]{0,2})?$".toRegex()

  TextField(
      label = { Text("Price Estimate") },
      value = priceEstimateDisplay,
      modifier = Modifier.testTag(TestTags.PRICE_SEARCH),
      keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
      onValueChange = { input ->
        // ensures that we can only set digits and points (and don't crash with input.toFloat())
        if (input.isNotBlank() && input.matches(regex)) {
          createItineraryViewModel.getNewItinerary()!!.price(input.toFloat())
        }
        priceEstimateDisplay = input
      })
}

@Composable
fun TimeDurationEstimation(createItineraryViewModel: CreateItineraryViewModel) {
  var timeEstimateDisplay by remember {
    mutableStateOf(createItineraryViewModel.getNewItinerary()?.time?.toString() ?: "")
  }
  TextField(
      label = { Text("Time Estimate (in hours)") },
      value = timeEstimateDisplay,
      modifier = Modifier.testTag(TestTags.TIME_SEARCH),
      keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
      onValueChange = { input ->
        if (input.isNotBlank() && input.isDigitsOnly())
            createItineraryViewModel.getNewItinerary()?.time(input.toInt())
        timeEstimateDisplay = input
      })
}

@Composable
fun RelevantTags(createItineraryViewModel: CreateItineraryViewModel) {
  val allTags = ItineraryTags.toSearchCategoryList()
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

  if (isTagsDDM) {
    Popup(alignment = Alignment.Center, onDismissRequest = { isTagsDDM = false }) {
      OutlinedCard(
          modifier = Modifier.padding(8.dp).wrapContentSize(),
          shape = MaterialTheme.shapes.medium) {
            TagSelector(
                searchCategoryList = allTags,
                selectedTags = dispTags,
                createItineraryViewModel = createItineraryViewModel)
          }
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagSelector(
    searchCategoryList: List<SearchCategory>,
    selectedTags: MutableList<Tag>,
    createItineraryViewModel: CreateItineraryViewModel
) {
  Column {
    Box(Modifier.fillMaxWidth().padding(6.dp)) {
      Text("Please select up to $MAX_TAGS tags", Modifier.align(Alignment.Center))
    }
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)) {
          for (sc in searchCategoryList) {
            TagsButton(
                searchCategory = sc,
                selectedTags = selectedTags,
                createItineraryViewModel = createItineraryViewModel)
          }
        }
  }
}

@Composable
fun TagsButton(
    searchCategory: SearchCategory,
    selectedTags: MutableList<Tag>,
    createItineraryViewModel: CreateItineraryViewModel
) {
  var selected by remember { mutableStateOf(selectedTags.contains(searchCategory.tag)) }
  FilterChip(
      modifier = Modifier.wrapContentSize(),
      selected = selected,
      onClick = {
        if (selected) {
          selectedTags.remove(searchCategory.tag)
          createItineraryViewModel.getNewItinerary()!!.tags.remove(searchCategory.tag)
          selected = false
        } else if (selectedTags.size < MAX_TAGS) {
          selectedTags.add(searchCategory.tag)
          createItineraryViewModel.getNewItinerary()!!.tags.add(searchCategory.tag)
          selected = true
        }
      },
      label = { Text(searchCategory.tag) },
      leadingIcon = { Icon(imageVector = searchCategory.icon, searchCategory.tag) },
      colors =
          FilterChipDefaults.filterChipColors(
              containerColor = MaterialTheme.colorScheme.secondaryContainer,
              labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
              iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
              selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
          ))
}
