package com.github.wanderwise_inc.app.data

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI

/**
 * `GoogleSignInLauncher` is a class that implements the `SignInLauncher` interface to handle Google
 * sign-in.
 *
 * This class is responsible for initiating the sign-in process using Google as the identity
 * provider.
 *
 * @constructor Creates a `GoogleSignInLauncher` with the specified `activityResultLauncher`.
 * @property activityResultLauncher The `ActivityResultLauncher` that is used to launch the sign-in
 */
class GoogleSignInLauncher(
    private val activityResultLauncher: ActivityResultLauncher<Intent>,
) : SignInLauncher {
  // `providers` is a list of identity providers. In this case, it only contains Google.
  private val providers by lazy { listOf(AuthUI.IdpConfig.GoogleBuilder().build()) }

  // `signInIntent` is an intent that is used to initiate the sign-in process.
  private val signInIntent by lazy {
    AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()
  }

  override fun signIn() {
    activityResultLauncher.launch(signInIntent)
  }
}
