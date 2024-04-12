package com.github.wanderwise_inc.app.ui.overview.categories

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

@Composable
fun OverviewSightSeeingScreen(
    navController: NavHostController,
    mapViewModel: MapViewModel
) {
    Text(text = "Welcome, here you will find sight-seeing-themed itineraries", modifier = Modifier.testTag("Overview Sight-seeing screen"))
}