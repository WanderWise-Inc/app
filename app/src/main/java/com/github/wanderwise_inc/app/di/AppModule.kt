package com.github.wanderwise_inc.app.di

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.lifecycleScope
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.wanderwise_inc.app.MainActivity
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.DirectionsRepositoryImpl
import com.github.wanderwise_inc.app.data.GoogleSignInLauncher
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ImageRepositoryImpl
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.ItineraryRepositoryImpl
import com.github.wanderwise_inc.app.data.LocationsRepository
import com.github.wanderwise_inc.app.data.LocationsRepositoryImpl
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.data.ProfileRepositoryImpl
import com.github.wanderwise_inc.app.data.SignInLauncher
import com.github.wanderwise_inc.app.disk.SavedItinerariesSerializer
import com.github.wanderwise_inc.app.isNetworkAvailable
import com.github.wanderwise_inc.app.network.DirectionsApiServiceFactory
import com.github.wanderwise_inc.app.network.LocationsApiServiceFactory
import com.github.wanderwise_inc.app.proto.location.SavedItineraries
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.LocationClient
import com.github.wanderwise_inc.app.viewmodel.LoginViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.github.wanderwise_inc.app.viewmodel.UserLocationClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

/**
 * `AppModule` is a class that provides dependencies for the application.
 *
 * This class is responsible for creating and providing instances of various classes used throughout
 * the application. It uses the lazy initialization pattern to ensure that instances are only
 * created when they are needed.
 *
 * @constructor Creates an instance of `AppModule` for the given activity.
 * @property activity The main activity of the application.
 */
class AppModule(
    private val activity: MainActivity,
) {
  init {
    Log.d("ModuleProvider", "Using AppModule")
  }

  /** The `FirebaseAuth` instance used for authentication. */
  val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

  /** The `FirebaseStorage` instance used for storing images. */
  val firebaseStorage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

  /** The `FirebaseFirestore` instance used for storing data. */
  private val firestore: FirebaseFirestore by lazy { Firebase.firestore }

  /** The `DirectionsRepository` instance used for fetching directions. */
  val directionsRepository: DirectionsRepository by lazy {
    DirectionsRepositoryImpl(DirectionsApiServiceFactory.createDirectionsApiService())
  }

  /** The `ImageRepository` instance used for storing images. */
  val imageRepository: ImageRepository by lazy {
    ImageRepositoryImpl(imageLauncher, firebaseStorage.reference, null)
  }

  /** The `ActivityResultLauncher` used for launching the image picker. */
  private val imageLauncher: ActivityResultLauncher<Intent> by lazy {
    activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      if (result.resultCode == Activity.RESULT_OK) {
        result.data?.data?.let { imageRepository.setCurrentFile(it) }
      }
    }
  }

  /** The `ItineraryRepository` instance used for storing itineraries. */
  val itineraryRepository: ItineraryRepository by lazy {
    ItineraryRepositoryImpl(
        firestore,
        activity.applicationContext,
        activity.applicationContext.savedItinerariesDataStore)
  }

  /** The `DataStore` used for storing saved itineraries. */
  private val Context.savedItinerariesDataStore: DataStore<SavedItineraries> by
      dataStore("saved_itineraries.pb", SavedItinerariesSerializer)

  /** The `LocationsRepository` instance used for fetching locations. */
  val locationsRepository: LocationsRepository by lazy {
    LocationsRepositoryImpl(LocationsApiServiceFactory.createLocationsApiService())
  }

  /** The `ProfileRepository` instance used for fetching user profiles. */
  val profileRepository: ProfileRepository by lazy {
    ProfileRepositoryImpl(firestore, activity.applicationContext)
  }

  /** The `BottomNavigationViewModel` instance used for managing the bottom navigation bar. */
  val bottomNavigationViewModel: BottomNavigationViewModel by lazy { BottomNavigationViewModel() }

  /** The `CreateItineraryViewModel` instance used for creating itineraries. */
  val createItineraryViewModel by lazy {
    CreateItineraryViewModel(
        itineraryRepository, directionsRepository, locationsRepository, locationClient)
  }

  /** The `ItineraryViewModel` instance used for managing itineraries. */
  val itineraryViewModel: ItineraryViewModel by lazy {
    ItineraryViewModel(
        itineraryRepository, directionsRepository, locationsRepository, locationClient)
  }

  /** The `LocationClient` instance used for fetching user locations. */
  private val locationClient: LocationClient by lazy {
    UserLocationClient(
        activity.applicationContext,
        LocationServices.getFusedLocationProviderClient(activity.applicationContext))
  }

  /** The `LoginViewModel` instance used for managing user authentication. */
  val loginViewModel: LoginViewModel by lazy {
    LoginViewModel(signInLauncher, activity.applicationContext.isNetworkAvailable())
  }

  /** The `SignInLauncher` instance used for launching the sign-in flow. */
  private val signInLauncher: SignInLauncher by lazy { GoogleSignInLauncher(signInResultLauncher) }

  /** The `ActivityResultLauncher` used for launching the sign-in flow. */
  private val signInResultLauncher: ActivityResultLauncher<Intent> by lazy {
    activity.registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
      val user = if (res.resultCode == Activity.RESULT_OK) firebaseAuth.currentUser else null

      activity.lifecycleScope.launch { loginViewModel.handleSignInResult(profileViewModel, user) }
    }
  }

  /** The `ProfileViewModel` instance used for managing user profiles. */
  val profileViewModel: ProfileViewModel by lazy {
    ProfileViewModel(profileRepository, imageRepository)
  }
}
