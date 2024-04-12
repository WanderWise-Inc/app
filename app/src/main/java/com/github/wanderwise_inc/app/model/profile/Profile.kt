package com.github.wanderwise_inc.app.model.profile

import android.net.Uri

object ProfileLabels {
  const val UID = "uid"
  const val USER_UID = "user_uid"
  const val BIO = "bio"
}

data class Profile(
    var uid: String = "",
    var displayName: String,
    val userUid: String,
    val bio: String,
    val profilePicture: Uri? = null
) {
  /** @return a map of a profile */
  fun toMap(): Map<String, Any> {
    return mapOf(
        ProfileLabels.UID to uid, ProfileLabels.USER_UID to userUid, ProfileLabels.BIO to bio)
  }

  /** @brief no argument constructor for serialization */
  constructor() : this(displayName = "", userUid = "", bio = "")
}
