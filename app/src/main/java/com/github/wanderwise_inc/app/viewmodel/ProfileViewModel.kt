package com.github.wanderwise_inc.app.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ProfileRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.profile.DEFAULT_OFFLINE_PROFILE
import com.github.wanderwise_inc.app.model.profile.Profile
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {
  private var isSignInComplete = false // set on sign-in success
  /** The `Profile` of the currently signed-in user. Set on sign-in */
  private lateinit var activeProfile: Profile

  /** @return flow of a user profile */
  fun getProfile(userUid: String): Flow<Profile?> {
    Log.d("ProfileViewModel", "Fetching profile for user: $userUid")
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

  /**
   * Adds a liked `Itinerary` to the `Profile` with user-uid `userUid` except if this is the default
   * offline profile, in which case this call has no effect
   */
  fun addLikedItinerary(userUid: String, itineraryUid: String) {
    if (userUid == DEFAULT_OFFLINE_PROFILE.userUid) return
    profileRepository.addItineraryToLiked(userUid, itineraryUid)
  }

  /**
   * Removes a liked `Itinerary` from the `Profile` with user-uid `userUid` except if this is the
   * default offline profile, in which case this call has no effect
   */
  fun removeLikedItinerary(userUid: String, itineraryUid: String) {
    if (userUid == DEFAULT_OFFLINE_PROFILE.userUid) return
    profileRepository.removeItineraryFromLiked(userUid, itineraryUid)
  }

  /**
   * @return `true` iff the itinerary with uid `itineraryUid` is liked by the profile with user-uid
   *   `userUid`
   */
  suspend fun checkIfItineraryIsLiked(userUid: String, itineraryUid: String): Boolean {
    if (userUid == DEFAULT_OFFLINE_PROFILE.userUid) return false
    return profileRepository.checkIfItineraryIsLiked(userUid, itineraryUid)
  }

  /** @return `true` iff the itinerary with uid `itineraryUid` is liked by the active profile */
  suspend fun checkIfItineraryIsLikedByActiveProfile(itineraryUid: String): Boolean {
    return if (isSignInComplete)
        profileRepository.checkIfItineraryIsLiked(getUserUid(), itineraryUid)
    else false
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
   * @return the profile of the active user, as initialized at sign-in. If sign-in in was
   *   unsuccessful, this will return `DEFAULT_OFFLINE_PROFILE` as defined in `Profile.kt`
   */
  fun getActiveProfile(): Profile = activeProfile

  /** @return the UID of the signed in profile */
  fun getUserUid(): String = activeProfile.userUid

  /** Updates liked itineraries of active profile locally */
  fun setActiveProfileLikedItineraries(itineraries: List<Itinerary>) {
    if (itineraries.isNotEmpty()) {
      Log.d("ProfileViewModel", "Updating active profile liked: ${itineraries.map { it.uid }}")
      activeProfile.likedItinerariesUid.clear()
      activeProfile.likedItinerariesUid.addAll(itineraries.map { it.uid })
    }
  }

  /** Creates a profile from a Firebase user */
  fun createProfileFromFirebaseUser(user: FirebaseUser): Profile {
    val uid = user.uid
    val displayName = user.displayName ?: ""
    val bio = ""

    return Profile(userUid = uid, displayName = displayName, bio = bio)
  }
}
