package com.github.wanderwise_inc.app.di

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.github.wanderwise_inc.app.data.DefaultGoogleSignInLauncher
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ImageRepositoryImpl
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.data.ProfileRepositoryTestImpl
import com.github.wanderwise_inc.app.data.SignInRepositoryImpl
import com.github.wanderwise_inc.app.network.ApiServiceFactory
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.github.wanderwise_inc.app.viewmodel.UserLocationClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

object AppModule {

    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    fun provideFirebaseStorage() = FirebaseStorage.getInstance()

    fun provideImageRepository(
        imageLauncher: ActivityResultLauncher<Intent>,
        firebaseStorage: FirebaseStorage
    ): ImageRepository {
        return ImageRepositoryImpl(
            imageLauncher,
            firebaseStorage.reference,
            null
        )
    }

    fun provideDirectionsRepository() = DirectionsRepository(
        ApiServiceFactory.createDirectionsApiService()
    )

    fun provideItineraryRepository() = ItineraryRepositoryTestImpl()

    fun provideProfileRepository() = ProfileRepositoryTestImpl()

    fun provideSignInRepository() = SignInRepositoryImpl()

    fun provideBottomNavigationViewModel() = BottomNavigationViewModel()

    fun provideMapViewModel(
        context: Context,
        itineraryRepository: ItineraryRepository,
        directionsRepository: DirectionsRepository
    ) = MapViewModel(
        itineraryRepository,
        directionsRepository,
        provideLocationClient(context)
    )

    fun provideProfileViewModel(
        profileRepository: ProfileRepository,
        imageRepository: ImageRepository
    ) = ProfileViewModel(
        profileRepository,
        imageRepository
    )

    fun provideGoogleSignInLauncher(
        signInLauncher: ActivityResultLauncher<Intent>
    ) = DefaultGoogleSignInLauncher(
        signInLauncher,
        provideSignInIntent()
    )

    private fun provideLocationClient(context: Context) = UserLocationClient(
        context,
        LocationServices.getFusedLocationProviderClient(context)
    )

    private val providers = listOf(
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    private fun provideSignInIntent() = AuthUI
        .getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .build()
}