package com.github.wanderwise_inc.app.di

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.wanderwise_inc.app.MainActivity
import com.github.wanderwise_inc.app.data.DefaultGoogleSignInLauncher
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.DirectionsRepositoryImpl
import com.github.wanderwise_inc.app.data.GoogleSignInLauncher
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ImageRepositoryImpl
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.data.ProfileRepositoryTestImpl
import com.github.wanderwise_inc.app.data.SignInRepository
import com.github.wanderwise_inc.app.data.SignInRepositoryImpl
import com.github.wanderwise_inc.app.network.ApiServiceFactory
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.LocationClient
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.github.wanderwise_inc.app.viewmodel.UserLocationClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class AppModule(
    activity: MainActivity,
    navController: NavController
) {

    init {
        Log.d("ModuleProvider", "Using AppModule")
    }

    val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    val firebaseStorage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    val imageRepository: ImageRepository by lazy {
        ImageRepositoryImpl(imageLauncher, firebaseStorage.reference, null)
    }

    val imageLauncher: ActivityResultLauncher<Intent> by lazy {
        activity
            .registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == ComponentActivity.RESULT_OK) {
                    result.data?.data?.let {
                        imageRepository.setCurrentFile(it)
                        Log.d("STORE IMAGE", "CURRENT FILE SELECTED")
                    }
                }
            }
    }

    val directionsRepository: DirectionsRepository by lazy {
        DirectionsRepositoryImpl(ApiServiceFactory.createDirectionsApiService())
    }

    val itineraryRepository: ItineraryRepository by lazy { ItineraryRepositoryTestImpl() }

    val profileRepository: ProfileRepository by lazy { ProfileRepositoryTestImpl() }

    val signInRepository: SignInRepository by lazy { SignInRepositoryImpl() }

    val bottomNavigationViewModel: BottomNavigationViewModel by lazy { BottomNavigationViewModel() }

    val createItineraryViewModel: CreateItineraryViewModel by lazy {
        CreateItineraryViewModel(itineraryRepository, directionsRepository, locationClient)
    }

    val itineraryViewModel: ItineraryViewModel by lazy {
        ItineraryViewModel(itineraryRepository, directionsRepository, locationClient)
    }

    val locationClient: LocationClient by lazy {
        UserLocationClient(activity.applicationContext, LocationServices.getFusedLocationProviderClient(activity.applicationContext))
    }

    val profileViewModel: ProfileViewModel by lazy { ProfileViewModel(profileRepository, imageRepository) }

    val googleSignInLauncher: GoogleSignInLauncher by lazy { DefaultGoogleSignInLauncher(signInLauncher, sinInIntent) }

    private val signInLauncher: ActivityResultLauncher<Intent> by lazy {
        activity.registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
            /* on failure, don't throw an exception. Pass the null value down for proper handling */
            val currUser = if (res.resultCode == ComponentActivity.RESULT_OK) firebaseAuth.currentUser else null

            Log.d("MainActivity", "Firebase sign-in result: $currUser")
            activity.lifecycleScope.launch { signInRepository.signIn(navController, profileViewModel, currUser) }
        }
    }

    val sinInIntent: Intent by lazy {
        AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()
    }

    val providers by lazy { listOf(AuthUI.IdpConfig.GoogleBuilder().build()) }
}
