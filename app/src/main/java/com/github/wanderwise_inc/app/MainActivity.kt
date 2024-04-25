package com.github.wanderwise_inc.app

// import com.github.wanderwise_inc.app.data.ImageRepositoryTestImpl

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.data.ImageRepositoryImpl
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ProfileRepositoryImpl
import com.github.wanderwise_inc.app.ui.navigation.graph.RootNavigationGraph
import com.github.wanderwise_inc.app.ui.theme.WanderWiseTheme
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.storage.FirebaseStorage

class MainActivity : ComponentActivity() {
  private val itineraryRepository = ItineraryRepositoryTestImpl()
  private val mapViewModel = MapViewModel(itineraryRepository)
  private lateinit var imageRepository: ImageRepositoryImpl

  // declaration for use of storage
  private val storage = FirebaseStorage.getInstance()
  private var imageReference = storage.reference

  private lateinit var profileViewModel: ProfileViewModel

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
              profileViewModel = profileViewModel,
              mapViewModel = mapViewModel,
              imageRepository = imageRepository,
              navController = rememberNavController())
        }
      }
    }
  }

  /**
   * image launcher Used to set launch an activity that will set the currentFile of the
   * imageRepository to the selected file by the user (the one in the photo gallery) Launcher that
   * will be called in the imageRepository
   */
  private val imageLauncher =
      registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
          result?.data?.data.let {
            imageRepository.setCurrentFile(it)
            Log.d("STORE IMAGE", "CURRENTFILE SELECTED")
          }
        }
      }
}
