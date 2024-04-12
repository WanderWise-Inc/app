package com.github.wanderwise_inc.app.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.ui.navigation.BottomNavigationMenu
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.ui.navigation.graph.HomeNavGraph
import com.github.wanderwise_inc.app.viewmodel.HomeViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel,
    navController: NavHostController = rememberNavController(),
) {
    val navigationActions = NavigationActions(navController)
    Scaffold(
        topBar = {
            /*TopAppBar(
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.primarySurface,
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = { *//*TODO*//* },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colors.primarySurface),
                        
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Button to obtain more info",
                        )
                    }
                    Text(text = "WanderWise")
                    TextButton(
                        onClick = { *//*TODO*//* },
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
            }*/
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
                profileViewModel
                // innerPadding
            )
        }
    }
}