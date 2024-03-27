package com.github.wanderwise_inc.app

import android.app.Activity.RESULT_OK
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
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.ViewModelProvider
import com.github.wanderwise_inc.app.viewmodel.HomeViewModel
import com.github.wanderwise_inc.app.viewmodel.LoginViewModel

@Composable
fun LoginScreen() {
    Box(modifier = Modifier
        .fillMaxSize())
         {
        // IMAGE
        Image(painter = painterResource(id = R.drawable.map_image),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .matchParentSize())
        Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "WanderWise", fontSize = 30.sp, fontWeight = FontWeight.Bold)
            SignInButton()
        }


    }

        // TEXT

        // SPACE
        Spacer(modifier = Modifier.height(100.dp))
        // BUTTON
        SignInButton()
    }


@Composable
fun SignInButton() {
    val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

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
        if (user != null || it.resultCode == RESULT_OK) {
            // successful sign in

            // DO NAVIGATION

            //navController.navigate(route = Screen.Detail.route)

        } else {
            // unsuccessful sign in

        }
    }

    Button(
        onClick = {
            signInLauncher.launch(signInIntent)
        },
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .wrapContentSize(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.Gray),
        colors = ButtonDefaults.buttonColors(Color.White)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.google__g__logo_svg),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text = "Sign in with google",
                modifier = Modifier.padding(6.dp),
                color = Color.Black
            )
        }
    }
}