package com.github.wanderwise_inc.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.ui.navigation.BottomNavigationMenu
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.ui.navigation.graph.HomeNavGraph
import com.github.wanderwise_inc.app.viewmodel.HomeViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel

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
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.primarySurface,
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colors.primarySurface),
                        
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Button to obtain more info",
                        )
                    }
                    Text(text = "WanderWise")
                    TextButton(
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colors.primarySurface)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.settings_icon), 
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