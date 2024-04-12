package com.github.wanderwise_inc.app


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.github.wanderwise_inc.app.ui.navigation.graph.RootNavigationGraph
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ImageRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.ui.map.MapScreen
import com.github.wanderwise_inc.app.ui.theme.WanderWiseTheme
import com.github.wanderwise_inc.app.viewmodel.HomeViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    private val homeViewModel by viewModels<HomeViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
    private val profileViewModel by viewModels<ProfileViewModel>()
    private val itineraryRepository = ItineraryRepositoryTestImpl()
    private val mapViewModel = MapViewModel(itineraryRepository)


    // private lateinit var analytics : FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageRepository = ImageRepositoryTestImpl(application)

        setContent {
            WanderWiseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // HomeScreen(homeViewModel, mapViewModel)
                    RootNavigationGraph(
                        application.applicationContext,
                        homeViewModel = homeViewModel,
                        userViewModel = userViewModel,
                        mapViewModel = mapViewModel,
                        navController = rememberNavController()
                    )
                }
            }
        }
    }
}

@Composable
fun ImgTest(imageRepository: ImageRepository) {
    val imageFlow = imageRepository.fetchImage(null)
    val bitmap by imageFlow.collectAsState(initial = null)
    if (bitmap != null)
        Image(painter = BitmapPainter(bitmap!!.asImageBitmap()), contentDescription = "testImage")
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WanderWiseTheme {
        MapScreen()
    }
}

/*@Composable
fun AddUser(userViewModel: UserViewModel) {
    val coroutineScope = rememberCoroutineScope()
    Button(onClick = {
        coroutineScope.launch {
            val id = "xasifnoiqnfi12adnadeud"
            val user = User(id, "Jean", "jean@gmail.com", "")
            if (userViewModel.getUser(id) == null) {
                userViewModel.setUser(user)
                Log.d("USERS", "USER ADDED TO DB")
            } else {
                userViewModel.updateUserName(user, "Theo")
                userViewModel.updateEmail(user, "theo@gmail.com")
                userViewModel.updatePhoneNumber(user, "0779345362")
            }

            val currentUser = userViewModel.getUser(id)
            if (currentUser!!.username == "Theo") {
                Log.d("USERS", "FOUND THEO")
                userViewModel.deleteUser(currentUser)
                Log.d("USERS", "DELETED THEO")
            } else {
                Log.d("USERS", "NOT FOUND THEO")
            }
        }
    }) {
        Text(text = "CLICK ME")
    }

}*/

/*@Composable
fun TestSignIn(userViewModel: UserViewModel) {
    val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
    val coroutineScope = rememberCoroutineScope()
    // Create and launch sign-in intent
    val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .build()

    val signInLauncher = rememberLauncherForActivityResult(
        contract = FirebaseAuthUIActivityResultContract()
    ) {
        val response = it.idpResponse
        val user = FirebaseAuth.getInstance().currentUser
        Log.d("USERS", "ABOUT TO SIGN IN")
        if (user == null) {
            Log.d("USERS", "USER IS NULL")
        }
        Log.d("USERS", "RESULT_CODE IS " + it.resultCode)
        if (it.resultCode == Activity.RESULT_OK || user != null) {
            // successful sign in

            // DO NAVIGATION
            Log.d("USERS", "USERS IS SIGNING IN")
            //navController.navigate(route = Screen.Detail.route)
            val username = user?.displayName
            val email = user?.email
            val uid = user?.uid
            val phoneNumber = user?.phoneNumber

            val u = User(uid!!, username!!, email!!, phoneNumber!!)

            coroutineScope.launch {
                userViewModel.setUser(u)
            }

        } else {
            // unsuccessful sign in
            Log.d("USERS", "UNSUCCESSFUL SIGN IN")
        }
    }


    Button(onClick = { signInLauncher.launch(signInIntent)}) {
        Text(text = "SIGN IN")
    }
}*/

@Composable
fun TestProfile(profileViewModel : ProfileViewModel) {

}