package com.github.wanderwise_inc.app

import android.Manifest
import android.content.Context
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
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.GoogleSignInLauncher
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.LocationsRepository
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.data.SignInRepository
import com.github.wanderwise_inc.app.di.AppModule
import com.github.wanderwise_inc.app.disk.SavedItinerariesSerializer
import com.github.wanderwise_inc.app.proto.location.SavedItineraries
import com.github.wanderwise_inc.app.ui.navigation.graph.RootNavigationGraph
import com.github.wanderwise_inc.app.ui.theme.WanderWiseTheme
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.github.wanderwise_inc.app.viewmodel.UserLocationClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

  private lateinit var firebaseAuth: FirebaseAuth
  private lateinit var firebaseStorage: FirebaseStorage

  private lateinit var imageRepository: ImageRepository
  private lateinit var itineraryRepository: ItineraryRepository
  private lateinit var directionsRepository: DirectionsRepository
  private lateinit var locationsRepository: LocationsRepository
  private lateinit var profileRepository: ProfileRepository
  private lateinit var signInRepository: SignInRepository

  private lateinit var bottomNavigationViewModel: BottomNavigationViewModel
  private lateinit var createItineraryViewModel: CreateItineraryViewModel
  private lateinit var itineraryViewModel: ItineraryViewModel
  private lateinit var profileViewModel: ProfileViewModel

  private lateinit var googleSignInLauncher: GoogleSignInLauncher

  private lateinit var navController: NavHostController

  private val providers by lazy { listOf(AuthUI.IdpConfig.GoogleBuilder().build()) }

  private val imageLauncher by lazy {
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
      if (res.resultCode == RESULT_OK) {
        res.data?.data?.let {
          imageRepository.setCurrentFile(it)
          Log.d("STORE IMAGE", "CURRENT FILE SELECTED")
        }
      }
    }
  }

  private val signInLauncher by lazy {
    registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
      /* on failure, don't throw an exception. Pass the null value down for proper handling */
      val currUser = if (res.resultCode == RESULT_OK) firebaseAuth.currentUser else null

      Log.d("MainActivity", "Firebase sign-in result: $currUser")
      lifecycleScope.launch { signInRepository.signIn(navController, profileViewModel, currUser) }
    }
  }

  private val signInIntent by lazy {
    AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()
  }

  private val locationClient by lazy {
    UserLocationClient(
        applicationContext, LocationServices.getFusedLocationProviderClient(applicationContext))
  }

  private val Context.savedItinerariesDataStore: DataStore<SavedItineraries> by
      dataStore(fileName = "saved_itineraries.pb", serializer = SavedItinerariesSerializer)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    init()

    setContent {
      WanderWiseTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          navController = rememberNavController()
          RootNavigationGraph(
              googleSignInLauncher,
              profileViewModel,
              itineraryViewModel,
              createItineraryViewModel,
              bottomNavigationViewModel,
              imageRepository,
              navController)
        }
      }
    }
  }

  private fun init() {
    requestPermissions()

    AppModule.initialize(
        imageLauncher,
        signInLauncher,
        signInIntent,
        locationClient,
        savedItinerariesDataStore,
        applicationContext,
        FirebaseAuth.getInstance(),
        FirebaseFirestore.getInstance(),
        FirebaseStorage.getInstance())

    firebaseAuth = AppModule.firebaseAuth
    firebaseStorage = AppModule.firebaseStorage

    initializeRepositories()

    initializeViewModels()

    googleSignInLauncher = AppModule.googleSignInLauncher
  }

  private fun requestPermissions() {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
        0)
  }

  private fun initializeRepositories() {
    imageRepository = AppModule.imageRepository
    itineraryRepository = AppModule.itineraryRepository
    directionsRepository = AppModule.directionsRepository
    locationsRepository = AppModule.locationsRepository
    profileRepository = AppModule.profileRepository
    signInRepository = AppModule.signInRepository
  }

  private fun initializeViewModels() {
    bottomNavigationViewModel = AppModule.bottomNavigationViewModel
    createItineraryViewModel = AppModule.createItineraryViewModel
    itineraryViewModel = AppModule.itineraryViewModel
    profileViewModel = AppModule.profileViewModel
  }
}
