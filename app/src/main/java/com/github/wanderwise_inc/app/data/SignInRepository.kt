package com.github.wanderwise_inc.app.data

import android.app.Activity
import android.util.Log
import androidx.navigation.NavController
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.navigation.graph.Graph
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.first

interface SignInRepository {
  suspend fun signIn(
      result: FirebaseAuthUIAuthenticationResult,
      navController: NavController,
      profileViewModel: ProfileViewModel,
      user: FirebaseUser?,
      resultCode: Int
  )
}

class SignInRepositoryImpl : SignInRepository {
  override suspend fun signIn(
      result: FirebaseAuthUIAuthenticationResult,
      navController: NavController,
      profileViewModel: ProfileViewModel,
      user: FirebaseUser?,
      resultCode: Int
  ) {
    if (user == null) {
      throw Exception("User is Null")
    } else {
      if (resultCode == Activity.RESULT_OK) {
        Log.d("TESTING SIGN IN BUTTON", "RESULT_OK")
        // Get the user from database (function that returns a flow)
        val currentProfile = profileViewModel.getProfile(user.uid).first()
        Log.d("TESTING SIGN IN BUTTON", "GET PROFILE PASSED")

        // User was already present in the database, in which case we only
        // navigate to the Home page
        if (currentProfile != null) {
          navController.navigate(Graph.HOME)
        } else {
          // We define properly the fields, because some of them could be empty
          val username = user.displayName
          val properUsername = username ?: ""
          val uid = user.uid
          val description = ""

          val newProfile =
              Profile(
                  displayName = properUsername,
                  userUid = uid,
                  bio = description,
                  profilePicture = user.photoUrl)

          // We set the user to the database
          profileViewModel.setProfile(newProfile)
          navController.navigate(Graph.HOME)
        }
      } else {
        // unsuccessful sign in
        throw Exception("User unsuccessful sign in")
      }
    }
  }
}
