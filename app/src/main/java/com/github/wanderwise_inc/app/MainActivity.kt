package com.github.wanderwise_inc.app

// import com.github.wanderwise_inc.app.data.ImageRepositoryTestImpl

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.data.ImageRepositoryImpl
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ProfileRepositoryImpl
import com.github.wanderwise_inc.app.data.SignInRepositoryImpl
import com.github.wanderwise_inc.app.ui.navigation.graph.RootNavigationGraph
import com.github.wanderwise_inc.app.ui.theme.WanderWiseTheme
import com.github.wanderwise_inc.app.viewmodel.HomeViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.github.wanderwise_inc.app.viewmodel.SignInViewModel
import com.google.firebase.storage.FirebaseStorage

class MainActivity : ComponentActivity() {
  private val homeViewModel by viewModels<HomeViewModel>()
  // private val profileViewModel by viewModels<ProfileViewModel>()

  private val itineraryRepository = ItineraryRepositoryTestImpl()
  private val signInRepository = SignInRepositoryImpl()
  private val mapViewModel = MapViewModel(itineraryRepository)
  private lateinit var imageRepository: ImageRepositoryImpl

  // declaration for use of storage
  private var currentFile: Uri? = null
  private val storage = FirebaseStorage.getInstance()
  private var imageReference = storage.reference

  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var signInViewModel: SignInViewModel

  // private lateinit var analytics : FirebaseAnalytics
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val profileRepository = ProfileRepositoryImpl()
    imageRepository = ImageRepositoryImpl(imageLauncher, imageReference, null)
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
              imageRepository = imageRepository,
              navController = rememberNavController())
        }
      }
    }
  }

  private val imageLauncher =
      registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
          result?.data?.data.let {
            // use the global variable for access
            imageRepository.setCurrentFile(it)
            Log.d("STORE IMAGE", "CURRENTFILE SELECTED")
          }
        }
      }
}
