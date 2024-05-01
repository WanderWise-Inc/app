package com.github.wanderwise_inc.app.data

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

interface GoogleSignInLauncher {
  fun launchSignIn()
}

class RealGoogleSignInLauncher(
    private val signInLauncher: ActivityResultLauncher<Intent>,
    private val signInIntent: Intent
) : GoogleSignInLauncher {
  override fun launchSignIn() {
    signInLauncher.launch(signInIntent)
  }
}
