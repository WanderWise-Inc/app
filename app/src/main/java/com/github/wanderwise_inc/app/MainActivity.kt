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
import com.github.wanderwise_inc.app.data.*
import com.github.wanderwise_inc.app.network.ApiServiceFactory
import com.github.wanderwise_inc.app.ui.navigation.graph.RootNavigationGraph
import com.github.wanderwise_inc.app.ui.theme.WanderWiseTheme
import com.github.wanderwise_inc.app.viewmodel.*
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage

    private lateinit var itineraryRepository: ItineraryRepository
    private lateinit var profileRepository: ProfileRepository
    private lateinit var imageRepository: ImageRepository
    private lateinit var directionsRepository: DirectionsRepository
    private lateinit var signInRepository: SignInRepository

    private lateinit var mapViewModel: MapViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var bottomNavigationViewModel: BottomNavigationViewModel

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
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        requestPermissions()
        initializeRepositories()
        setupSignIn()
        initializeViewModels()
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
        val imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == RESULT_OK) {
                res.data?.data?.let {
                    imageRepository.setCurrentFile(it)
                    Log.d("STORE IMAGE", "CURRENT FILE SELECTED")
                }
            }
        }

        itineraryRepository = ItineraryRepositoryTestImpl()
        directionsRepository = DirectionsRepository(ApiServiceFactory.createDirectionsApiService())
        profileRepository = ProfileRepositoryTestImpl()
        imageRepository = ImageRepositoryImpl(imageLauncher, firebaseStorage.reference, null)
        signInRepository = SignInRepositoryImpl()
    }

    private fun setupSignIn() {
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

        val signInIntent = AuthUI
            .getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
                lifecycleScope.launch {
                    val user = FirebaseAuth.getInstance().currentUser
                    signInRepository.signIn(
                        res,
                        navController,
                        profileViewModel,
                        user,
                        res.resultCode
                    )
                }
            }

        googleSignInLauncher = DefaultGoogleSignInLauncher(signInLauncher, signInIntent)
    }

    private fun initializeViewModels() {
        val userLocationClient = UserLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )

        mapViewModel = MapViewModel(itineraryRepository, directionsRepository, userLocationClient)
        profileViewModel = ProfileViewModel(profileRepository, imageRepository)
        bottomNavigationViewModel = BottomNavigationViewModel()
    }
}
