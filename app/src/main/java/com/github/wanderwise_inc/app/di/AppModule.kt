package com.github.wanderwise_inc.app.di

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.github.wanderwise_inc.app.data.DefaultGoogleSignInLauncher
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.DirectionsRepositoryImpl
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

object AppModule : ModuleProvider {

    override fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    override fun provideFirebaseStorage() = FirebaseStorage.getInstance()

    override fun provideImageRepository(
        imageLauncher: ActivityResultLauncher<Intent>,
        firebaseStorage: FirebaseStorage
    ): ImageRepository {
        return ImageRepositoryImpl(imageLauncher, firebaseStorage.reference, null)
    }

    override fun provideDirectionsRepository() =
        DirectionsRepositoryImpl(ApiServiceFactory.createDirectionsApiService())

    override fun provideItineraryRepository() = ItineraryRepositoryTestImpl()

    override fun provideProfileRepository() = ProfileRepositoryTestImpl()

    override fun provideSignInRepository() = SignInRepositoryImpl()

    override fun provideBottomNavigationViewModel() = BottomNavigationViewModel()

    override fun provideMapViewModel(
        context: Context,
        itineraryRepository: ItineraryRepository,
        directionsRepository: DirectionsRepository
    ) = MapViewModel(itineraryRepository, directionsRepository, provideLocationClient(context))

    override fun provideProfileViewModel(
        profileRepository: ProfileRepository,
        imageRepository: ImageRepository
    ) = ProfileViewModel(profileRepository, imageRepository)

    override fun provideGoogleSignInLauncher(signInLauncher: ActivityResultLauncher<Intent>) =
        DefaultGoogleSignInLauncher(signInLauncher, provideSignInIntent())

    private fun provideLocationClient(context: Context) =
        UserLocationClient(context, LocationServices.getFusedLocationProviderClient(context))

    private val providers = listOf(AuthUI.IdpConfig.GoogleBuilder().build())

    private fun provideSignInIntent() =
        AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()
}
