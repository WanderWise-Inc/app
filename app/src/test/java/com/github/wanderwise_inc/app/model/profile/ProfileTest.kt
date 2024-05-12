package com.github.wanderwise_inc.app.model.profile

import org.junit.Test

class ProfileTest {

  @Test
  fun profile_to_map() {
    val p =
        Profile(
            uid = "0",
            displayName = "TestUser",
            userUid = "1",
            bio = "thisIsTheBioOfTestUser",
            profilePicture = null,
            likedItinerariesUid = mutableListOf("0", "1"))
    val uid = "uid"
    val user_uid = "userUid"
    val bio = "bio"
    val displayName = "displayName"
    val profilePicture = "profilePicture"
    val likedItineraries = "likedItinerariesUid"

    val map =
        mapOf(
            uid to p.uid,
            user_uid to p.userUid,
            bio to p.bio,
            displayName to p.displayName,
            profilePicture to p.profilePicture,
            likedItineraries to p.likedItinerariesUid)

    val profileMap = p.toMap()

    assert(map.size == profileMap.size)
    assert(map[uid] == profileMap[uid])
    assert(map[user_uid] == profileMap[user_uid])
    assert(map[bio] == profileMap[bio])
    assert(map[displayName] == profileMap[displayName])
    assert(map[profilePicture] == profileMap[profilePicture])
    assert(map[likedItineraries] == profileMap[likedItineraries])
  }
}
