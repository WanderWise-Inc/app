package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.wanderwise_inc.app.ui.TestTags

@Composable
fun CreationStepPreviewBanner() {
    Text(
        text = "Welcome to the final step of creating your itinerary, here you will be able to " +
                "preview the banner corresponding to your itinerary",
        modifier = Modifier.testTag(TestTags.CREATION_SCREEN_PREVIEW_BANNER)
    )
}