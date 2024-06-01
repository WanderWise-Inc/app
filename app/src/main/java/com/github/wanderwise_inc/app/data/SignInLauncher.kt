package com.github.wanderwise_inc.app.data

/**
 * `SignInLauncher` is an interface that represents the contract for signing in.
 *
 * This interface should be implemented by any class that is responsible for handling the sign-in process.
 */
interface SignInLauncher {
    /**
     * Initiate the sign-in process.
     */
    fun signIn()
}
