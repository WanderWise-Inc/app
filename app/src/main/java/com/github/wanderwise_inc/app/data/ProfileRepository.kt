package com.github.wanderwise_inc.app.data

import android.net.Uri
import android.util.Log
import com.github.wanderwise_inc.app.model.profile.Profile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

const val DB_USERS_PATH = "users"
interface ProfileRepository {
  /**
   * @param userUid the uid of a user
   * @return a flow of a queried profile
   */
  fun getProfile(userUid: String): Flow<Profile?>

  /** @return a flow of all profiles */
  fun getAllProfiles(): Flow<List<Profile>>

  /** @brief sets a user profile */
  suspend fun setProfile(profile: Profile)

  /** @brief deletes a user profile. Use sparingly */
  fun deleteProfile(profile: Profile)
}

/** @brief test implementation of repository simulating a remote data source */
class ProfileRepositoryTestImpl : ProfileRepository {
  private var profiles = mutableListOf<Profile>()
  private var uidCtr = 0 // for getting a new uid

  /** @throws NoSuchElementException if there are no matches */
  override fun getProfile(userUid: String): Flow<Profile?> {
    return flow {
      val filteredProfiles = profiles.filter { it.userUid == userUid }
      if (filteredProfiles.isEmpty()) emit(null) else emit(filteredProfiles.first())
    }
  }

  override fun getAllProfiles(): Flow<List<Profile>> {
    return flow { emit(profiles) }
  }

  override suspend fun setProfile(profile: Profile) {
    profiles.remove(profile) // remove profile if it already contained
    if (profile.uid.isBlank()) {
      profile.uid = uidCtr.toString()
      uidCtr++
    }
    profiles.add(profile)
  }

  override fun deleteProfile(profile: Profile) {
    profiles.remove(profile)
  }
}

/**
 * class implementation of the ProfileRepository
 */
class ProfileRepositoryImpl : ProfileRepository {
  private val db = FirebaseFirestore.getInstance()
  private val usersCollection = db.collection(DB_USERS_PATH)

  /**
   * get profile function. This function gets the user Profile given the userUid
   * @param userUid the uid of a user
   * @return a flow of the Profile model of a user
   */
  override fun getProfile(userUid: String): Flow<Profile?> {
    return flow {
      val document = usersCollection.document(userUid).get().await()
      if (document.exists()) {
        var uid = document.get("uid").toString()
        var userUid = document.get("user_uid").toString()
        var displayName = document.get("display_name").toString()
        var bio = document.get("bio").toString()
        var profilePicture = Uri.parse(document.get("profile_picture").toString())
        emit(Profile(uid, displayName, userUid, bio, profilePicture))
      } else {
        emit(null)
      }
    }
  }

  override fun getAllProfiles(): Flow<List<Profile>> {
    TODO("Not yet implemented")
  }

  /**
   * set profile function. This function adds a user to the database
   * @param profile the profile of a user that should be added to the database
   */
  override suspend fun setProfile(profile: Profile) {
    val profileMap = profile.toMap()
    usersCollection
      .document(profile.userUid)
      .set(profileMap)
      .await()
  }

  override fun deleteProfile(profile: Profile) {
    TODO("Not yet implemented")
  }

}
