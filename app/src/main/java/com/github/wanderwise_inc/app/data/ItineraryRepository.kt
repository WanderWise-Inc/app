package com.github.wanderwise_inc.app.data

import android.util.Log
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryLabels
import com.github.wanderwise_inc.app.model.location.Tag
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine

interface ItineraryRepository {
  /** @return a list of all public itineraries */
  fun getPublicItineraries(): Flow<List<Itinerary>>

  /**
   * @param userUid acts as a foreign key to user table
   * @return all itineraries created by user with uid `userUid`
   */
  fun getUserItineraries(userUid: String): Flow<List<Itinerary>>

  /**
   * @param tags
   * @return all itineraries with at least one matching tag with `tags`
   */
  fun getItinerariesWithTags(tags: List<Tag>): Flow<List<Itinerary>>

  suspend fun getItinerary(uid: String): Itinerary

  /** @brief sets an itinerary. If the itinerary has a blank UID, one will be generated */
  fun setItinerary(itinerary: Itinerary)

  /** @brief update and itinerary. */
  fun updateItinerary(oldUid: String, new: Itinerary)

  /** @brief deletes an itinerary */
  fun deleteItinerary(itinerary: Itinerary)
}

/** @brief repository used for testing viewmodel functionality */
class ItineraryRepositoryTestImpl : ItineraryRepository {
  private val itineraries = mutableListOf<Itinerary>()
  private var uidCtr = 0

  override fun getPublicItineraries(): Flow<List<Itinerary>> {
    print(itineraries)
    return flow { emit(itineraries.filter { it.visible }) }
  }

  override fun getUserItineraries(userUid: String): Flow<List<Itinerary>> {
    return flow { emit(itineraries.filter { it.userUid == userUid }) }
  }

  override fun getItinerariesWithTags(tags: List<Tag>): Flow<List<Itinerary>> {
    return flow {
      emit(itineraries.filter { it.tags.toSet().intersect(tags.toSet()).isNotEmpty() })
    }
  }

  override suspend fun getItinerary(uid: String): Itinerary {
    return itineraries.first { it.uid == uid }
  }

  override fun setItinerary(itinerary: Itinerary) {
    itineraries.remove(itinerary) // remove if the itinerary is already present
    if (itinerary.uid.isBlank()) {
      itinerary.uid = uidCtr.toString()
      uidCtr++
    }
    itineraries.add(itinerary)
  }

  override fun updateItinerary(oldUid: String, new: Itinerary) {
    assert(oldUid == new.uid)
    itineraries.removeIf { it.uid == oldUid }
    itineraries.add(new)
    Log.d("ITINERARY_UPDATE", "$new")
  }

  override fun deleteItinerary(itinerary: Itinerary) {
    itineraries.remove(itinerary)
  }
}

class ItineraryRepositoryImpl(private val db: FirebaseFirestore) : ItineraryRepository {
  private val itinerariesCollection = db.collection("itineraries")

  override fun getPublicItineraries(): Flow<List<Itinerary>> {
    return flow {
          val itineraries =
              suspendCancellableCoroutine<List<Itinerary>> { continuation ->
                itinerariesCollection
                    .whereEqualTo(ItineraryLabels.VISIBLE, true)
                    .get()
                    .addOnSuccessListener { snap ->
                      val documents = snap.documents
                      val iti = documents.mapNotNull { it.toObject(Itinerary::class.java) }
                      continuation.resume(iti)
                    }
                    .addOnFailureListener { exception ->
                      continuation.resumeWithException(exception)
                    }
              }
          emit(itineraries)
        }
        .catch { emit(listOf()) }
  }

  override fun getUserItineraries(userUid: String): Flow<List<Itinerary>> {
    return flow {
          val itineraries =
              suspendCancellableCoroutine<List<Itinerary>> { continuation ->
                itinerariesCollection
                    .whereEqualTo(ItineraryLabels.USER_UID, userUid)
                    .get()
                    .addOnSuccessListener { snap ->
                      val documents = snap.documents
                      val iti = documents.mapNotNull { it.toObject(Itinerary::class.java) }
                      Log.d("ItineraryRepository", "Successfully got public itineraries")
                      continuation.resume(iti)
                    }
                    .addOnFailureListener {
                      Log.d("ItineraryRepository", "Failed to get public itineraries")
                      continuation.resumeWithException(it)
                    }
              }
          emit(itineraries)
        }
        .catch { emit(listOf()) }
  }

  override fun getItinerariesWithTags(tags: List<Tag>): Flow<List<Itinerary>> {
    return flow {
          val itineraries =
              suspendCancellableCoroutine<List<Itinerary>> { continuation ->
                itinerariesCollection
                    .whereArrayContainsAny(ItineraryLabels.TAGS, tags)
                    .get()
                    .addOnSuccessListener { snap ->
                      val documents = snap.documents
                      val iti = documents.mapNotNull { it.toObject(Itinerary::class.java) }
                      Log.d("ItineraryRepository", "Successfully got public itineraries")
                      continuation.resume(iti)
                    }
                    .addOnFailureListener {
                      Log.d("ItineraryRepository", "Failed to get public itineraries")
                      continuation.resumeWithException(it)
                    }
              }
          emit(itineraries)
        }
        .catch { emit(listOf()) }
  }

  override suspend fun getItinerary(uid: String): Itinerary {
    val document =
        suspendCancellableCoroutine<DocumentSnapshot> { continuation ->
          itinerariesCollection
              .document(uid)
              .get()
              .addOnSuccessListener { document -> continuation.resume(document) }
              .addOnFailureListener { exception -> continuation.resumeWithException(exception) }
        }
    if (document.exists()) {
      return document.toObject(Itinerary::class.java)!!
    } else {
      throw Exception("Itinerary not found")
    }
  }

  override fun setItinerary(itinerary: Itinerary) {
    if (itinerary.uid.isBlank()) {
      itinerary.uid = itinerariesCollection.document().id
    }
    val itineraryMap = itinerary.toMap()
    itinerariesCollection
        .document(itinerary.uid)
        .set(itineraryMap)
        .addOnSuccessListener { Log.d("ItineraryRepository", "Successfully set itinerary") }
        .addOnFailureListener {
          Log.d("ItineraryRepository", "Failed to set itinerary")
          throw it
        }
  }

  override fun updateItinerary(oldUid: String, new: Itinerary) {
    if (oldUid != new.uid) {
      throw Exception("UIDs do not match")
    }
    val itineraryMap = new.toMap()
    itinerariesCollection
        .document(oldUid)
        .set(itineraryMap)
        .addOnSuccessListener { Log.d("ItineraryRepository", "Successfully updated itinerary") }
        .addOnFailureListener {
          Log.d("ItineraryRepository", "Failed to update itinerary")
          throw it
        }
  }

  override fun deleteItinerary(itinerary: Itinerary) {
    itinerariesCollection
        .document(itinerary.uid)
        .delete()
        .addOnSuccessListener { Log.d("ItineraryRepository", "Successfully deleted itinerary") }
        .addOnFailureListener {
          Log.d("ItineraryRepository", "Failed to delete itinerary")
          throw it
        }
  }
}
