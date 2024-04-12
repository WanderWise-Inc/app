package com.github.wanderwise_inc.app.ui.liked

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.ui.home.SearchBar
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.ui.navigation.Route
import com.github.wanderwise_inc.app.ui.navigation.TopNavigationMenu
import com.github.wanderwise_inc.app.ui.navigation.graph.LikedNavGraph
import com.github.wanderwise_inc.app.ui.navigation.graph.OverviewNavGraph
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

@Composable
fun LikedScreen(
    mapViewModel : MapViewModel,
    navController: NavHostController = rememberNavController()
) {
    //Text(text = "Welcome to the itinerary screen", Modifier.testTag("Itinerary Screen"))
    val navigator = NavigationActions(navController)
    Scaffold(
        topBar = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                SearchBar()               
                TopNavigationMenu(navigator, Route.LIKED)
            }
        },
        modifier = Modifier.testTag("Liked screen")
    ) {innerPadding ->
        LikedNavGraph(
            mapViewModel = mapViewModel,
            navController = navController,
            innerPadding = innerPadding
        )
    }
}