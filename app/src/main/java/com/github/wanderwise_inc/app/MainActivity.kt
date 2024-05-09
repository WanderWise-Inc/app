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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.wanderwise_inc.app.data.DefaultGoogleSignInLauncher
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.GoogleSignInLauncher
import com.github.wanderwise_inc.app.data.ImageRepositoryImpl
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.data.LocationsRepository
import com.github.wanderwise_inc.app.data.ProfileRepositoryTestImpl
import com.github.wanderwise_inc.app.data.SignInRepositoryImpl
import com.github.wanderwise_inc.app.network.DirectionsApiServiceFactory
import com.github.wanderwise_inc.app.network.LocationsApiServiceFactory
import com.github.wanderwise_inc.app.ui.creation.CreationScreen
import com.github.wanderwise_inc.app.ui.creation.steps.CreateItineraryMapWithSelector
import com.github.wanderwise_inc.app.ui.home.HomeScreen
import com.github.wanderwise_inc.app.ui.navigation.graph.RootNavigationGraph
import com.github.wanderwise_inc.app.ui.theme.WanderWiseTheme
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.github.wanderwise_inc.app.viewmodel.UserLocationClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  private lateinit var imageRepository: ImageRepositoryImpl
  private val directionsApiService = DirectionsApiServiceFactory.createDirectionsApiService()
  private val locationsApiService = LocationsApiServiceFactory.createDirectionsApiService()
  private val directionsRepository = DirectionsRepository(directionsApiService)
  private val locationsRepository = LocationsRepository(locationsApiService) 
private lateinit var itineraryViewModel: ItineraryViewModel
  private lateinit var createItineraryViewModel: CreateItineraryViewModel
  private val signInRepositoryImpl = SignInRepositoryImpl()
  private lateinit var googleSignInLauncher: GoogleSignInLauncher

  // declaration for use of storage
  private val storage = FirebaseStorage.getInstance()
  private var imageReference = storage.reference
  private lateinit var navController: NavHostController

  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var bottomNavigationViewModel: BottomNavigationViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
    val signInIntent =
        AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()

    val signInLauncher =
        registerForActivityResult(
            FirebaseAuthUIActivityResultContract(),
        ) { res ->
          lifecycleScope.launch {
            val user = FirebaseAuth.getInstance().currentUser
            signInRepositoryImpl.signIn(res, navController, profileViewModel, user, res.resultCode)
          }
        }

    googleSignInLauncher = DefaultGoogleSignInLauncher(signInLauncher, signInIntent)

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

    itineraryViewModel =
        ItineraryViewModel(itineraryRepository, directionsRepository, locationsRepository, userLocationClient)
    profileViewModel = ProfileViewModel(profileRepository, imageRepository)
    createItineraryViewModel =
        CreateItineraryViewModel(itineraryRepository, directionsRepository, locationsRepository, userLocationClient)
    bottomNavigationViewModel = BottomNavigationViewModel()

    setContent {
      WanderWiseTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          navController = rememberNavController()
            CreationScreen(
                createItineraryViewModel = createItineraryViewModel,
                profileViewModel = profileViewModel,
                firebaseAuth = FirebaseAuth.getInstance()
            )
            /*RootNavigationGraph(
              googleSignInLauncher = googleSignInLauncher,
              profileViewModel = profileViewModel,
              itineraryViewModel = itineraryViewModel,
              createItineraryViewModel = createItineraryViewModel,
              bottomNavigationViewModel = bottomNavigationViewModel,
              imageRepository = imageRepository,
              navController = navController,
              firebaseAuth = FirebaseAuth.getInstance(),
          )*/
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
