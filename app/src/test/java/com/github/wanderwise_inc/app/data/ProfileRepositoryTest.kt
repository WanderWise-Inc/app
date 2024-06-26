package com.github.wanderwise_inc.app.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
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
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProfileRepositoryTest {

  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

  @Mock private lateinit var db: FirebaseFirestore
  @Mock private lateinit var userCollection: CollectionReference
  @Mock private lateinit var documentRef: DocumentReference
  @Mock private lateinit var voidTask: Task<Void>
  @Mock private lateinit var taskDocSnap: Task<DocumentSnapshot>
  @Mock private lateinit var docSnap: DocumentSnapshot
  @Mock private lateinit var taskQuerySnap: Task<QuerySnapshot>
  @Mock private lateinit var querySnap: QuerySnapshot
  @Mock private lateinit var context: Context
  @Mock private lateinit var connectivityManager: ConnectivityManager
  @Mock private lateinit var networkInfo: Network
  @Mock private lateinit var networkCapabilities: NetworkCapabilities

  private lateinit var profileRepositoryImpl: ProfileRepository
  private lateinit var profile0: Profile
  private lateinit var profile1: Profile
  private lateinit var profile2: Profile
  private lateinit var profiles: MutableList<Profile>

  @Before
  fun `setup mocks`() {
    context = mock()
    connectivityManager = mock()
    networkInfo = mock()
    networkCapabilities = mock()

    `when`(db.collection(anyString())).thenReturn(userCollection)
    `when`(context.getSystemService(anyString())).thenReturn(connectivityManager)
    `when`(connectivityManager.activeNetwork).thenReturn(networkInfo)
    `when`(connectivityManager.getNetworkCapabilities(networkInfo)).thenReturn(networkCapabilities)
    `when`(networkCapabilities.hasCapability(anyInt())).thenReturn(true)
  }

  @Before
  fun `setup repositories and data`() {
    profileRepositoryImpl = ProfileRepositoryImpl(db, context)
    profile0 = Profile("0", "Profile0", "bio of Profile0")
    profile1 = Profile("1", "Profile1", "bio of Profile1")
    profile2 = Profile("2", "Profile2", "bio of Profile2")
    profiles = mutableListOf(profile0, profile1, profile2)
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

  @Test(expected = Exception::class)
  fun `check if itinerary is liked should throw an exception if failure occurred`() = runTest {
    `when`(userCollection.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(taskDocSnap)
    `when`(taskDocSnap.addOnSuccessListener(any())).thenReturn(taskDocSnap)
    `when`(taskDocSnap.addOnFailureListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnFailureListener
      listener.onFailure(Exception("Get bytes return an exception"))
      taskDocSnap
    }
    profileRepositoryImpl.checkIfItineraryIsLiked(profile0.userUid, "i0")
  }

  @Test
  fun `check if itinerary is liked should return true if itinerary is liked`() = runTest {
    `when`(userCollection.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(taskDocSnap)
    `when`(taskDocSnap.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<DocumentSnapshot>
      listener.onSuccess(docSnap)
      taskDocSnap
    }
    `when`(docSnap.get(anyString())).thenReturn(listOf("i0"))
    val isLiked = profileRepositoryImpl.checkIfItineraryIsLiked(profile0.userUid, "i0")
    assertTrue(isLiked)
    val shouldNotBeLiked = profileRepositoryImpl.checkIfItineraryIsLiked(profile0.userUid, "i1")
    assertFalse(shouldNotBeLiked)
  }

  @Test
  fun `getLiked itineraries should return an empty list if an error occurred`() = runTest {
    `when`(userCollection.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(taskDocSnap)
    `when`(taskDocSnap.addOnSuccessListener(any())).thenReturn(taskDocSnap)
    `when`(taskDocSnap.addOnFailureListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnFailureListener
      listener.onFailure(Exception("Get bytes return an exception"))
      taskDocSnap
    }
    val itineraries = profileRepositoryImpl.getLikedItineraries(profile0.userUid).first()
    assertTrue(itineraries.isEmpty())
  }

  @Test
  fun `getLiked itineraries should return the list of liked itineraries`() = runTest {
    `when`(userCollection.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(taskDocSnap)
    `when`(taskDocSnap.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<DocumentSnapshot>
      listener.onSuccess(docSnap)
      taskDocSnap
    }
    `when`(docSnap.get(anyString())).thenReturn(listOf("i0", "i1", "i2"))
    val itineraries = profileRepositoryImpl.getLikedItineraries(profile0.userUid).first()
    assertEquals(3, itineraries.size)
    for (i in 0..2) {
      assertEquals("i$i", itineraries[i])
    }
  }
}
