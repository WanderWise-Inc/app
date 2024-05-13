package com.github.wanderwise_inc.app.data

import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.profile.Profile
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
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
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProfileRepositoryTest {

  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

  @Mock private lateinit var db: FirebaseFirestore
  @Mock private lateinit var userCollection : CollectionReference
  @Mock private lateinit var documentRef : DocumentReference
  @Mock private lateinit var voidTask : Task<Void>
  @Mock private lateinit var taskDocSnap : Task<DocumentSnapshot>
  @Mock private lateinit var docSnap : DocumentSnapshot
  @Mock private lateinit var taskQuerySnap : Task<QuerySnapshot>
  @Mock private lateinit var querySnap : QuerySnapshot

  private lateinit var profileRepositoryTestImpl: ProfileRepository
  private lateinit var profileRepositoryImpl: ProfileRepository
  private lateinit var profile0: Profile
  private lateinit var profile1: Profile
  private lateinit var profile2: Profile
  private lateinit var profiles: MutableList<Profile>

  @Before
  fun setup() {
    profileRepositoryTestImpl = ProfileRepositoryTestImpl()
    `when`(db.collection(anyString())).thenReturn(userCollection)
    profileRepositoryImpl = ProfileRepositoryImpl(db)
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

  // FIREBASE IMPLEMENTATION

  @Test(expected = Exception::class)
  fun `set user should throw an exception if a failure occurred`() = runTest {
    `when`(userCollection.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.set(any())).thenReturn(voidTask)
    `when`(voidTask.addOnSuccessListener(any())).thenReturn(voidTask)
    `when`(voidTask.addOnFailureListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnFailureListener
      listener.onFailure(Exception("Get bytes return an exception"))
      null
    }
    profileRepositoryImpl.setProfile(profile0)
  }

  @Test
  fun `set user should not throw an exception if it was successful`() = runTest {
    val profileList = mutableListOf<Profile>()
    `when`(userCollection.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.set(any())).thenReturn(voidTask)
    `when`(voidTask.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<Void>
      profileList.add(profile0)
      listener.onSuccess(null)
      voidTask
    }
    `when`(voidTask.addOnFailureListener(any())).thenReturn(voidTask)
    profileRepositoryImpl.setProfile(profile0)
    assertEquals(1, profileList.size)
    assertEquals(profile0, profileList[0])
  }

  @Test
  fun `getProfile should return null if failure happened`() = runTest {
    `when`(userCollection.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(taskDocSnap)
    `when`(taskDocSnap.addOnSuccessListener(any())).thenReturn(taskDocSnap)
    `when`(taskDocSnap.addOnFailureListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnFailureListener
      listener.onFailure(Exception("Get bytes return an exception"))
      taskDocSnap
    }
    val p = profileRepositoryImpl.getProfile("0").first()
    assertNull(p)
  }

  @Test
  fun `getProfile should return null if document doesn't exist`() = runTest {
    `when`(userCollection.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(taskDocSnap)
    `when`(taskDocSnap.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<DocumentSnapshot>
      listener.onSuccess(docSnap)
      taskDocSnap
    }
    `when`(docSnap.exists()).thenReturn(false)
    val p = profileRepositoryImpl.getProfile("0").first()
    assertNull(p)
  }

  @Test
  fun `getProfile should return the correct profile if document exists`() = runTest {
    `when`(userCollection.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(taskDocSnap)
    `when`(taskDocSnap.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<DocumentSnapshot>
      listener.onSuccess(docSnap)
      taskDocSnap
    }
    `when`(docSnap.exists()).thenReturn(true)
    `when`(docSnap.toObject(Profile::class.java)).thenReturn(profile0)
    val p = profileRepositoryImpl.getProfile("0").first()
    assertEquals(profile0, p)
  }

  @Test
  fun `get all profiles should return an empty list if failure occurred`() = runTest {
    `when`(userCollection.get()).thenReturn(taskQuerySnap)
    `when`(taskQuerySnap.addOnSuccessListener(any())).thenReturn(taskQuerySnap)
    `when`(taskQuerySnap.addOnFailureListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnFailureListener
      listener.onFailure(Exception("Get bytes return an exception"))
      taskQuerySnap
    }
    val pro = profileRepositoryImpl.getAllProfiles().first()
    assertTrue(pro.isEmpty())
  }

  @Test
  fun `getAll profiles should return the correct list of profiles`() = runTest {
    `when`(userCollection.get()).thenReturn(taskQuerySnap)
    `when`(taskQuerySnap.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<QuerySnapshot>
      listener.onSuccess(querySnap)
      taskQuerySnap
    }
    val mockDocuments =
      profiles.map { p ->
        val mockDocument = Mockito.mock(DocumentSnapshot::class.java)
        `when`(mockDocument.toObject(Profile::class.java)).thenReturn(p)
        mockDocument
      }
    `when`(querySnap.documents).thenReturn(mockDocuments)
    val pro = profileRepositoryImpl.getAllProfiles().first()
    assertEquals(profiles, pro)
  }

  @Test(expected = Exception::class)
  fun `delete profile should throw an exception if failure occurred`() = runTest {
      `when`(userCollection.document(anyString())).thenReturn(documentRef)
      `when`(documentRef.delete()).thenReturn(voidTask)
      `when`(voidTask.addOnSuccessListener(any())).thenReturn(voidTask)
      `when`(voidTask.addOnFailureListener(any())).thenAnswer {
          val listener = it.arguments[0] as OnFailureListener
          listener.onFailure(Exception("Get bytes return an exception"))
          null
      }
      profileRepositoryImpl.deleteProfile(profile0)
  }

  @Test
  fun `delete profile should correctly delete a profile`() = runTest {
    val profileList = mutableListOf(profile0)
    `when`(userCollection.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.delete()).thenReturn(voidTask)
    `when`(voidTask.addOnSuccessListener(any())).thenAnswer {
        val listener = it.arguments[0] as OnSuccessListener<Void>
        profileList.remove(profile0)
        listener.onSuccess(null)
        voidTask
    }
    `when`(voidTask.addOnFailureListener(any())).thenReturn(voidTask)
    profileRepositoryImpl.deleteProfile(profile0)
    assertTrue(profileList.isEmpty())
  }

  @Test(expected = Exception::class)
  fun `add itinerary liked should throw an exception if failure occurred`() = runTest {
    `when`(userCollection.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.update(anyString(), any())).thenReturn(voidTask)
    `when`(voidTask.addOnSuccessListener(any())).thenReturn(voidTask)
    `when`(voidTask.addOnFailureListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnFailureListener
      listener.onFailure(Exception("Get bytes return an exception"))
      null
    }
    profileRepositoryImpl.addItineraryToLiked(profile0.userUid, "i0")
  }

  @Test
  fun `add itinerary liked should not throw an exception if successful`() = runTest {
    val itineraryList = mutableListOf<String>()
    `when`(userCollection.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.update(anyString(), any())).thenReturn(voidTask)
    `when`(voidTask.addOnSuccessListener(any())).thenAnswer {
        val listener = it.arguments[0] as OnSuccessListener<Void>
        itineraryList.add("tokyo")
        listener.onSuccess(null)
        voidTask
    }
    `when`(voidTask.addOnFailureListener(any())).thenReturn(voidTask)
    profileRepositoryImpl.addItineraryToLiked(profile0.userUid, "tokyo")
    assertEquals(1, itineraryList.size)
    assertEquals("tokyo", itineraryList[0])
  }

  @Test(expected = Exception::class)
  fun `remove itinerary from liked should throw an exception if failure occurred`() = runTest {
    `when`(userCollection.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.update(anyString(), any())).thenReturn(voidTask)
    `when`(voidTask.addOnSuccessListener(any())).thenReturn(voidTask)
    `when`(voidTask.addOnFailureListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnFailureListener
      listener.onFailure(Exception("Get bytes return an exception"))
      null
    }
    profileRepositoryImpl.removeItineraryFromLiked(profile0.userUid, "i0")
  }

  @Test
  fun `remove itinerary from liked should correctly remove the itinerary`() = runTest {
    val itineraryList = mutableListOf("i0")
    `when`(userCollection.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.update(anyString(), any())).thenReturn(voidTask)
    `when`(voidTask.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<Void>
      itineraryList.remove("i0")
      listener.onSuccess(null)
      voidTask
    }
    `when`(voidTask.addOnFailureListener(any())).thenReturn(voidTask)
    profileRepositoryImpl.removeItineraryFromLiked(profile0.userUid, "i0")
    assertTrue(itineraryList.isEmpty())
  }
}
