package com.github.wanderwise_inc.app.di

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.datastore.core.DataStore
import com.github.wanderwise_inc.app.data.DefaultGoogleSignInLauncher
import com.github.wanderwise_inc.app.data.DirectionsRepositoryImpl
import com.github.wanderwise_inc.app.data.ImageRepositoryImpl
import com.github.wanderwise_inc.app.data.ItineraryRepositoryImpl
import com.github.wanderwise_inc.app.data.LocationsRepositoryImpl
import com.github.wanderwise_inc.app.data.ProfileRepositoryImpl
import com.github.wanderwise_inc.app.data.SignInRepositoryImpl
import com.github.wanderwise_inc.app.network.DirectionsApiServiceFactory
import com.github.wanderwise_inc.app.network.LocationsApiServiceFactory
import com.github.wanderwise_inc.app.proto.location.SavedItineraries
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.LocationClient
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@SuppressLint("StaticFieldLeak")
object AppModule {

  private lateinit var imageLauncher: ActivityResultLauncher<Intent>
  private lateinit var signInLauncher: ActivityResultLauncher<Intent>
  private lateinit var sinInIntent: Intent
  private lateinit var locationClient: LocationClient
  private lateinit var savedItinerariesDataStore: DataStore<SavedItineraries>
  private lateinit var context: Context
  lateinit var firebaseAuth: FirebaseAuth
  lateinit var db: FirebaseFirestore
  lateinit var firebaseStorage: FirebaseStorage

  init {
    Log.d("ModuleProvider", "Using AppModule")
  }

  fun initialize(
      imageLauncher: ActivityResultLauncher<Intent>,
      signInLauncher: ActivityResultLauncher<Intent>,
      sinInIntent: Intent,
      locationClient: LocationClient,
      savedItinerariesDataStore: DataStore<SavedItineraries>,
      context: Context,
      firebaseAuth: FirebaseAuth,
      firebaseFirestore: FirebaseFirestore,
      firebaseStorage: FirebaseStorage
  ) {
    this.imageLauncher = imageLauncher
    this.signInLauncher = signInLauncher
    this.sinInIntent = sinInIntent
    this.locationClient = locationClient
    this.savedItinerariesDataStore = savedItinerariesDataStore
    this.context = context
    this.firebaseAuth = firebaseAuth
    this.db = firebaseFirestore
    this.firebaseStorage = firebaseStorage
  }

  val imageRepository by lazy {
    ImageRepositoryImpl(imageLauncher, firebaseStorage.reference, null)
  }

  val directionsRepository by lazy {
    DirectionsRepositoryImpl(DirectionsApiServiceFactory.createDirectionsApiService())
  }

  val locationsRepository by lazy {
    LocationsRepositoryImpl(LocationsApiServiceFactory.createLocationsApiService())
  }

  val itineraryRepository by lazy {
    ItineraryRepositoryImpl(db, context, savedItinerariesDataStore)
  }

  val profileRepository by lazy { ProfileRepositoryImpl(db) }

  val signInRepository by lazy { SignInRepositoryImpl() }

  val bottomNavigationViewModel by lazy { BottomNavigationViewModel() }

  val createItineraryViewModel by lazy {
    CreateItineraryViewModel(
        itineraryRepository, directionsRepository, locationsRepository, locationClient)
  }

  val itineraryViewModel by lazy {
    ItineraryViewModel(
        itineraryRepository, directionsRepository, locationsRepository, locationClient)
  }

  val profileViewModel by lazy { ProfileViewModel(profileRepository, imageRepository) }

  val googleSignInLauncher by lazy { DefaultGoogleSignInLauncher(signInLauncher, sinInIntent) }
}
