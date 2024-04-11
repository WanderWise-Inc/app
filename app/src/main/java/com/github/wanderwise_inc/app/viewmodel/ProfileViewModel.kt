package com.github.wanderwise_inc.app.viewmodel

import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.model.profile.Profile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow

class ProfileViewModel(private val profileRepository: ProfileRepository) : ViewModel() {
    /**
     * @return flow of a user profile
     */
    fun getProfile(userUid: String): Flow<Profile> {
        return profileRepository.getProfile(userUid)
    }

    /**
     * @return all profiles in data source
     */
    fun getAllProfiles(): Flow<List<Profile>> {
        return profileRepository.getAllProfiles()
    }

    /**
     * @brief sets a profile
     */
    fun setProfile(profile: Profile) {
        profileRepository.setProfile(profile)
    }

    /**
     * @brief deletes a profile from the data source
     */
    fun deleteProfile(profile: Profile) {
        profileRepository.deleteProfile(profile)
    }

    public fun sendResetPassword() {
        val firebaseAuth = FirebaseAuth.getInstance()
        try {
            firebaseAuth.sendPasswordResetEmail("ismaililekan@gmail.com")
            Log.d("PROFILE", "EMAIL FOR RESET SENT")
        } catch (e: Exception) {
            Log.w("PROFILE", e)
        }
    }

    public fun deleteUser() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser!!
        try {
            user.delete()
            Log.d("PROFILE", "USER DELETED FROM AUTH")
        } catch (e: Exception) {
            Log.w("PROFILE", e)
        }
    }
}