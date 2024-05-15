package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.itinerary.ItineraryBanner
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

@Composable
fun CreationStepPreviewBanner(createItineraryViewModel: CreateItineraryViewModel, profileViewModel: ProfileViewModel, imageRepository: ImageRepository) {
  val dummyItinerary =
      Itinerary(
          "1",
          "",
          emptyList(),
          "Dummy itinerary",
          emptyList(),
          "",
          true,
      )

  // set the itineary, not permanent, will do later when logic is applied
  val itinerary = FakeItinerary.SWITZERLAND

  // function that do nothing
  val onBannerClick = { i: Itinerary -> }
  val onLikeButtonClick = { i: Itinerary, b: Boolean -> }

  Box(
      modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 0.dp),
      contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            verticalArrangement = spacedBy(15.dp),
            modifier = Modifier.testTag(TestTags.CREATION_SCREEN_PREVIEW_BANNER)) {
              item {
                ItineraryBanner(
                    itinerary = dummyItinerary,
                    onLikeButtonClick = onLikeButtonClick,
                    onBannerClick = onBannerClick,
                    profileViewModel = profileViewModel,
                    imageRepository = imageRepository)
              }

              item {
                ItineraryBanner(
                    itinerary = itinerary,
                    onLikeButtonClick = onLikeButtonClick,
                    onBannerClick = onBannerClick,
                    profileViewModel = profileViewModel,
                    imageRepository = imageRepository)
              }

              item {
                ItineraryBanner(
                    itinerary = dummyItinerary,
                    onLikeButtonClick = onLikeButtonClick,
                    onBannerClick = onBannerClick,
                    profileViewModel = profileViewModel,
                    imageRepository = imageRepository)
              }
            }
      }
}
