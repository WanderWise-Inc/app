package com.github.wanderwise_inc.app.model.profile

object ProfileLabels {
  const val USER_UID = "userUid"
  const val NAME = "displayName"
  const val BIO = "bio"
  const val LIKED_ITINERARIES = "likedItinerariesUid"
}

data class Profile(
    var userUid: String,
    val displayName: String,
    val bio: String,
    val likedItinerariesUid: MutableList<String> = mutableListOf()
) {
  /** @return a map of a profile */
  fun toMap(): Map<String, Any?> {
    return mapOf(
        ProfileLabels.USER_UID to userUid,
        ProfileLabels.NAME to displayName,
        ProfileLabels.BIO to bio,
        ProfileLabels.LIKED_ITINERARIES to likedItinerariesUid)
  }

  /** @brief no argument constructor for serialization */
  constructor() : this(displayName = "", userUid = "", bio = "")

  /** @brief constructor with userUid for testing */
  constructor(
      userUid: String,
  ) : this(displayName = "", userUid = userUid, bio = "")
}

/** set in `ProfileViewModel` on sign-in failure and when no data is cached */
val DEFAULT_OFFLINE_PROFILE =
    Profile(displayName = "Offline Wanderer", userUid = "-1", bio = "Wandering without connection")
