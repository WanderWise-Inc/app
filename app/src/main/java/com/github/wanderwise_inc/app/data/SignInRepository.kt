package com.github.wanderwise_inc.app.data

import androidx.navigation.NavController
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.navigation.graph.Graph
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.first

interface SignInRepository {
    suspend fun signIn(
        navController: NavController,
        profileViewModel: ProfileViewModel,
        user: FirebaseUser?,
    )
}

class SignInRepositoryImpl : SignInRepository {
    override suspend fun signIn(
        navController: NavController,
        profileViewModel: ProfileViewModel,
        user: FirebaseUser?,
    ) {
        if (user == null) {
            throw Exception("User is Null")
        } else {
            // Get the user from database (function that returns a flow)
            val currentProfile = profileViewModel.getProfile(user.uid).first()

            // User was already present in the database, in which case we only
            // navigate to the Home page
            if (currentProfile != null) {
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
                        profilePicture = user.photoUrl
                    )

                // Set the user to the database
                profileViewModel.setProfile(newProfile)
                navController.navigate(Graph.HOME)
            }
        }
    }
}
