package com.github.wanderwise_inc.app.model.profile

import android.net.Uri

object ProfileLabels {
  const val UID = "uid"
  const val USER_UID = "user_uid"
  const val BIO = "bio"
  const val NAME = "display_name"
  const val PICTURE = "profile_picture"
}

data class Profile(
    var uid: String = "",
    val displayName: String,
    val userUid: String,
    val bio: String,
    val profilePicture: Uri? = null
) {
  /** @return a map of a profile */
  fun toMap(): Map<String, Any?> {
    return mapOf(
        ProfileLabels.UID to uid,
        ProfileLabels.NAME to displayName,
        ProfileLabels.USER_UID to userUid,
        ProfileLabels.BIO to bio,
        ProfileLabels.PICTURE to profilePicture)
  }

  /** @brief no argument constructor for serialization */
  // constructor() : this(displayName = "", userUid = "", bio = "")
}
