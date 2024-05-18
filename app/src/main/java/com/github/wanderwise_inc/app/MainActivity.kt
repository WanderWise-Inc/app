package com.github.wanderwise_inc.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.data.LocationsRepository
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.di.AppModule
import com.github.wanderwise_inc.app.ui.navigation.graph.RootNavigationGraph
import com.github.wanderwise_inc.app.ui.theme.WanderWiseTheme
import com.github.wanderwise_inc.app.viewmodel.BottomNavigationViewModel
import com.github.wanderwise_inc.app.viewmodel.CreateItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.LoginViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class MainActivity : ComponentActivity() {

    private val appModule by lazy { AppModule(this) }

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage

    private lateinit var imageRepository: ImageRepository
    private lateinit var itineraryRepository: ItineraryRepository
    private lateinit var profileRepository: ProfileRepository
    private lateinit var directionsRepository: DirectionsRepository
    private lateinit var locationsRepository: LocationsRepository

    private lateinit var bottomNavigationViewModel: BottomNavigationViewModel
    private lateinit var createItineraryViewModel: CreateItineraryViewModel
    private lateinit var itineraryViewModel: ItineraryViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var profileViewModel: ProfileViewModel

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
                        profileViewModel,
                        itineraryViewModel,
                        createItineraryViewModel,
                        bottomNavigationViewModel,
                        loginViewModel,
                        imageRepository,
                        navController
                    )
                }
            }
        }
    }

    private fun init() {
        requestPermissions()

        firebaseAuth = appModule.firebaseAuth
        firebaseStorage = appModule.firebaseStorage

        initializeRepositories()

        initializeViewModels()
    }

    private fun requestPermissions() {
      val permissions = arrayOf(
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION,
      )

      val notGrantedPermissions = permissions.filter {
          ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
      }

      if (notGrantedPermissions.isNotEmpty()) {
          ActivityCompat.requestPermissions(
            this,
            notGrantedPermissions.
            toTypedArray(),
            0
          )
      }
    }

    private fun initializeRepositories() {
        directionsRepository = appModule.directionsRepository
        imageRepository = appModule.imageRepository
        itineraryRepository = appModule.itineraryRepository
        locationsRepository = appModule.locationsRepository
        profileRepository = appModule.profileRepository
    }

    private fun initializeViewModels() {
        bottomNavigationViewModel = appModule.bottomNavigationViewModel
        createItineraryViewModel = appModule.createItineraryViewModel
        itineraryViewModel = appModule.itineraryViewModel
        loginViewModel = appModule.loginViewModel
        profileViewModel = appModule.profileViewModel
    }
}
