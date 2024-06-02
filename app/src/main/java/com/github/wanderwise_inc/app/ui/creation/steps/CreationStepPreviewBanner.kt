package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.itinerary.DummyBanner
import com.github.wanderwise_inc.app.ui.itinerary.ItineraryBanner
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel

/** @brief screen for previewing the created itinerary inside the feed */
@Composable
fun CreationStepPreviewBanner(
    createItineraryViewModel: CreateItineraryViewModel,
    imageRepository: ImageRepository
) {
  // function that do nothing
  val onBannerClick = { _: Itinerary -> }
  val onLikeButtonClick = { _: Itinerary, _: Boolean -> }

  Box(
      modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
      contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            verticalArrangement = spacedBy(8.dp),
            modifier = Modifier.testTag(TestTags.CREATION_SCREEN_PREVIEW_BANNER)) {
              item { DummyBanner("0") }
              item {
                ItineraryBanner(
                    itinerary =
                        createItineraryViewModel.getFocusedItinerary()
                            ?: Itinerary.Builder().build(),
                    onLikeButtonClick = onLikeButtonClick,
                    onBannerClick = onBannerClick,
                    imageRepository = imageRepository,
                    inCreation = true)
              }
              item { DummyBanner("1") }
              item { DummyBanner("2") }
              item { Spacer(Modifier.padding(5.dp)) }
            }
      }
}
