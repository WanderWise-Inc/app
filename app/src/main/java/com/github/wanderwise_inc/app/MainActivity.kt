package com.github.wanderwise_inc.app

import android.Manifest
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
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
import com.github.wanderwise_inc.app.viewmodel.UserLocationClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

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

    private val providers by lazy {
        listOf(AuthUI.IdpConfig.GoogleBuilder().build())
    }

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
            if (res.resultCode != RESULT_OK) throw Exception("User unsuccessful sign in")

            lifecycleScope.launch {
                signInRepository.signIn(navController, profileViewModel, firebaseAuth.currentUser)
            }
        }
    }

    private val signInIntent by lazy {
        AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()
    }

    private val locationClient by lazy {
        UserLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

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

        AppModule.initialize(
            imageLauncher,
            signInLauncher,
            signInIntent,
            locationClient
        )

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
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            ),
            0
        )
    }

    private fun initializeRepositories() {
        imageRepository = AppModule.imageRepository
        itineraryRepository = AppModule.itineraryRepository
        directionsRepository = AppModule.directionsRepository
        profileRepository = AppModule.profileRepository
        signInRepository = AppModule.signInRepository
    }

    private fun initializeViewModels() {
        bottomNavigationViewModel = AppModule.bottomNavigationViewModel
        mapViewModel = AppModule.mapViewModel
        profileViewModel = AppModule.profileViewModel
    }
}
