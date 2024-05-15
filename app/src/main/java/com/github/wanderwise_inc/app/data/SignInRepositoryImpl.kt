package com.github.wanderwise_inc.app.data

import androidx.navigation.NavController
import com.github.wanderwise_inc.app.model.profile.DEFAULT_OFFLINE_PROFILE
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.navigation.graph.Graph
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.first

class SignInRepositoryImpl : SignInRepository {
  override suspend fun signIn(
      navController: NavController,
      profileViewModel: ProfileViewModel,
      user: FirebaseUser?,
  ) {
    if (user == null) {
      profileViewModel.setActiveProfile(DEFAULT_OFFLINE_PROFILE)
    } else {
      // Get the user from database
      val currentProfile = profileViewModel.getProfile(user.uid).first()

      // User already present in the database, navigate to the Home page
      if (currentProfile != null) {
          profileViewModel.setActiveProfile(currentProfile)
        navController.navigate(Graph.HOME)
      } else {
        // Define properly the fields, because some of them could be empty
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

        // Set the user to the database
        profileViewModel.setProfile(newProfile)
        profileViewModel.setActiveProfile(newProfile)
      }
    }
    navController.navigate(Graph.HOME)
  }
}
