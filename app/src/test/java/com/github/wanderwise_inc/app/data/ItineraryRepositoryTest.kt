package com.github.wanderwise_inc.app.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.datastore.core.DataStore
import com.github.wanderwise_inc.app.model.location.FakeItinerary
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryTags
import com.github.wanderwise_inc.app.proto.location.SavedItineraries
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ItineraryRepositoryTest {
  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()

  @Mock private lateinit var db: FirebaseFirestore
  @Mock private lateinit var context: Context
  @Mock private lateinit var savedItinerariesDataStore: DataStore<SavedItineraries>
  @Mock private lateinit var itineraryColl: CollectionReference
  @Mock private lateinit var queryTask: Task<QuerySnapshot>
  @Mock private lateinit var query: QuerySnapshot
  @Mock private lateinit var documentRef: DocumentReference
  @Mock private lateinit var documentSnapshot: DocumentSnapshot
  @Mock private lateinit var docTask: Task<DocumentSnapshot>
  @Mock private lateinit var voidTask: Task<Void>

  private val itineraryObject = FakeItinerary
  private lateinit var itineraryRepositoryTest: ItineraryRepository
  private lateinit var itineraryRepository: ItineraryRepository
  private val locationsList =
      listOf(itineraryObject.SAN_FRANCISCO, itineraryObject.SWITZERLAND, itineraryObject.TOKYO)

  @Before
  fun setup() {
    val connectivityManager = mock(ConnectivityManager::class.java)
    val networkInfo = mock(Network::class.java)
    val networkCapabilities = mock(NetworkCapabilities::class.java)
    `when`(connectivityManager.activeNetwork).thenReturn(networkInfo)
    `when`(connectivityManager.getNetworkCapabilities(any())).thenReturn(networkCapabilities)
    `when`(networkCapabilities.hasCapability(anyInt())).thenReturn(true)

    itineraryRepositoryTest = ItineraryRepositoryTestImpl()
    `when`(db.collection(anyString())).thenReturn(itineraryColl)
    `when`(context.getSystemService(any())).thenReturn(connectivityManager)
    itineraryRepository = ItineraryRepositoryImpl(db, context, savedItinerariesDataStore)
  }

  @Test
  fun `getPublicItineraries should return an empty list if no itineraries were added`() = runTest {
    val getPublicList = itineraryRepositoryTest.getPublicItineraries().first()
    assertTrue(getPublicList.isEmpty())
  }

  @Test
  fun `getPublicItineraries should correctly return public itineraries`() = runTest {
    for (i in locationsList.indices) {
      itineraryRepositoryTest.setItinerary(locationsList[i])
    }

    val getPublicList = itineraryRepositoryTest.getPublicItineraries().first()
    assertEquals(locationsList.size, getPublicList.size)

    for (i in getPublicList.indices) {
      assertEquals(locationsList[i], getPublicList[i])
    }
  }

  @Test
  fun `getUserItineraries should correctly returns a list of user itineraries`() = runTest {
    for (i in locationsList.indices) {
      itineraryRepositoryTest.setItinerary(locationsList[i])
    }

    val user0List = itineraryRepositoryTest.getUserItineraries("Sophia Reynolds").first()
    val user1List = itineraryRepositoryTest.getUserItineraries("Elena Cruz").first()
    val user2List = itineraryRepositoryTest.getUserItineraries("Liam Bennett").first()

    assertTrue(user0List.size == 1)
    assertTrue(user1List.size == 1)
    assertTrue(user2List.size == 1)

    assertEquals(itineraryObject.TOKYO, user0List[0])
    assertEquals(itineraryObject.SAN_FRANCISCO, user1List[0])
    assertEquals(itineraryObject.SWITZERLAND, user2List[0])
  }

  @Test
  fun `getItinerariesWithTags should return correct list of itineraries with following tags`() =
      runTest {
        for (i in locationsList.indices) {
          itineraryRepositoryTest.setItinerary(locationsList[i])
        }
        val tags0 = listOf(ItineraryTags.URBAN)
        val tags1 = listOf(ItineraryTags.ACTIVE)
        val tags2 = listOf(ItineraryTags.ADVENTURE, ItineraryTags.URBAN)
        val list0 = itineraryRepositoryTest.getItinerariesWithTags(tags0).first()
        val list1 = itineraryRepositoryTest.getItinerariesWithTags(tags1).first()
        val list2 = itineraryRepositoryTest.getItinerariesWithTags(tags2).first()

        assertTrue(list0.size == 2)
        assertTrue(list1.size == 1)
        assertTrue(list2.size == 3)

        assertTrue(list0.containsAll(listOf(itineraryObject.TOKYO, itineraryObject.SAN_FRANCISCO)))
        assertTrue(list1.contains(itineraryObject.SAN_FRANCISCO))
        assertTrue(list2.containsAll(locationsList))
      }

  @Test
  fun `getItinerary should return the itinerary with the given uid`() = runTest {
    for (i in locationsList.indices) {
      itineraryRepositoryTest.setItinerary(locationsList[i])
    }

    for (i in 0..2) {
      assertEquals(locationsList[i], itineraryRepositoryTest.getItinerary(i.toString()))
    }
  }

  @Test
  fun `setItinerary should correctly add a new itinerary to the list`() = runTest {
    val a = itineraryRepositoryTest.getPublicItineraries().first()
    assertTrue(a.isEmpty())
    for (i in locationsList.indices) {
      itineraryRepositoryTest.setItinerary(locationsList[i])
      val currList = itineraryRepositoryTest.getPublicItineraries().first()
      assertEquals(i + 1, currList.size)
      assertTrue(currList.contains(locationsList[i]))
    }
  }

  @Test
  fun `update Itinerary should correctly update the itinerary in the list`() = runTest {
    for (i in 0..1) {
      itineraryRepositoryTest.setItinerary(locationsList[i])
    }
    val getList = itineraryRepositoryTest.getPublicItineraries().first()
    assertTrue(getList.size == 2)
    assertTrue(
        getList.containsAll(listOf(itineraryObject.SWITZERLAND, itineraryObject.SAN_FRANCISCO)))

    itineraryRepositoryTest.updateItinerary(getList[0].uid, itineraryObject.SAN_FRANCISCO)
    val updateList = itineraryRepositoryTest.getPublicItineraries().first()
    assertTrue(getList.containsAll(updateList))
  }

  @Test
  fun `delete itinerary should correctly delete the itinerary in the list`() = runTest {
    for (i in 0..2) {
      itineraryRepositoryTest.setItinerary(locationsList[i])
    }

    val a = itineraryRepositoryTest.getPublicItineraries().first()
    assertTrue(a.size == 3)

    for (i in 2 downTo 0) {
      itineraryRepositoryTest.deleteItinerary(locationsList[i])
      val currList = itineraryRepositoryTest.getPublicItineraries().first()
      assertTrue(currList.size == i)
      assertFalse(currList.contains(locationsList[i]))
    }
  }

  // FIREBASE IMPLEMENTATION TEST

  @Test
  fun `getPublicItineraries should return an empty list if no itineraries were added (FIREBASE)`() =
      runTest {
        `when`(itineraryColl.whereEqualTo(anyString(), any())).thenReturn(itineraryColl)
        `when`(itineraryColl.get()).thenReturn(queryTask)
        `when`(queryTask.addOnSuccessListener(any())).thenReturn(queryTask)
        `when`(queryTask.addOnFailureListener(any())).thenAnswer {
          val listener = it.arguments[0] as OnFailureListener
          listener.onFailure(Exception("Get bytes return an exception"))
          null
        }
        val publicItineraries = itineraryRepository.getPublicItineraries().first()
        assertTrue(publicItineraries.isEmpty())
      }

  @Test
  fun `getPublic itineraries should return the correct list if itineraries were added (FIREBASE)`() =
      runTest {
        `when`(itineraryColl.whereEqualTo(anyString(), any())).thenReturn(itineraryColl)
        `when`(itineraryColl.get()).thenReturn(queryTask)
        `when`(queryTask.addOnSuccessListener(any())).thenAnswer {
          val listener = it.arguments[0] as OnSuccessListener<QuerySnapshot>
          listener.onSuccess(query)
          queryTask
        }
        val mockDocuments =
            locationsList.map { itinerary ->
              val mockDocument = mock(DocumentSnapshot::class.java)
              `when`(mockDocument.toObject(Itinerary::class.java)).thenReturn(itinerary)
              mockDocument
            }
        `when`(query.documents).thenReturn(mockDocuments)
        val publicItineraries = itineraryRepository.getPublicItineraries().first()
        assertEquals(locationsList, publicItineraries)
      }

  @Test
  fun `getUser itineraries should return an empty list if nothing was found (FIREBASE)`() =
      runTest {
        `when`(itineraryColl.whereEqualTo(anyString(), any())).thenReturn(itineraryColl)
        `when`(itineraryColl.get()).thenReturn(queryTask)
        `when`(queryTask.addOnSuccessListener(any())).thenReturn(queryTask)
        `when`(queryTask.addOnFailureListener(any())).thenAnswer {
          val listener = it.arguments[0] as OnFailureListener
          listener.onFailure(Exception("Get bytes return an exception"))
          null
        }
        val userItineraries = itineraryRepository.getUserItineraries("testUser").first()
        assertTrue(userItineraries.isEmpty())
      }

  @Test
  fun `get user itineraries should return the list of the user itineraries (FIREBASE)`() = runTest {
    `when`(itineraryColl.whereEqualTo(anyString(), any())).thenReturn(itineraryColl)
    `when`(itineraryColl.get()).thenReturn(queryTask)
    `when`(queryTask.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<QuerySnapshot>
      listener.onSuccess(query)
      queryTask
    }

    val userList = listOf(itineraryObject.SAN_FRANCISCO)
    val mockDocuments =
        userList.map { itinerary ->
          val mockDocument = mock(DocumentSnapshot::class.java)
          `when`(mockDocument.toObject(Itinerary::class.java)).thenReturn(itinerary)
          mockDocument
        }
    `when`(query.documents).thenReturn(mockDocuments)
    val userItineraries =
        itineraryRepository.getUserItineraries(itineraryObject.SAN_FRANCISCO.userUid).first()
    assertTrue(userItineraries.size == 1)
    assertEquals(userList[0], userItineraries[0])
  }

  @Test
  fun `get itineraries with tags should return an empty list if nothing was found (FIREBASE)`() =
      runTest {
        `when`(itineraryColl.whereArrayContainsAny(anyString(), any())).thenReturn(itineraryColl)
        `when`(itineraryColl.get()).thenReturn(queryTask)
        `when`(queryTask.addOnSuccessListener(any())).thenReturn(queryTask)
        `when`(queryTask.addOnFailureListener(any())).thenAnswer {
          val listener = it.arguments[0] as OnFailureListener
          listener.onFailure(Exception("Get bytes return an exception"))
          null
        }
        val itinerariesWithTags =
            itineraryRepository.getItinerariesWithTags(listOf(ItineraryTags.URBAN)).first()
        assertTrue(itinerariesWithTags.isEmpty())
      }

  @Test
  fun `get itineraries with tags should return the list of itineraries with the tags (FIREBASE)`() =
      runTest {
        `when`(itineraryColl.whereArrayContainsAny(anyString(), any())).thenReturn(itineraryColl)
        `when`(itineraryColl.get()).thenReturn(queryTask)
        `when`(queryTask.addOnSuccessListener(any())).thenAnswer {
          val listener = it.arguments[0] as OnSuccessListener<QuerySnapshot>
          listener.onSuccess(query)
          queryTask
        }
        val locationWithTagUrban = listOf(itineraryObject.SAN_FRANCISCO, itineraryObject.TOKYO)
        val mockDocuments =
            locationWithTagUrban.map { itinerary ->
              val mockDocument = mock(DocumentSnapshot::class.java)
              `when`(mockDocument.toObject(Itinerary::class.java)).thenReturn(itinerary)
              mockDocument
            }
        `when`(query.documents).thenReturn(mockDocuments)
        val itinerariesWithTags =
            itineraryRepository.getItinerariesWithTags(listOf(ItineraryTags.URBAN)).first()
        assertEquals(locationWithTagUrban, itinerariesWithTags)
      }

  @Test(expected = Exception::class)
  fun `get itinerary should throw an error if no itineraries were found (FIREBASE)`() = runTest {
    `when`(itineraryColl.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(docTask)
    `when`(docTask.addOnSuccessListener(any())).thenReturn(docTask)
    `when`(docTask.addOnFailureListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnFailureListener
      listener.onFailure(Exception("Get bytes return an exception"))
      docTask
    }
    itineraryRepository.getItinerary("0")
  }

  @Test(expected = Exception::class)
  fun `get itinerary should throw an error if the document doesn't exist (FIREBASE)`() = runTest {
    `when`(itineraryColl.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(docTask)
    `when`(docTask.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<DocumentSnapshot>
      listener.onSuccess(documentSnapshot)
      docTask
    }
    `when`(documentSnapshot.exists()).thenReturn(false)
    itineraryRepository.getItinerary("0")
  }

  @Test
  fun `get itinerary should return the correct itinerary (FIREBASE)`() = runTest {
    `when`(itineraryColl.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(docTask)
    `when`(docTask.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<DocumentSnapshot>
      listener.onSuccess(documentSnapshot)
      docTask
    }
    `when`(documentSnapshot.exists()).thenReturn(true)
    `when`(documentSnapshot.toObject(Itinerary::class.java))
        .thenReturn(itineraryObject.SAN_FRANCISCO)
    val itinerary = itineraryRepository.getItinerary("0")
    assertEquals(itineraryObject.SAN_FRANCISCO, itinerary)
  }

  @Test(expected = Exception::class)
  fun `set itinerary should throw an error if it wasn't added successfully (FIREBASE)`() = runTest {
    `when`(itineraryColl.document()).thenReturn(documentRef)
    `when`(documentRef.id).thenReturn("0")
    `when`(itineraryColl.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.set(any())).thenReturn(voidTask)
    `when`(voidTask.addOnSuccessListener(any())).thenReturn(voidTask)
    `when`(voidTask.addOnFailureListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnFailureListener
      listener.onFailure(Exception("Get bytes return an exception"))
      voidTask
    }
    itineraryRepository.setItinerary(itineraryObject.SAN_FRANCISCO)
  }

  @Test
  fun `set itinerary should correctly set an itinerary to the database (FIREBASE)`() = runTest {
    val testList = mutableListOf<Itinerary>()
    `when`(itineraryColl.document()).thenReturn(documentRef)
    `when`(documentRef.id).thenReturn("0")
    `when`(itineraryColl.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.set(any())).thenReturn(voidTask)
    `when`(voidTask.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<Void>
      listener.onSuccess(null)
      testList.add(itineraryObject.SAN_FRANCISCO)
      voidTask
    }
    itineraryRepository.setItinerary(itineraryObject.SAN_FRANCISCO)
    assertEquals(listOf(itineraryObject.SAN_FRANCISCO), testList)
  }

  @Test(expected = Exception::class)
  fun `update itinerary should throw an exception if oldUid isnt the same as new itinerary uid`() =
      runTest {
        itineraryRepository.updateItinerary("0", itineraryObject.SAN_FRANCISCO)
      }

  @Test(expected = Exception::class)
  fun `update itinerary should throw an exception if set failed`() = runTest {
    `when`(itineraryColl.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.set(any())).thenReturn(voidTask)
    `when`(voidTask.addOnSuccessListener(any())).thenReturn(voidTask)
    `when`(voidTask.addOnFailureListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnFailureListener
      listener.onFailure(Exception("Get bytes return an exception"))
      voidTask
    }
    itineraryRepository.updateItinerary(
        itineraryObject.SAN_FRANCISCO.uid, itineraryObject.SAN_FRANCISCO)
  }

  @Test
  fun `update itinerary should correctly set a new itinerary to the database (FIREBASE)`() =
      runTest {
        val testList = mutableListOf(itineraryObject.SAN_FRANCISCO)
        val newItinerary = itineraryObject.SAN_FRANCISCO.copy(numLikes = 10)
        `when`(itineraryColl.document(anyString())).thenReturn(documentRef)
        `when`(documentRef.set(any())).thenReturn(voidTask)
        `when`(voidTask.addOnSuccessListener(any())).thenAnswer {
          val listener = it.arguments[0] as OnSuccessListener<Void>
          listener.onSuccess(null)
          testList.remove(itineraryObject.SAN_FRANCISCO)
          testList.add(newItinerary)
          voidTask
        }
        itineraryRepository.updateItinerary(itineraryObject.SAN_FRANCISCO.uid, newItinerary)
        assertEquals(listOf(newItinerary), testList)
      }

  @Test(expected = Exception::class)
  fun `delete itinerary should throw an exception if an error occurred during deletion`() =
      runTest {
        `when`(itineraryColl.document(anyString())).thenReturn(documentRef)
        `when`(documentRef.delete()).thenReturn(voidTask)
        `when`(voidTask.addOnSuccessListener(any())).thenReturn(voidTask)
        `when`(voidTask.addOnFailureListener(any())).thenAnswer {
          val listener = it.arguments[0] as OnFailureListener
          listener.onFailure(Exception("Get bytes return an exception"))
          voidTask
        }
        itineraryRepository.deleteItinerary(itineraryObject.SAN_FRANCISCO)
      }

  @Test
  fun `delete itinerary should correctly remove the itinerary from database`() = runTest {
    val testList = mutableListOf(itineraryObject.SAN_FRANCISCO)
    `when`(itineraryColl.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.delete()).thenReturn(voidTask)
    `when`(voidTask.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<Void>
      listener.onSuccess(null)
      testList.remove(itineraryObject.SAN_FRANCISCO)
      voidTask
    }
    itineraryRepository.deleteItinerary(itineraryObject.SAN_FRANCISCO)
    assertTrue(testList.isEmpty())
  }

  @Test(expected = Exception::class)
  fun `add user to liked should throw an exception if failure occurred`() = runTest {
    `when`(itineraryColl.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.update(anyString(), any())).thenReturn(voidTask)
    `when`(voidTask.addOnSuccessListener(any())).thenReturn(voidTask)
    `when`(voidTask.addOnFailureListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnFailureListener
      listener.onFailure(Exception("Get bytes return an exception"))
      voidTask
    }
    itineraryRepository.addUserToLiked(itineraryObject.SAN_FRANCISCO.uid, "testUser")
  }

  @Test
  fun `add user to liked should correctly add the uid to the itinerary likedUsers list`() = runTest {
    val testItinerary = itineraryObject.SWITZERLAND
    assertTrue(testItinerary.likedUsers.isEmpty())
    `when`(itineraryColl.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.update(anyString(), any())).thenReturn(voidTask)
    `when`(voidTask.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<Void>
      listener.onSuccess(null)
      testItinerary.likedUsers.add("testUser")
      voidTask
    }
    `when`(voidTask.addOnFailureListener(any())).thenReturn(voidTask)
    itineraryRepository.addUserToLiked(testItinerary.uid, "testUser")
    assertTrue(testItinerary.likedUsers.contains("testUser"))
  }

  @Test(expected = Exception::class)
  fun `remove user from liked should throw an exception if failure occurred`() = runTest {
    `when`(itineraryColl.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.update(anyString(), any())).thenReturn(voidTask)
    `when`(voidTask.addOnSuccessListener(any())).thenReturn(voidTask)
    `when`(voidTask.addOnFailureListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnFailureListener
      listener.onFailure(Exception("Get bytes return an exception"))
      voidTask
    }
    itineraryRepository.removeUserFromLiked(itineraryObject.SAN_FRANCISCO.uid, "testUser")
  }

  @Test
  fun `remove user from liked should correctly remove the userUid to the itinerary likedUsers list`() = runTest {
    val testItinerary = itineraryObject.TOKYO
    testItinerary.likedUsers.add("testUser")
    assertFalse(testItinerary.likedUsers.isEmpty())
    `when`(itineraryColl.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.update(anyString(), any())).thenReturn(voidTask)
    `when`(voidTask.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<Void>
      listener.onSuccess(null)
      testItinerary.likedUsers.remove("testUser")
      voidTask
    }
    `when`(voidTask.addOnFailureListener(any())).thenReturn(voidTask)
    itineraryRepository.addUserToLiked(testItinerary.uid, "testUser")
    assertTrue(testItinerary.likedUsers.isEmpty())
  }

  @Test
  fun `get liked users should return an empty list if an error occurred`() = runTest {
    `when`(itineraryColl.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(docTask)
    `when`(docTask.addOnSuccessListener(any())).thenReturn(docTask)
    `when`(docTask.addOnFailureListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnFailureListener
      listener.onFailure(Exception("Get bytes return an exception"))
      null
    }
    val likedUsers = itineraryRepository.getLikedUsers(itineraryObject.SAN_FRANCISCO.uid).first()
    assertTrue(likedUsers.isEmpty())
  }

  @Test
  fun `get liked users should correctly return the likedUser list`() = runTest {
    val testItinerary = itineraryObject.SAN_FRANCISCO
    testItinerary.likedUsers.add("testUser")
    `when`(itineraryColl.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(docTask)
    `when`(docTask.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<DocumentSnapshot>
      listener.onSuccess(documentSnapshot)
      docTask
    }
    `when`(documentSnapshot.get(anyString())).thenReturn(testItinerary.likedUsers)
    val likedUsers = itineraryRepository.getLikedUsers(itineraryObject.SAN_FRANCISCO.uid).first()
    assertEquals(testItinerary.likedUsers, likedUsers)
  }
}
