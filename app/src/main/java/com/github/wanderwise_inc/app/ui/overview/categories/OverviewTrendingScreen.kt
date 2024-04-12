package com.github.wanderwise_inc.app.ui.overview.categories

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

@Composable
fun OverviewTrendingScreen(
    navController: NavHostController,
    mapViewModel: MapViewModel
) {
    Text(text = "Welcome, Here you will find some Trending Itineraries")
    /*val personalItineraries = mapViewModel.getUserItineraries()
    LazyColumn {
        *//*items(personalItineraries){
            ItineraryBanner(it)
        }*//*
    }*/
}
