package com.github.wanderwise_inc.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.wanderwise_inc.app.model.user.User
import com.github.wanderwise_inc.app.ui.theme.WanderWiseTheme
import com.github.wanderwise_inc.app.viewmodel.HomeViewModel
import com.github.wanderwise_inc.app.viewmodel.UserViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val homeViewModel by viewModels<HomeViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
    private lateinit var analytics : FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WanderWiseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(userViewModel = userViewModel)
                    //AddUser(userViewModel = userViewModel)
                }
            }
        }
    }
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
        Greeting("Android")
    }
}

@Composable
fun AddUser(userViewModel: UserViewModel) {
    val coroutineScope = rememberCoroutineScope()
    Button(onClick = {
        coroutineScope.launch {
            val userList = userViewModel.getAllUser()
            if (userList.isEmpty()) {
                Log.d("USERS", "USER NOT FOUND")
            } else {
                for(user in userList) {
                    Log.d("USERS", "USERNAME IS " + user.username)
                    Log.d("USERS", "USER ID IS " + user.userid)
                    Log.d("USERS", "USER AGE IS " + user.age)
                }
            }
        }
    }) {
        Text(text = "CLICK ME")
    }

}