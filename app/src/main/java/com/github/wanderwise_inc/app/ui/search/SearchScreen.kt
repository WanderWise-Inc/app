package com.github.wanderwise_inc.app.ui.search

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

@Composable
fun SearchScreen(
    // navController: NavHostController,
    mapViewModel: MapViewModel
) {
    Text(
        text = "Welcome, here you will find all the results to your search",
        modifier = Modifier.testTag("Search screen")
    )
}