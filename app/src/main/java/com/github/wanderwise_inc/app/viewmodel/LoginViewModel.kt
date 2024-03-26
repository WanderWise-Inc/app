package com.github.wanderwise_inc.app.viewmodel

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    // Choose authentication providers
    private val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

    // Create and launch sign-in intent
    private val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .build()

    @Composable
    fun SignIn() {
        val signInLauncher = rememberLauncherForActivityResult(
            contract = FirebaseAuthUIActivityResultContract()
        ) {
            val response = it.idpResponse
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null || it.resultCode == RESULT_OK) {
                // successful sign in
                /*            navController.navigate(
                                route = Screen.Detail.passId(user?.uid.toString())
                            )*/
                //navController.navigate(route = Screen.Detail.route)

            } else {
                // unsuccessful sign in

            }
        }
        signInLauncher.launch(signInIntent)
    }
}
