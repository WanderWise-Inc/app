package com.github.wanderwise_inc.app.di

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.github.wanderwise_inc.app.data.DefaultGoogleSignInLauncher
import com.github.wanderwise_inc.app.data.DirectionsRepositoryImpl
import com.github.wanderwise_inc.app.data.ImageRepositoryImpl
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ProfileRepositoryTestImpl
import com.github.wanderwise_inc.app.data.SignInRepositoryImpl
import com.github.wanderwise_inc.app.network.ApiServiceFactory
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.LocationClient
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

object AppModule {

  private lateinit var imageLauncher: ActivityResultLauncher<Intent>
  private lateinit var signInLauncher: ActivityResultLauncher<Intent>
  private lateinit var sinInIntent: Intent
  private lateinit var locationClient: LocationClient

  init {
    Log.d("ModuleProvider", "Using AppModule")
  }

  fun initialize(
      imageLauncher: ActivityResultLauncher<Intent>,
      signInLauncher: ActivityResultLauncher<Intent>,
      sinInIntent: Intent,
      locationClient: LocationClient
  ) {
    this.imageLauncher = imageLauncher
    this.signInLauncher = signInLauncher
    this.sinInIntent = sinInIntent
    this.locationClient = locationClient
  }

  val firebaseAuth by lazy { FirebaseAuth.getInstance() }

  val firebaseStorage by lazy { FirebaseStorage.getInstance() }

  val imageRepository by lazy {
    ImageRepositoryImpl(imageLauncher, firebaseStorage.reference, null)
  }

  val directionsRepository by lazy {
    DirectionsRepositoryImpl(ApiServiceFactory.createDirectionsApiService())
  }

  val itineraryRepository by lazy { ItineraryRepositoryTestImpl() }

  val profileRepository by lazy { ProfileRepositoryTestImpl() }

  val signInRepository by lazy { SignInRepositoryImpl() }

  val bottomNavigationViewModel by lazy { BottomNavigationViewModel() }

  val createItineraryViewModel by lazy {
    CreateItineraryViewModel(itineraryRepository, directionsRepository, locationClient)
  }

  val itineraryViewModel by lazy {
    ItineraryViewModel(itineraryRepository, directionsRepository, locationClient)
  }

  val profileViewModel by lazy { ProfileViewModel(profileRepository, imageRepository) }

  val googleSignInLauncher by lazy { DefaultGoogleSignInLauncher(signInLauncher, sinInIntent) }
}
