package com.github.wanderwise_inc.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.github.wanderwise_inc.app.data.SignInRepository
import com.google.firebase.auth.FirebaseUser

class SignInViewModel(private val signInRepository: SignInRepository) : ViewModel() {
  public suspend fun signIn(
      result: FirebaseAuthUIAuthenticationResult,
      navController: NavController,
      profileViewModel: ProfileViewModel,
      user: FirebaseUser?,
      resultCode: Int
  ) {
    signInRepository.signIn(
        result, navController = navController, profileViewModel, user, resultCode)
  }
}
