package com.github.wanderwise_inc.app.model.profile

import android.net.Uri

object ProfileLabels {
  const val UID = "uid"
  const val USER_UID = "userUid"
  const val BIO = "bio"
  const val NAME = "displayName"
  const val PICTURE = "profilePicture"
  const val LIKED_ITINERARIES = "likedItinerariesUid"
}

data class Profile(
    var uid: String = "",
    val displayName: String,
    val userUid: String,
    val bio: String,
    val profilePicture: Uri? = null,
    val likedItinerariesUid: MutableList<String> = mutableListOf()
) {
  /** @return a map of a profile */
  fun toMap(): Map<String, Any?> {
    return mapOf(
        ProfileLabels.UID to uid,
        ProfileLabels.NAME to displayName,
        ProfileLabels.USER_UID to userUid,
        ProfileLabels.BIO to bio,
        ProfileLabels.PICTURE to profilePicture,
        ProfileLabels.LIKED_ITINERARIES to likedItinerariesUid)
  }

  /** @brief no argument constructor for serialization */
  constructor() : this(displayName = "", userUid = "", bio = "")

  /** @brief constructor with userUid for testing */
  constructor(
      userUid: String,
  ) : this(displayName = "", userUid = userUid, bio = "")
}
