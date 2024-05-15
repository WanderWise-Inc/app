package com.github.wanderwise_inc.app.data

import com.firebase.ui.auth.AuthUI

object SignInProviders {
    val providers by lazy {
        listOf(
            google
        )
    }

    private val google by lazy {
        AuthUI
            .IdpConfig
            .GoogleBuilder()
            .build()
    }
}