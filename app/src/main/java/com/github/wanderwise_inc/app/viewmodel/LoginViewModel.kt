package com.github.wanderwise_inc.app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.wanderwise_inc.app.data.SignInLauncher
import com.github.wanderwise_inc.app.model.profile.DEFAULT_OFFLINE_PROFILE
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.first

class LoginViewModel(
    private val signInLauncher: SignInLauncher,
    private val isNetworkAvailable: Boolean,
) : ViewModel() {
  private val _signInState = MutableLiveData(SignInState.NONE)
  val signInState: LiveData<SignInState>
    get() = _signInState

  fun signIn() {
    if (isNetworkAvailable) signInLauncher.signIn()
    else {
      Log.d("LoginViewModel", "Network unavailable")
      _signInState.value = SignInState.OFFLINE
    }
  }

  suspend fun handleSignInResult(
      profileViewModel: ProfileViewModel,
      user: FirebaseUser?,
  ) {
    user?.let { signInSucceeded(profileViewModel, it) } ?: signInFailed(profileViewModel)
  }

  private fun signInFailed(profileViewModel: ProfileViewModel) {
    profileViewModel.setActiveProfile(DEFAULT_OFFLINE_PROFILE)
    _signInState.value = SignInState.FAILURE
  }

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
}

enum class SignInState {
  NONE,
  SUCCESS,
  FAILURE,
  OFFLINE,
}
