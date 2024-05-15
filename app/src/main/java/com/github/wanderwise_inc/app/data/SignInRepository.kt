package com.github.wanderwise_inc.app.data

import com.github.wanderwise_inc.app.viewmodel.LoginViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseUser

interface SignInRepository {
  suspend fun signIn(
      loginViewModel: LoginViewModel,
      profileViewModel: ProfileViewModel,
      user: FirebaseUser?,
  )
}
