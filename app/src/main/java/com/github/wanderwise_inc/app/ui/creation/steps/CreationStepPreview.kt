package com.github.wanderwise_inc.app.ui.creation.steps

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
import com.github.wanderwise_inc.app.ui.navigation.Destination.CreationStepsDestinations
import com.github.wanderwise_inc.app.ui.navigation.graph.Graph
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.R

@Composable
fun CreationStepPreview(
    navController: NavHostController = rememberNavController()
) {
    var selected by remember { mutableIntStateOf(0) }
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    if (selected == 0) {
                        selected = 1
                    } else {
                        selected = 0
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = if (selected == 0) R.drawable.map_icon else R.drawable.home_icon),
                    contentDescription = null,
                    tint = Color(0xFF191C1E),
                    modifier = Modifier
                        .size(30.dp)
                        .padding(2.dp)
                )   
            }
        }
    ) {padding ->
        NavHost(
            navController = navController,
            route = Graph.CREATION,
            startDestination = CreationStepsDestinations.ChooseLocations.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(route = CreationStepsDestinations.PreviewBanner.route) {
                CreationStepPreviewBanner()
            }
            composable(route = CreationStepsDestinations.PreviewItinerary.route) {
                CreationStepPreviewItinerary()
            }
        }
    }
}