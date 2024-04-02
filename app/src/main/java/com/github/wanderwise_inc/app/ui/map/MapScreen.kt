package com.github.wanderwise_inc.app.ui.map

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun MapScreen() {
    Text(text = "Welcome to the map screen", Modifier.testTag("Map screen"))
}