package com.github.wanderwise_inc.app.ui.overview

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun OverviewScreen() {
    Text(text = "Welcome to the overview screen", modifier = Modifier.testTag("Overview screen"))
}