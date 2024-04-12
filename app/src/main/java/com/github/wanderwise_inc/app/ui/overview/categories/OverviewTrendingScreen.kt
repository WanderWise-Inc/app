package com.github.wanderwise_inc.app.ui.overview.categories

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

@Composable
fun OverviewTrendingScreen(
    navController: NavHostController,
    mapViewModel: MapViewModel
) {
    Text(text = "Welcome, Here you will find some Trending Itineraries", modifier = Modifier.testTag("Overview Trending screen"))
    /*val personalItineraries = mapViewModel.getUserItineraries()
    LazyColumn {
        *//*items(personalItineraries){
            ItineraryBanner(it)
        }*//*
    }*/
}
