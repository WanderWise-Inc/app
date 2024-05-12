package com.github.wanderwise_inc.app.data

import android.util.Log
import com.github.wanderwise_inc.app.model.profile.Profile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class ProfileRepositoryImpl(db: FirebaseFirestore) : ProfileRepository {
  private val usersCollection = db.collection("users")

  override fun getProfile(userUid: String): Flow<Profile?> {
    TODO("Not yet implemented")
  }

  override fun getAllProfiles(): Flow<List<Profile>> {
    TODO("Not yet implemented")
  }

  override fun setProfile(profile: Profile) {
    val userMap = profile.toMap()
    usersCollection
        .document(profile.userUid)
        .set(userMap)
        .addOnSuccessListener { Log.d("ProfileRepositoryImpl", "Successfully set profile") }
        .addOnFailureListener {
          Log.d("ProfileRepositoryImpl", "Failed to set profile")
          throw it
        }
  }

  override fun deleteProfile(profile: Profile) {
    usersCollection
      .document(profile.userUid)
      .delete()
      .addOnSuccessListener {
        Log.d("ProfileRepositoryImpl", "Successfully deleted profile")
      }
      .addOnFailureListener {
        Log.d("ProfileRepositoryImpl", "Failed to delete profile")
        throw it
      }
  }

  override fun addItineraryToLiked(userUid: String, itineraryUid: String) {
    TODO("Not yet implemented")
  }

  override fun removeItineraryFromLiked(userUid: String, itineraryUid: String) {
    TODO("Not yet implemented")
  }

  override fun checkIfItineraryIsLiked(userUid: String, itineraryUid: String): Boolean {
    TODO("Not yet implemented")
  }

  override fun getLikedItineraries(userUid: String): Flow<List<String>> {
    TODO("Not yet implemented")
  }
}
