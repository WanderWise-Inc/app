package com.github.wanderwise_inc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.data.ImageRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ProfileRepositoryTestImpl
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.navigation.graph.RootNavigationGraph
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

    profileRepository.setProfile(Profile(userUid = "testing"))

    // START: viewmodel initialization (default data for demoing)
    val itineraryAdventureAndLuxury =
        Itinerary(
            userUid = "1",
            locations = listOf(),
            title = "Shopping then adventure",
            tags = listOf(ItineraryTags.ADVENTURE, ItineraryTags.LUXURY),
            description = "gucci",
            visible = true,
        )
    val itineraryAdventure =
        Itinerary(
            userUid = "1",
            locations = listOf(),
            title = "Hike",
            tags =
                listOf(
                    ItineraryTags.ACTIVE,
                    ItineraryTags.PHOTOGRAPHY,
                    ItineraryTags.NATURE,
                    ItineraryTags.ADVENTURE,
                    ItineraryTags.FOODIE,
                    ItineraryTags.RURAL,
                    ItineraryTags.WILDLIFE,
                    ItineraryTags.WELLNESS),
            description = null,
            visible = true,
        )

    val privateItinerary =
        Itinerary(
            userUid = "testing", // my UID!
            locations = listOf(),
            title = "My test itinerary",
            tags = listOf(ItineraryTags.ADVENTURE),
            description = null,
            visible = false,
        )
    mapViewModel.setItinerary(itineraryAdventure)
    mapViewModel.setItinerary(itineraryAdventureAndLuxury)
    mapViewModel.setItinerary(privateItinerary)
    // END: Viewmodel initialization

    setContent {
      WanderWiseTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
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
