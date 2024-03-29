package com.github.wanderwise_inc.app

import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.em
import androidx.lifecycle.ViewModelProvider
import com.github.wanderwise_inc.app.model.user.User
import com.github.wanderwise_inc.app.viewmodel.HomeViewModel
import com.github.wanderwise_inc.app.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(userViewModel : UserViewModel, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .requiredWidth(width = 1280.dp)
            .requiredHeight(height = 1100.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.underground_2725336_1280),
            contentDescription = "underground-2725336_1280 1",
            modifier = Modifier
                .requiredWidth(width = 1280.dp)
                .requiredHeight(height = 853.dp)
                .alpha(0.6f))
        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(
                    x = 485.dp,
                    y = 829.dp
                )
                .requiredWidth(width = 289.dp)
                .requiredHeight(height = 39.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(color = Color(0xFF972626))){ SignInButton(userViewModel)}
        Image(
            painter = painterResource(id = R.drawable.google__g__logo_svg),
            contentDescription = "google-logo-9808 1",
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(
                    x = 505.dp,
                    y = 841.dp
                )
                .requiredSize(size = 15.dp))
        Text(
            text = "Sign-In with Google",
            color = Color.White,
            lineHeight = 1.em,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium),
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .offset(
                    x = 40.dp,
                    y = 837.dp
                )
                .requiredWidth(width = 250.dp)
                .requiredHeight(height = 35.dp))
        Text(
            text = "WanderWise",
            color = Color.White,
            lineHeight = 0.56.em,
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium),
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(
                    x = 550.dp,
                    y = 730.dp
                ))
    }
}


@Composable
fun SignInButton(userViewModel: UserViewModel) {
    // Added a coroutine because userViewModel functions are async
    val coroutineScope = rememberCoroutineScope()
    val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

    // Create and launch sign-in intent
    val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .build()

    // creating the launcher that will be used to signIn
    val signInLauncher = rememberLauncherForActivityResult(
        contract = FirebaseAuthUIActivityResultContract()
    ) {
        val response = it.idpResponse
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.d("USERS", "USER IS NULL")

            // TODO Handle ERROR
            
        } else {
            // If the user is not null, we must check if this user is already in the database,
            // in which case we will not add it again
            coroutineScope.launch {
                // Check if the ResultCode is OK
                if (it.resultCode == RESULT_OK) {
                    // Get the user from database (async function)
                    val currentUser = userViewModel.getUser(user.uid)
                    if (currentUser != null) {
                        Log.d("USERS", "USER ALREADY IN DATABASE")

                        // TODO Navigation

                    } else {
                        Log.d("USERS", "USER NOT FOUND IN DATABASE, MUST CREATE IT")

                        // We define properly the fields, because some of them could be empty
                        val username = user.displayName
                        val properUsername = username ?: ""
                        val email = user.email
                        val properEmail = email ?: ""
                        val uid = user.uid
                        val phoneNumber = user.phoneNumber
                        val properPhoneNumber = phoneNumber ?: ""

                        val u = User(uid, properUsername, properEmail, properPhoneNumber)

                        // Trying to set the user
                        coroutineScope.launch {
                            val succ = userViewModel.setUser(u)
                            if (succ) {
                                Log.d("USERS", "USER ADDED TO DB")

                                // TODO Navigation

                            } else {
                                Log.d("USERS", "USER NOT ADDED TO DB")

                                // TODO Handle ERROR
                            }
                        }

                    }
                } else {
                    // unsuccessful sign in
                    Log.d("USERS", "UNSUCCESSFUL SIGN IN")

                    // TODO Handle ERROR
                }
            }
        }

    }

    Button(
        onClick = {
            signInLauncher.launch(signInIntent)
        },
        modifier = Modifier
            .requiredWidth(width = 289.dp)
            .requiredHeight(height = 39.dp)
            ,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Gray),
        colors = ButtonDefaults.buttonColors(Color.Transparent)
    ) {
    }
}

