package com.github.wanderwise_inc.app.di

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.GoogleSignInLauncher
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.data.SignInRepository
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

interface ModuleProvider {
  fun provideFirebaseAuth(): FirebaseAuth

  fun provideFirebaseStorage(): FirebaseStorage

  fun provideImageRepository(
      imageLauncher: ActivityResultLauncher<Intent>,
      firebaseStorage: FirebaseStorage
  ): ImageRepository

  fun provideDirectionsRepository(): DirectionsRepository

  fun provideItineraryRepository(): ItineraryRepository

  fun provideProfileRepository(): ProfileRepository

  fun provideSignInRepository(): SignInRepository

  fun provideBottomNavigationViewModel(): BottomNavigationViewModel

  fun provideMapViewModel(
      context: Context,
      itineraryRepository: ItineraryRepository,
      directionsRepository: DirectionsRepository
  ): MapViewModel

  fun provideProfileViewModel(
      profileRepository: ProfileRepository,
      imageRepository: ImageRepository
  ): ProfileViewModel

  fun provideGoogleSignInLauncher(
      signInLauncher: ActivityResultLauncher<Intent>
  ): GoogleSignInLauncher
}
