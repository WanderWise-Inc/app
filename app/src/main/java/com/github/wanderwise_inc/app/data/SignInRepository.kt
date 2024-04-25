package com.github.wanderwise_inc.app.data

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
      // TODO Handle ERROR
      throw Exception("User is Null")
    } else {
      // If the user is not null, we must check if this user is already in the database,
      // in which case we will not add it again
      // ACTIVITY.RESULT_OK == -1
      if (resultCode == -1) {
        // Get the user from database (function that returns a flow)
        val currentProfile = profileViewModel.getProfile(user.uid).first()

        // User was already present in the database, in which case we only
        // navigate to the Home page
        if (currentProfile != null) {
          Log.d("USERS", "USER ALREADY IN DATABASE")
          navController.navigate(Graph.HOME)
        } else {
          Log.d("USERS", "USER NOT FOUND IN DATABASE, MUST CREATE IT")

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
        Log.d("USERS", "UNSUCCESSFUL SIGN IN")
        throw Exception("User unsuccessful sign in")
        // TODO Handle ERROR
      }
    }
  }
}
