package com.github.wanderwise_inc.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ImageRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ProfileRepositoryTestImpl
import com.github.wanderwise_inc.app.network.ApiServiceFactory
import com.github.wanderwise_inc.app.ui.navigation.graph.RootNavigationGraph
import com.github.wanderwise_inc.app.ui.theme.WanderWiseTheme
import com.github.wanderwise_inc.app.viewmodel.HomeViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

class MainActivity : ComponentActivity() {
  private val homeViewModel by viewModels<HomeViewModel>()
  // private val profileViewModel by viewModels<ProfileViewModel>()

  val url =
      "https://maps.googleapis.com/maps/api/directions/json" +
          "?destination=Montreal" +
          "&origin=Toronto" +
          "&key=${BuildConfig.MAPS_API_KEY}"

  private val itineraryRepository = ItineraryRepositoryTestImpl()
  private val directionsApiService = ApiServiceFactory.createDirectionsApiService()
  private val directionsRepository = DirectionsRepository(directionsApiService)
  private val mapViewModel = MapViewModel(itineraryRepository, directionsRepository)

  private lateinit var profileViewModel: ProfileViewModel

  // private lateinit var analytics : FirebaseAnalytics
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    Log.d("url", "url = $url")
    val profileRepository = ProfileRepositoryTestImpl()
    val imageRepository = ImageRepositoryTestImpl(application)
    profileViewModel = ProfileViewModel(profileRepository, imageRepository)

    setContent {
      WanderWiseTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          // HomeScreen(homeViewModel, mapViewModel)
          RootNavigationGraph(
              application.applicationContext,
              homeViewModel = homeViewModel,
              profileViewModel = profileViewModel,
              mapViewModel = mapViewModel,
              navController = rememberNavController())
        }
      }
    }
  }
}
