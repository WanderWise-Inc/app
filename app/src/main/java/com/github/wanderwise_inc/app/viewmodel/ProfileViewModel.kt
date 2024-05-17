package com.github.wanderwise_inc.app.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.model.profile.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {
  private var isSignInComplete = false // set on sign-in success
  private lateinit var activeProfile: Profile // set on sign-in
  private val _profilePictureUri = MutableStateFlow<Uri?>(null)
  val profilePictureUri: StateFlow<Uri?> get() = _profilePictureUri

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
  fun setProfile(profile: Profile) {
    profileRepository.setProfile(profile)
  }

  /** Deletes a profile from the data source */
  fun deleteProfile(profile: Profile) {
    profileRepository.deleteProfile(profile)
  }

  /** @return the profile picture of a user as a bitmap flow for asynchronous drawing */
  fun getProfilePicture(profile: Profile): Flow<Uri?> {
    return imageRepository.fetchImage("profilePicture/${profile.userUid}")
  }

  /** @return the default profile picture asset */
  fun getDefaultProfilePicture(): Flow<Uri?> {
    return imageRepository.fetchImage("profilePicture/defaultProfilePicture.jpg")
  }

  fun addLikedItinerary(userUid: String, itineraryUid: String) {
    Log.d("ProfileViewModel", "adding liked itinerary")
    profileRepository.addItineraryToLiked(userUid, itineraryUid)
  }

  fun removeLikedItinerary(userUid: String, itineraryUid: String) {
    profileRepository.removeItineraryFromLiked(userUid, itineraryUid)
  }

  suspend fun checkIfItineraryIsLiked(userUid: String, itineraryUid: String): Boolean {
    Log.d("ProfileViewModel", "checking if itinerary $itineraryUid is liked...")
    return profileRepository.checkIfItineraryIsLiked(userUid, itineraryUid)
  }

  /**
   * @return all of the profile's liked itineraries, or an empty list if there is no actively signed
   *   in profile
   */
  fun getLikedItineraries(userUid: String): Flow<List<String>> {
    return profileRepository.getLikedItineraries(userUid)
  }

  /** Sets the active profile. Called on sign-in and only called once */
  fun setActiveProfile(profile: Profile) {
    if (!isSignInComplete) {
      Log.d("ProfileViewModel", "Setting active profile to: $profile")
      activeProfile = profile
      isSignInComplete = true
    }
  }

  /**
   * Returns the profile of the active user, as initialized at sign-in. If sign-in in was
   * unsuccessful, this will return `DEFAULT_OFFLINE_PROFILE` as defined in `Profile.kt`
   */
  fun getActiveProfile(): Profile = activeProfile

  /** Returns the UID of the signed in profile */
  fun getUserUid(): String = activeProfile.userUid
}
