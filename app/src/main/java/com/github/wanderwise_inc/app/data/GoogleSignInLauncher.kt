package com.github.wanderwise_inc.app.data

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

class DefaultGoogleSignInLauncher(
    private val signInLauncher: ActivityResultLauncher<Intent>,
    private val signInIntent: Intent
) : GoogleSignInLauncher {
  override fun signIn() {
    signInLauncher.launch(signInIntent)
  }
}
