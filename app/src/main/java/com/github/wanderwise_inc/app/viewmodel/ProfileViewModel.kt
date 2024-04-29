package com.github.wanderwise_inc.app.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.model.profile.Profile
import kotlinx.coroutines.flow.Flow

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {
  /** @return flow of a user profile */
  fun getProfile(userUid: String): Flow<Profile?> {
    Log.d("USER SIGN IN", "CALLING GET PROFILE")
    return profileRepository.getProfile(userUid)
  }

  /** @return all profiles in data source */
  fun getAllProfiles(): Flow<List<Profile>> {
    return profileRepository.getAllProfiles()
  }

  /** Sets a profile in data source */
  suspend fun setProfile(profile: Profile) {
    profileRepository.setProfile(profile)
  }

  /** Deletes a profile from the data source */
  fun deleteProfile(profile: Profile) {
    profileRepository.deleteProfile(profile)
  }

  /** @return the profile picture of a user as a bitmap flow for asynchronous drawing */
  fun getProfilePicture(profile: Profile): Flow<Bitmap?> {
    return imageRepository.fetchImage("profilePicture/${profile.userUid}")
  }

  /** @return the default profile picture asset */
  fun getDefaultProfilePicture(): Flow<Bitmap?> {
    return imageRepository.fetchImage("profilePicture/defaultProfilePicture.jpg")
  }

  /** @brief add an Itinerary to the user's liked itineraries */
  fun addLikedItinerary(userUid: String, itineraryUid: String) {
    profileRepository.addItineraryToLiked(userUid, itineraryUid)
  }

  /** @brief remove an Itinerary to the user's liked itineraries */
  fun removeLikedItinerary(userUid: String, itineraryUid: String) {
    profileRepository.removeItineraryFromLiked(userUid, itineraryUid)
  }

  fun checkIfItineraryIsLiked(userUid: String, itineraryUid: String): Boolean {
    return profileRepository.checkIfItineraryIsLiked(userUid, itineraryUid)
  }

  fun getLikedItineraries(userUid: String): Flow<List<String>> {
    return profileRepository.getLikedItineraries(userUid)
  }
}
