package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.wanderwise_inc.app.ui.TestTags

@Composable
fun CreationStepChooseTagsScreen() {
    Text(
        text = "Welcome to the third step of creating your itinerary, here you will be able to " +
                "choose the tags of your itinerary as well as some additional info",
        modifier = Modifier.testTag(TestTags.CREATION_SCREEN_TAGS)
    )
}