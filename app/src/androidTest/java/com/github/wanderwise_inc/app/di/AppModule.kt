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
import com.github.wanderwise_inc.app.data.DirectionsRepositoryImpl
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ImageRepositoryImpl
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.ItineraryRepositoryImpl
import com.github.wanderwise_inc.app.data.LocationsRepositoryImpl
import com.github.wanderwise_inc.app.data.ProfileRepositoryImpl
import com.github.wanderwise_inc.app.data.SignInLauncher
import com.github.wanderwise_inc.app.disk.SavedItinerariesSerializer
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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.launch

class AppModule(
    activity: MainActivity,
) {
  init {
    Log.d("ModuleProvider", "Using TestModule")
  }

  val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

  val firebaseStorage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

  private val firestore: FirebaseFirestore by lazy { Firebase.firestore }

  val directionsRepository by lazy {
    DirectionsRepositoryImpl(DirectionsApiServiceFactory.createDirectionsApiService())
  }

  val imageRepository: ImageRepository by lazy {
    ImageRepositoryImpl(imageLauncher, firebaseStorage.reference, null)
  }

  private val imageLauncher: ActivityResultLauncher<Intent> by lazy {
    activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      if (result.resultCode == Activity.RESULT_OK) {
        result.data?.data?.let { imageRepository.setCurrentFile(it) }
      }
    }
  }

  val itineraryRepository: ItineraryRepository by lazy {
    ItineraryRepositoryImpl(
        firestore,
        activity.applicationContext,
        activity.applicationContext.savedItinerariesDataStore)
  }

  private val Context.savedItinerariesDataStore: DataStore<SavedItineraries> by
      dataStore("saved_itineraries.pb", SavedItinerariesSerializer)

  val locationsRepository by lazy {
    LocationsRepositoryImpl(LocationsApiServiceFactory.createLocationsApiService())
  }

  val profileRepository by lazy { ProfileRepositoryImpl(firestore) }

  val bottomNavigationViewModel: BottomNavigationViewModel by lazy { BottomNavigationViewModel() }

  val createItineraryViewModel by lazy {
    CreateItineraryViewModel(
        itineraryRepository, directionsRepository, locationsRepository, locationClient)
  }

  val itineraryViewModel: ItineraryViewModel by lazy {
    ItineraryViewModel(
        itineraryRepository, directionsRepository, locationsRepository, locationClient)
  }

  private val locationClient: LocationClient by lazy {
    UserLocationClient(
        activity.applicationContext,
        LocationServices.getFusedLocationProviderClient(activity.applicationContext))
  }

  val loginViewModel: LoginViewModel by lazy { LoginViewModel(signInLauncher) }

  private val signInLauncher: SignInLauncher by lazy {
    val testUser = mockk<FirebaseUser>()
    every { testUser.uid } returns "test_user_id"
    every { testUser.displayName } returns "Test User"

    object : SignInLauncher {
      override fun signIn() {
        activity.lifecycleScope.launch {
          loginViewModel.handleSignInResult(profileViewModel, testUser)
        }
      }
    }
  }

  private val activityResultLauncher: ActivityResultLauncher<Intent> by lazy {
    activity.registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
      val user = if (res.resultCode == Activity.RESULT_OK) firebaseAuth.currentUser else null

      activity.lifecycleScope.launch { loginViewModel.handleSignInResult(profileViewModel, user) }
    }
  }

  val profileViewModel: ProfileViewModel by lazy {
    ProfileViewModel(profileRepository, imageRepository)
  }
}
