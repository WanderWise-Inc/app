package com.github.wanderwise_inc.app.model.profile

object ProfileLabels {
    const val UID = "uid"
    const val USER_UID = "user_uid"
    const val BIO = "bio"
    const val COUNTRY = "country"
}

data class Profile(
    var uid: String = "",
    val userUid: String,
    val bio: String,
    val country: String,
) {
    /**
     * @return a map of a profile
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            ProfileLabels.UID to uid,
            ProfileLabels.USER_UID to userUid,
            ProfileLabels.BIO to bio,
            ProfileLabels.COUNTRY to country
        )
    }

    /**
     * @brief no argument constructor for serialization
     */
    constructor() : this(userUid="", bio="", country="")
}