package com.github.wanderwise_inc.app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.wanderwise_inc.app.data.SignInLauncher
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.first

/**
 * ViewModel for the login screen.
 *
 * @param signInLauncher The launcher for the sign-in flow.
 * @param isNetworkAvailable Whether the network is available.
 * @constructor Creates a new instance of LoginViewModel.
 */
class LoginViewModel(
    private val signInLauncher: SignInLauncher,
    private val isNetworkAvailable: Boolean,
) : ViewModel() {
  // The state of the sign-in process
  private val _signInState = MutableLiveData(SignInState.NONE)
  val signInState: LiveData<SignInState>
    get() = _signInState

  /** Launches the sign-in process. */
  fun signIn() {
    if (isNetworkAvailable) signInLauncher.signIn()
    else {
      Log.d("LoginViewModel", "Network unavailable")
      _signInState.value = SignInState.OFFLINE
    }
  }

  /**
   * Handles the result of the sign-in process.
   *
   * @param profileViewModel The profile view model.
   * @param user The signed-in user.
   */
  suspend fun handleSignInResult(
      profileViewModel: ProfileViewModel,
      user: FirebaseUser?,
  ) {
    user?.let { signInSucceeded(profileViewModel, it) } ?: signInFailed()
  }

  /** Sets the sign-in state to [SignInState.FAILURE]. */
  private fun signInFailed() {
    _signInState.value = SignInState.FAILURE
  }

  /**
   * Sets the sign-in state to [SignInState.SUCCESS] and updates the active profile.
   *
   * @param profileViewModel The profile view model.
   * @param user The signed-in user.
   */
  private suspend fun signInSucceeded(profileViewModel: ProfileViewModel, user: FirebaseUser) {
    // Get the user from database
    val currentProfile = profileViewModel.getProfile(user.uid).first()

    if (currentProfile != null) {
      profileViewModel.setActiveProfile(currentProfile)
    } else {
      val newProfile = profileViewModel.createProfileFromFirebaseUser(user)

      // Set the user to the database
      profileViewModel.setProfile(newProfile)
      profileViewModel.setActiveProfile(newProfile)
    }

    _signInState.value = SignInState.SUCCESS
  }

  /** Factory for creating a `LoginViewModel`. */
  class Factory(
      private val signInLauncher: SignInLauncher,
      private val isNetworkAvailable: Boolean,
  ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST") return LoginViewModel(signInLauncher, isNetworkAvailable) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}

/** The state of the sign-in process. */
enum class SignInState {
  NONE,
  SUCCESS,
  FAILURE,
  OFFLINE,
}
