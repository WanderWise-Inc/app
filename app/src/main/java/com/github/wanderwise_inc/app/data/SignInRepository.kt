package com.github.wanderwise_inc.app.data

import androidx.navigation.NavController
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseUser

interface SignInRepository {
  suspend fun signIn(
      navController: NavController,
      profileViewModel: ProfileViewModel,
      user: FirebaseUser?,
  )
}
