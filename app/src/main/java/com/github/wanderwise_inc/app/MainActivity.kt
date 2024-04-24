package com.github.wanderwise_inc.app

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.data.ImageRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ProfileRepositoryTestImpl
import com.github.wanderwise_inc.app.ui.navigation.graph.RootNavigationGraph
import com.github.wanderwise_inc.app.ui.theme.WanderWiseTheme
import com.github.wanderwise_inc.app.viewmodel.HomeViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.github.wanderwise_inc.app.viewmodel.UserLocationClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {
    private val homeViewModel by viewModels<HomeViewModel>()

    private lateinit var mapViewModel: MapViewModel

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ask for location permissions
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            0
        )

        val itineraryRepository = ItineraryRepositoryTestImpl()
        val userLocationClient = UserLocationClient(
                applicationContext,
                LocationServices.getFusedLocationProviderClient(applicationContext)
        )
        mapViewModel = MapViewModel(itineraryRepository, userLocationClient)

        val profileRepository = ProfileRepositoryTestImpl()
        val imageRepository = ImageRepositoryTestImpl(application)
        profileViewModel = ProfileViewModel(profileRepository, imageRepository)

        setContent {
            WanderWiseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // HomeScreen(homeViewModel, mapViewModel)
                    RootNavigationGraph(
                        application.applicationContext,
                        homeViewModel = homeViewModel,
                        profileViewModel = profileViewModel,
                        mapViewModel = mapViewModel,
                        navController = rememberNavController()
                    )
                }
            }
        }
    }
}
