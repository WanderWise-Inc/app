package com.github.wanderwise_inc.app.ui.signin

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.data.GoogleSignInLauncher
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel

@Composable
fun LoginScreen(
    googleSignInLauncher: GoogleSignInLauncher,
    profileViewModel: ProfileViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
  Box(modifier = modifier.requiredWidth(width = 1280.dp).requiredHeight(height = 1100.dp)) {
    Image(
        painter = painterResource(id = R.drawable.underground_2725336_1280),
        contentDescription = "underground-2725336_1280 1",
        modifier =
            Modifier.requiredWidth(width = 1280.dp).requiredHeight(height = 853.dp).alpha(0.6f))
    Box(
        modifier =
            Modifier.align(alignment = Alignment.TopStart)
                .offset(x = 485.dp, y = 829.dp)
                .requiredWidth(width = 289.dp)
                .requiredHeight(height = 39.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(color = Color(0xFF972626))) {
          SignInButton(googleSignInLauncher, profileViewModel, navController)
        }
    Image(
        painter = painterResource(id = R.drawable.google__g__logo_svg),
        contentDescription = "google-logo-9808 1",
        modifier =
            Modifier.align(alignment = Alignment.TopStart)
                .offset(x = 505.dp, y = 841.dp)
                .requiredSize(size = 15.dp))
    Text(
        text = "Sign-In with Google",
        color = Color.White,
        lineHeight = 1.em,
        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
        modifier =
            Modifier.align(alignment = Alignment.TopCenter)
                .offset(x = 40.dp, y = 837.dp)
                .requiredWidth(width = 250.dp)
                .requiredHeight(height = 35.dp))
    Text(
        text = "WanderWise",
        color = Color.White,
        lineHeight = 0.56.em,
        style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Medium),
        modifier = Modifier.align(alignment = Alignment.TopStart).offset(x = 550.dp, y = 730.dp))
  }
}

@Composable
fun SignInButton(
    googleSignInLauncher: GoogleSignInLauncher,
    profileViewModel: ProfileViewModel,
    navController: NavHostController,
) {
  Log.d("TESTING SIGN IN BUTTON", "IN SIGN IN BUTTON")
  // Added a coroutine because userViewModel functions are async
  /*  val signInRepositoryImpl = SignInRepositoryImpl()
  val coroutineScope = rememberCoroutineScope()
  val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
  // Create and launch sign-in intent
  val signInIntent =
      AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()
  // creating the launcher that will be used to signIn
  Log.d("TESTING SIGN IN BUTTON", "BEFORE LAUNCHER")
  val signInLauncher =
      rememberLauncherForActivityResult(contract = FirebaseAuthUIActivityResultContract()) {
        coroutineScope.launch {
          Log.d("TESTING SIGN IN BUTTON", "IN SIGN IN LAUNCHER")
          val user = FirebaseAuth.getInstance().currentUser
          signInRepositoryImpl.signIn(it, navController, profileViewModel, user, it.resultCode)
        }
      }*/

  Button(
      onClick = { googleSignInLauncher.launchSignIn() },
      modifier =
          Modifier.requiredWidth(width = 289.dp)
              .requiredHeight(height = 39.dp)
              .testTag("Sign in button"),
      shape = RoundedCornerShape(8.dp),
      border = BorderStroke(1.dp, Color.Gray),
      colors = ButtonDefaults.buttonColors(Color.Transparent)) {}
}
