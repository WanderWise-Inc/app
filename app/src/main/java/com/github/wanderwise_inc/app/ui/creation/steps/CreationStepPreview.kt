package com.github.wanderwise_inc.app.ui.creation.steps

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.wanderwise_inc.app.ui.navigation.Destination.CreationPreviewOptionsDestinations
import com.github.wanderwise_inc.app.ui.navigation.graph.Graph
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions

@Composable
fun CreationStepPreview(
    navController: NavHostController = rememberNavController()
) {
    var selected by remember { mutableStateOf(PreviewOption.Banner) }
    val navigator = NavigationActions(navController)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selected == PreviewOption.Banner) {
                        selected = PreviewOption.Itinerary
                        navigator.navigateTo(CreationPreviewOptionsDestinations.PreviewItinerary)
                    } else {
                        selected = PreviewOption.Banner
                        navigator.navigateTo(CreationPreviewOptionsDestinations.PreviewBanner)
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = 
                        if (selected == PreviewOption.Banner){
                            CreationPreviewOptionsDestinations.PreviewBanner.icon
                        } else {
                            CreationPreviewOptionsDestinations.PreviewItinerary.icon
                        }),
                    contentDescription = null,
                    tint = Color(0xFF191C1E),
                    modifier = Modifier
                        .size(30.dp)
                        .padding(2.dp)
                )
            }
        }
    ) { padding ->
        CreationStepPreviewNav(navController, padding)
    }
}

@Composable
fun CreationStepPreviewNav(
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        route = Graph.CREATION_PREVIEW,
        startDestination = CreationPreviewOptionsDestinations.PreviewBanner.route,
        modifier = Modifier.padding(padding)
    ) {
        composable(route = CreationPreviewOptionsDestinations.PreviewBanner.route) {
            CreationStepPreviewBanner()
        }
        composable(route = CreationPreviewOptionsDestinations.PreviewItinerary.route) {
            CreationStepPreviewItinerary()
        }
    }
}

enum class PreviewOption{
    Banner, Itinerary
}