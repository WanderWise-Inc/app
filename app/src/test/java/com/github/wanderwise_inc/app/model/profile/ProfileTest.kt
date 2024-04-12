package com.github.wanderwise_inc.app.model.profile

import org.junit.Assert.*
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
            profilePicture = null)
    val uid = "uid"
    val user_uid = "user_uid"
    val bio = "bio"

    val map = mapOf(uid to p.uid, user_uid to p.userUid, bio to p.bio)

    val profileMap = p.toMap()

    assert(map.size == profileMap.size)
    assert(map[uid] == profileMap[uid])
    assert(map[user_uid] == profileMap[user_uid])
    assert(map[bio] == profileMap[bio])
  }
}
