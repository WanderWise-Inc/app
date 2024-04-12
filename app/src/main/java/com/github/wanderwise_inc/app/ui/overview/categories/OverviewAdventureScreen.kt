package com.github.wanderwise_inc.app.ui.overview.categories

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

@Composable
fun OverviewAdventureScreen(navController: NavHostController, mapViewModel: MapViewModel) {
  Text(text = "Welcome, here you will find adventure-themed itineraries")
}
