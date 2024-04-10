package com.github.wanderwise_inc.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.ui.navigation.BottomNavigationMenu
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.ui.navigation.Destination.TopLevelDestination
import com.github.wanderwise_inc.app.ui.navigation.graph.HomeNavGraph
import com.github.wanderwise_inc.app.viewmodel.HomeViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.wanderwise_inc.app.R

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    mapViewModel: MapViewModel,
    navController: NavHostController = rememberNavController(),
) {
    val navigationActions = NavigationActions(navController)
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surfaceContainer),
                        
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Button to obtain more info",
                        )
                    }
                    Text(text = "WanderWise")
                    TextButton(
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.person_icon), 
                            modifier = Modifier
                                .size(20.dp),
                            contentDescription = null
                        )
                    }
                }
            }
        },
        bottomBar = {
            /*
            BottomNavigationMenu(
                onTabSelect = { screen -> navigator.navigateTo(screen) },
                selectedItem = TopLevelDestination.Overview
            )
             */
            BottomNavigationMenu(navigationActions)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            HomeNavGraph(
                mapViewModel,
                navController,
                innerPadding
            )
        }
    }
}