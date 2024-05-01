package com.github.wanderwise_inc.app.data

import com.github.wanderwise_inc.app.model.profile.Profile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProfileRepositoryTest {

  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

  private lateinit var profileRepositoryTestImpl: ProfileRepository
  private lateinit var profile0: Profile
  private lateinit var profile1: Profile
  private lateinit var profile2: Profile
  private lateinit var profiles: MutableList<Profile>

  @Before
  fun setup() {
    profileRepositoryTestImpl = ProfileRepositoryTestImpl()
    profile0 = Profile("0", "Profile0", "0", "bio of Profile0", null)
    profile1 = Profile("1", "Profile1", "1", "bio of Profile1", null)
    profile2 = Profile("2", "Profile2", "2", "bio of Profile2", null)
    profiles = mutableListOf(profile0, profile1, profile2)
  }

  @Test
  fun `getProfile should return null if no user in the list`() = runTest {
    val p = profileRepositoryTestImpl.getProfile("0").first()
    assertNull(p)
  }

  @Test
  fun `getProfile should return the correct profile if the profile is in the list`() = runTest {
    profileRepositoryTestImpl.setProfile(profile0)
    profileRepositoryTestImpl.setProfile(profile1)
    profileRepositoryTestImpl.setProfile(profile2)

    for (i in 0..2) {
      val p = profileRepositoryTestImpl.getProfile(i.toString()).first()
      assertEquals(profiles[i], p)
    }
  }

  @Test
  fun `getAll profiles should return an empty list if no profile was added`() = runTest {
    val profilesTest = profileRepositoryTestImpl.getAllProfiles().first()
    assert(profilesTest.isEmpty())
  }

  @Test
  fun `getAll profiles should return the correctList whenever we add a Profile`() = runTest {
    val a = profileRepositoryTestImpl.getAllProfiles().first()
    assert(a.isEmpty())

    profileRepositoryTestImpl.setProfile(profile0)
    profileRepositoryTestImpl.setProfile(profile1)
    profileRepositoryTestImpl.setProfile(profile2)

    val b = profileRepositoryTestImpl.getAllProfiles().first()

    assertEquals(3, b.size)
    for (i in b.indices) {
      assertEquals(profiles[i], b[i])
    }
  }

  @Test
  fun `setProfile should set a profile with uidCtr if the uid is blank`() = runTest {
    val p0 = Profile("", "Profile0", "0", "bio of Profile0", null)
    val p1 = Profile("", "Profile1", "1", "bio of Profile1", null)
    val p2 = Profile("", "Profile2", "2", "bio of Profile2", null)
    profileRepositoryTestImpl.setProfile(p0)
    profileRepositoryTestImpl.setProfile(p1)
    profileRepositoryTestImpl.setProfile(p2)

    val allProfiles = profileRepositoryTestImpl.getAllProfiles().first()

    for (i in 0..2) {
      assertEquals(profiles[i], allProfiles[i])
    }
  }

  @Test
  fun `deleteProfile should correctly delete the profile from the list`() = runTest {
    profileRepositoryTestImpl.setProfile(profile0)
    profileRepositoryTestImpl.setProfile(profile1)
    profileRepositoryTestImpl.setProfile(profile2)

    val a = profileRepositoryTestImpl.getAllProfiles().first()
    assertEquals(3, a.size)

    profileRepositoryTestImpl.deleteProfile(profile0)
    val b = profileRepositoryTestImpl.getAllProfiles().first()
    assertEquals(2, b.size)
    assertFalse(b.contains(profile0))

    profileRepositoryTestImpl.deleteProfile(profile1)
    val c = profileRepositoryTestImpl.getAllProfiles().first()
    assertEquals(1, c.size)
    assertFalse(b.contains(profile0))
    assertFalse(b.contains(profile1))

    profileRepositoryTestImpl.deleteProfile(profile2)
    val d = profileRepositoryTestImpl.getAllProfiles().first()
    assertEquals(0, d.size)
    assertFalse(b.contains(profile0))
    assertFalse(b.contains(profile1))
    assertFalse(b.contains(profile2))
  }

  @Test
  fun `add itinerary liked should correctly add the itinerary to the list`() = runTest {
    val iti = listOf("i0", "i1", "i2")
    profileRepositoryTestImpl.setProfile(profile0)

    for (i in iti.indices) {
      profileRepositoryTestImpl.addItineraryToLiked(profile0.userUid, iti[i])
    }

    val itineraries = profileRepositoryTestImpl.getLikedItineraries(profile0.userUid).first()

    assertEquals(iti.size, itineraries.size)
    for (i in itineraries.indices) {
      assertEquals(iti[i], itineraries[i])
    }
  }

  @Test
  fun `remove itinerary for liked should correctly remove those itineraries`() = runTest {
    val iti = listOf("i0", "i1", "i2")
    profileRepositoryTestImpl.setProfile(profile0)

    for (i in iti.indices) {
      profileRepositoryTestImpl.addItineraryToLiked(profile0.userUid, iti[i])
    }

    val itineraries = profileRepositoryTestImpl.getLikedItineraries(profile0.userUid).first()
    /** Remove the itineraries from the list */
    for (i in itineraries.indices) {
      profileRepositoryTestImpl.removeItineraryFromLiked(profile0.userUid, iti[i])
      assertEquals(2 - i, itineraries.size)
      assertFalse(itineraries.contains(iti[i]))
    }
  }

  @Test
  fun `if an itinerary is liked, it should be added to the list`() = runTest {
    val iti = listOf("i0", "i1", "i2")
    profileRepositoryTestImpl.setProfile(profile0)

    for (i in iti.indices) {
      profileRepositoryTestImpl.addItineraryToLiked(profile0.userUid, iti[i])
      assertTrue(profileRepositoryTestImpl.checkIfItineraryIsLiked(profile0.userUid, iti[i]))
    }
  }

  @Test
  fun `if an itinerary isn't liked, it should not be in the list`() = runTest {
    val notInList = listOf("n0", "n1", "n2")
    profileRepositoryTestImpl.setProfile(profile0)
    for (i in notInList.indices) {
      assertFalse(profileRepositoryTestImpl.checkIfItineraryIsLiked(profile0.userUid, notInList[i]))
    }
  }

  @Test
  fun `getLikedItineraries should return an empty list if no like happened`() = runTest {
    profileRepositoryTestImpl.setProfile(profile0)
    val itineraries = profileRepositoryTestImpl.getLikedItineraries(profile0.userUid).first()
    assertTrue(itineraries.isEmpty())
  }

  @Test
  fun `getLikedItineraries should return the list of liked itineraries`() = runTest {
    profileRepositoryTestImpl.setProfile(profile0)
    val iti = listOf("i0", "i1", "i2")
    for (i in iti.indices) {
      profileRepositoryTestImpl.addItineraryToLiked(profile0.userUid, iti[i])
    }
    val getItineraries = profileRepositoryTestImpl.getLikedItineraries(profile0.userUid).first()
    for (i in 0..2) {
      assertEquals(iti[i], getItineraries[i])
    }
  }
}
