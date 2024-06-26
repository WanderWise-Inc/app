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

    `when`(db.collection(anyString())).thenReturn(itineraryColl)
    `when`(context.getSystemService(any())).thenReturn(connectivityManager)
    itineraryRepository = ItineraryRepositoryImpl(db, context, savedItinerariesDataStore)
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

  @Test
  fun `get itinerary should return null if the document doesn't exist (FIREBASE)`() = runTest {
    `when`(itineraryColl.document(anyString())).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(docTask)
    `when`(docTask.addOnSuccessListener(any())).thenAnswer {
      val listener = it.arguments[0] as OnSuccessListener<DocumentSnapshot>
      listener.onSuccess(documentSnapshot)
      docTask
    }
    `when`(documentSnapshot.exists()).thenReturn(false)
    val ret = itineraryRepository.getItinerary("0")
    assert(ret == null)
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
}
