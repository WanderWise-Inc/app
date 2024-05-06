package com.github.wanderwise_inc.app

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.GoogleSignInLauncher
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.data.SignInRepository
import com.github.wanderwise_inc.app.di.AppModule
import com.github.wanderwise_inc.app.ui.navigation.graph.RootNavigationGraph
import com.github.wanderwise_inc.app.ui.theme.WanderWiseTheme
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class MainActivity : ComponentActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage

    private lateinit var imageRepository: ImageRepository
    private lateinit var itineraryRepository: ItineraryRepository
    private lateinit var directionsRepository: DirectionsRepository
    private lateinit var profileRepository: ProfileRepository
    private lateinit var signInRepository: SignInRepository

    private lateinit var bottomNavigationViewModel: BottomNavigationViewModel
    private lateinit var mapViewModel: MapViewModel
    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var googleSignInLauncher: GoogleSignInLauncher

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()

        setContent {
            WanderWiseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    navController = rememberNavController()
                    RootNavigationGraph(
                        googleSignInLauncher,
                        profileViewModel,
                        mapViewModel,
                        bottomNavigationViewModel,
                        imageRepository,
                        navController,
                        firebaseAuth
                    )
                }
            }
        }
    }

    private fun init() {
        requestPermissions()

        firebaseAuth = AppModule.provideFirebaseAuth()
        firebaseStorage = AppModule.provideFirebaseStorage()

        initializeRepositories()

        initializeViewModels()

        googleSignInLauncher = AppModule.provideGoogleSignInLauncher(
            this,
            lifecycleScope,
            firebaseAuth,
            signInRepository,
            navController,
            profileViewModel
        )
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            0
        )
    }

    private fun initializeRepositories() {
        imageRepository = AppModule.provideImageRepository(this, imageRepository, firebaseStorage)
        itineraryRepository = AppModule.provideItineraryRepository()
        directionsRepository = AppModule.provideDirectionsRepository()
        profileRepository = AppModule.provideProfileRepository()
        signInRepository = AppModule.provideSignInRepository()
    }

    private fun initializeViewModels() {
        bottomNavigationViewModel = AppModule.provideBottomNavigationViewModel()
        mapViewModel = AppModule.provideMapViewModel(applicationContext, itineraryRepository, directionsRepository)
        profileViewModel = AppModule.provideProfileViewModel(profileRepository, imageRepository)
    }
}
