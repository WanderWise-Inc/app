package com.github.wanderwise_inc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.data.ImageRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ProfileRepositoryTestImpl
import com.github.wanderwise_inc.app.ui.navigation.graph.RootNavigationGraph
import com.github.wanderwise_inc.app.ui.profile.ProfileScreen
import com.github.wanderwise_inc.app.ui.theme.WanderWiseTheme
import com.github.wanderwise_inc.app.viewmodel.HomeViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

class MainActivity : ComponentActivity() {
  private val homeViewModel by viewModels<HomeViewModel>()
  // private val profileViewModel by viewModels<ProfileViewModel>()

  private val itineraryRepository = ItineraryRepositoryTestImpl()
  private val mapViewModel = MapViewModel(itineraryRepository)

  private lateinit var profileViewModel: ProfileViewModel

  // private lateinit var analytics : FirebaseAnalytics
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val profileRepository = ProfileRepositoryTestImpl()
    val imageRepository = ImageRepositoryTestImpl(application)
    profileViewModel = ProfileViewModel(profileRepository, imageRepository)

    setContent {
      WanderWiseTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        ProfileScreen(mapViewModel = mapViewModel, profileViewModel = profileViewModel)  
        // HomeScreen(homeViewModel, mapViewModel)
          /*RootNavigationGraph(
              application.applicationContext,
              homeViewModel = homeViewModel,
              profileViewModel = profileViewModel,
              mapViewModel = mapViewModel,
              navController = rememberNavController())*/
        }
      }
    }
  }
}
