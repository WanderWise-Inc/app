package com.github.wanderwise_inc.app.ui.liked.categories

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

@Composable
fun LikedSightSeeingScreen(
    navController: NavHostController,
    mapViewModel: MapViewModel
) {
    Text(text = "Welcome, here you will find sight-seeing-themed itineraries", modifier = Modifier.testTag("Liked Sight-seeing screen"))
}