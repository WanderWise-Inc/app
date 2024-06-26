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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
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
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.location.SearchCategory
import com.github.wanderwise_inc.app.model.location.Tag
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import kotlinx.coroutines.launch

const val MAX_TAGS = 3
/**
 * Displays the screen for choosing tags during the creation of an itinerary.
 *
 * This composable function sets up a user interface that allows users to choose tags for their
 * itinerary. The screen includes components for displaying an itinerary image banner, price
 * estimation, time duration estimation, relevant tags, and a switch to mark the itinerary as public
 * or private.
 *
 * @param createItineraryViewModel The ViewModel that manages the state and logic for creating an
 *   itinerary.
 * @param imageRepository The repository responsible for handling image operations.
 */
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

/**
 * Displays a banner for the itinerary image with functionality to upload an image.
 *
 * This composable function creates a banner that allows users to upload an image for their
 * itinerary. It sets up the UI elements and handles the logic for selecting and displaying an
 * image.
 *
 * @param createItineraryViewModel The ViewModel that manages the state and logic for creating an
 *   itinerary.
 * @param modifier The modifier to be applied to the layout. Defaults to an empty modifier.
 * @param imageRepository The repository responsible for handling image operations, including
 *   setting the image and launching the image selection activity.
 */
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

/**
 * Displays a text box for inputting and displaying the price estimate of the itinerary.
 *
 * This composable function creates a text field where users can input a price estimate for their
 * itinerary. It ensures that the input is a valid number, updates the ViewModel with the new price,
 * and displays the current price estimate.
 *
 * @param createItineraryViewModel The ViewModel that manages the state and logic for creating an
 *   itinerary, including the price estimation.
 */
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

/**
 * Displays a text box for inputting and displaying the time duration estimate of the itinerary.
 *
 * This function creates a text field where users can input a time estimate for their itinerary in
 * hours. It ensures that the input is a valid number, updates the ViewModel with the new time
 * estimate, and displays the current time estimate.
 *
 * @param createItineraryViewModel The ViewModel that manages the state and logic for creating an
 *   itinerary, including the time duration estimation.
 */
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
/**
 * Displays a UI for selecting relevant tags for the itinerary.
 *
 * This composable function creates a user interface that allows users to select tags relevant to
 * their itinerary. It includes a button to open a dropdown menu for tag selection and displays the
 * currently selected tags.
 *
 * @param createItineraryViewModel The ViewModel that manages the state and logic for creating an
 *   itinerary, including tag selection.
 */
@Composable
fun RelevantTags(createItineraryViewModel: CreateItineraryViewModel) {
  // first tag is All, we don't want it to be selectable
  val allTags = ItineraryTags.toSearchCategoryList().drop(1)
  var isTagsDDM by remember { mutableStateOf(false) }

  var selectedTags = mutableListOf<Tag>()
  for (tag in createItineraryViewModel.getNewItinerary()?.tags!!) {
    selectedTags.add(tag)
  }
  val dispTags by remember(selectedTags) { mutableStateOf(selectedTags) }

  Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
    Text("Selected Tags: ${dispTags.joinToString(", ")}")
  }

  // clickable button to open dropdown menu
  Button(
      modifier = Modifier.testTag(TestTags.ITINERARY_CREATION_TAGS),
      onClick = { isTagsDDM = true }) {
        Text("Add Tags")
      }

  if (isTagsDDM) {
    // if drop down menu is set to "true" opens popup where we can select tags
    Popup(alignment = Alignment.Center, onDismissRequest = { isTagsDDM = false }) {
      OutlinedCard(
          modifier = Modifier.padding(8.dp).wrapContentSize(),
          shape = MaterialTheme.shapes.medium) {
            TagSelector(
                searchCategoryList = allTags,
                selectedTags = dispTags,
                newItinerary = createItineraryViewModel.getNewItinerary()!!)
          }
    }
  }
}

/**
 * Displays a switch button to toggle the visibility of the itinerary between public and private.
 *
 * This composable function creates a switch button that allows users to set the visibility of their
 * itinerary. It updates the ViewModel with the current visibility state and displays a text
 * indicating whether the itinerary is public or private.
 *
 * @param createItineraryViewModel The ViewModel that manages the state and logic for creating an
 *   itinerary, including visibility settings.
 */
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

/** @brief arranges the tags is a grid like manner */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagSelector(
    searchCategoryList: List<SearchCategory>,
    selectedTags: MutableList<Tag>,
    newItinerary: Itinerary.Builder
) {
  var moreThanAllowedTags by remember { mutableStateOf(false) }
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
        text = "Please select up to $MAX_TAGS tags",
        modifier = Modifier.padding(top = 12.dp),
        fontWeight = FontWeight.Bold)

    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(12.dp).testTag(TestTags.POPUP_TAG_SELECTION),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      for (sc in searchCategoryList) {
        TagsButton(searchCategory = sc, selectedTags = selectedTags, newItinerary = newItinerary) {
            b: Boolean ->
          moreThanAllowedTags = b
        }
      }
    }
    if (moreThanAllowedTags) {
      Text(
          text = "Cannot add more than $MAX_TAGS",
          color = MaterialTheme.colorScheme.error,
          modifier =
              Modifier.padding(bottom = 12.dp).testTag(TestTags.ERROR_MORE_THAN_ALLOWED_TAGS))
    }
  }
}

/** @brief takes care of composing the clickable tags */
@Composable
fun TagsButton(
    searchCategory: SearchCategory,
    selectedTags: MutableList<Tag>,
    newItinerary: Itinerary.Builder,
    moreThanAllowedTags: (Boolean) -> Unit
) {
  var selected by remember { mutableStateOf(selectedTags.contains(searchCategory.tag)) }

  FilterChip(
      modifier = Modifier.wrapContentSize().testTag("${TestTags.TAG_CHIP}_${searchCategory.tag}"),
      selected = selected,
      onClick = {
        if (selected) {
          selectedTags.remove(searchCategory.tag)
          newItinerary.tags.remove(searchCategory.tag)
          selected = false
          moreThanAllowedTags(false)
        } else if (selectedTags.size < MAX_TAGS) {
          selectedTags.add(searchCategory.tag)
          newItinerary.tags.add(searchCategory.tag)
          selected = true
          moreThanAllowedTags(false)
        } else {
          moreThanAllowedTags(true)
        }
      },
      label = { Text(searchCategory.tag) },
      leadingIcon = { Icon(imageVector = searchCategory.icon, searchCategory.tag) },
      trailingIcon = {
        if (selected) {
          Icon(imageVector = Icons.Outlined.Check, "added")
        } else {
          Icon(imageVector = Icons.Outlined.Add, "add")
        }
      },
      colors =
          FilterChipDefaults.filterChipColors(
              containerColor = MaterialTheme.colorScheme.secondaryContainer,
              labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
              iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
              selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
              selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimary,
          ))
}
