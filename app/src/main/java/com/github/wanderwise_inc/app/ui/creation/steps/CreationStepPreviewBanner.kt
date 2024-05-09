package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.wanderwise_inc.app.ui.TestTags
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.ui.itinerary.ItineraryBanner
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel

@Composable
fun CreationStepPreviewBanner(createItineraryViewModel: CreateItineraryViewModel) {
//itinerary: Itinerary, mapViewModel: MapViewModel
    //val itinerary = Itinerary("", "", emptyList(), "True itinerary", emptyList(), "", true,)
    val dummyItinerary = Itinerary("", "", emptyList(), "Dummy itinerary", emptyList(), "", true,)

    //set the itineary, not permanent (will have to check all this in navigation
    val itinerary =  if (createItineraryViewModel.getNewItinerary() == null)
        dummyItinerary
    else
        createItineraryViewModel.getNewItinerary()!!.build()

    //function that do nothing
    val onBannerClick = {i :Itinerary -> }
    val onLikeButtonClick = {i :Itinerary, b: Boolean-> }

    Scaffold(modifier = Modifier.testTag(TestTags.CREATION_SCREEN_PREVIEW_BANNER))
    { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding( start = 10.dp, end = 10.dp, bottom = innerPadding.calculateBottomPadding(), top = 0.dp),
            contentAlignment = Alignment.TopCenter){
            LazyColumn(
                verticalArrangement = spacedBy(15.dp)) {
                item {
                    ItineraryBanner(itinerary = dummyItinerary, onLikeButtonClick = onLikeButtonClick, onBannerClick = onBannerClick)
                }

                item {
                    ItineraryBanner(itinerary = itinerary, onLikeButtonClick = onLikeButtonClick, onBannerClick = onBannerClick)
                }

                item{
                    ItineraryBanner(itinerary = dummyItinerary, onLikeButtonClick = onLikeButtonClick, onBannerClick = onBannerClick)
                }
            }
        }
    }
}
