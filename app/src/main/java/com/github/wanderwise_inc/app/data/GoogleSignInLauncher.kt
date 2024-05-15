package com.github.wanderwise_inc.app.data

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI

class GoogleSignInLauncher(
    private val activityResultLauncher: ActivityResultLauncher<Intent>,
    private val providers: List<AuthUI.IdpConfig> = SignInProviders.providers
) : SignInLauncher {
    private val signInIntent by lazy {
        AuthUI
            .getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
    }

    override fun signIn() {
        activityResultLauncher.launch(signInIntent)
    }
}
