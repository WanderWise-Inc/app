package com.github.wanderwise_inc.app

// import com.github.wanderwise_inc.app.data.ProfileRepositoryImpl
import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ImageRepositoryImpl
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ProfileRepositoryTestImpl
import com.github.wanderwise_inc.app.network.ApiServiceFactory
import com.github.wanderwise_inc.app.ui.navigation.graph.RootNavigationGraph
import com.github.wanderwise_inc.app.ui.theme.WanderWiseTheme
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.github.wanderwise_inc.app.viewmodel.UserLocationClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class MainActivity : ComponentActivity() {
  private lateinit var imageRepository: ImageRepositoryImpl
  private val directionsApiService = ApiServiceFactory.createDirectionsApiService()
  private val directionsRepository = DirectionsRepository(directionsApiService)
  private lateinit var mapViewModel: MapViewModel

  // declaration for use of storage
  private val storage = FirebaseStorage.getInstance()
  private var imageReference = storage.reference

  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var bottomNavigationViewModel: BottomNavigationViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val profileRepository = ProfileRepositoryTestImpl()
    imageRepository = ImageRepositoryImpl(imageLauncher, imageReference, null)

    // Ask for location permissions
    ActivityCompat.requestPermissions(
        this,
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
        0)

    val itineraryRepository = ItineraryRepositoryTestImpl()
    val userLocationClient =
        UserLocationClient(
            applicationContext, LocationServices.getFusedLocationProviderClient(applicationContext))

    mapViewModel = MapViewModel(itineraryRepository, directionsRepository, userLocationClient)
    profileViewModel = ProfileViewModel(profileRepository, imageRepository)
    bottomNavigationViewModel = BottomNavigationViewModel()

    setContent {
      WanderWiseTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          RootNavigationGraph(
              application.applicationContext,
              profileViewModel = profileViewModel,
              mapViewModel = mapViewModel,
              bottomNavigationViewModel = bottomNavigationViewModel,
              imageRepository = imageRepository,
              navController = rememberNavController(),
              firebaseAuth = FirebaseAuth.getInstance(),
          )
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
