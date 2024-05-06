package com.github.wanderwise_inc.app.data

import android.util.Log
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryLabels
import com.github.wanderwise_inc.app.model.location.Tag
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

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
/**
 * const val ITINERARY_COLLECTION_PATH: String = "itineraries"
 *
 * /** @brief repository implementation using firestore as data source */ class
 * ItineraryRepositoryFirestoreImpl : ItineraryRepository { private val LOG_DEBUG_TAG =
 * "REPOSITORY_FIRESTORE" private val db = FirebaseFirestore.getInstance() private val
 * itineraryCollection = db.collection(ITINERARY_COLLECTION_PATH) /** @return a freshly generated
 * UID for an itinerary */ private fun genItineraryUid(): String = itineraryCollection.document().id
 *
 * override fun getPublicItineraries(): Flow<List<Itinerary>> { return flow { val snapshot =
 * itineraryCollection .whereEqualTo("visible", true) .get() .addOnSuccessListener {
 * Log.d(LOG_DEBUG_TAG, "Get success") } .addOnFailureListener { e -> Log.d(LOG_DEBUG_TAG, "Get
 * failure: err=$e") } .await() val itineraryList = snapshot.map { document ->
 * document.toObject(Itinerary::class.java) } emit(itineraryList) } }
 *
 * override fun getUserItineraries(userUid: String): Flow<List<Itinerary>> { val currUserUid =
 * FirebaseAuth.getInstance().currentUser?.uid
 *
 * return flow {
 *
 * // if the current user is non-null and equal to the queried user, return all itineraries. //
 * otherwise, we only return the public ones val query = if (currUserUid != null && currUserUid ==
 * userUid) { itineraryCollection.whereEqualTo(ItineraryLabels.USER_UID, userUid) } else {
 * itineraryCollection .whereEqualTo(ItineraryLabels.USER_UID, userUid)
 * .whereEqualTo(ItineraryLabels.VISIBLE, true) }
 *
 * val snapshot = query .get() .addOnSuccessListener { Log.d(LOG_DEBUG_TAG, "Get success") }
 * .addOnFailureListener { e -> Log.d(LOG_DEBUG_TAG, "Get failure, err=$e") } .await()
 *
 * val itineraryList = snapshot.map { document -> document.toObject(Itinerary::class.java) }
 * emit(itineraryList) } }
 *
 * override fun getItinerariesWithTags(tags: List<Tag>): Flow<List<Itinerary>> { return flow { val
 * query = itineraryCollection.whereArrayContainsAny(ItineraryLabels.TAGS, tags)
 *
 * val matches = query.get().await().map { document -> document.toObject(Itinerary::class.java) }
 * Log.d(LOG_DEBUG_TAG, "got $matches") emit(matches) } }
 *
 * override fun setItinerary(itinerary: Itinerary) { if (itinerary.uid.isBlank()) { itinerary.uid =
 * genItineraryUid() } val docRef = itineraryCollection.document(itinerary.uid) docRef
 * .set(itinerary.toMap()) .addOnSuccessListener { Log.d(LOG_DEBUG_TAG, "Set success") }
 * .addOnFailureListener { e -> Log.d(LOG_DEBUG_TAG, "Set failure: err=$e") } }
 *
 * override fun updateItinerary(oldUid: String, new: Itinerary) { assert(oldUid == new.uid) val
 * docRef = itineraryCollection.document(oldUid) docRef .update(new) .addOnSuccessListener {
 * Log.d(LOG_DEBUG_TAG, "Update success") } .addOnFailureListener { e -> Log.d(LOG_DEBUG_TAG,
 * "Update failure: err=$e") } }
 *
 * override fun deleteItinerary(itinerary: Itinerary) { val docRef =
 * itineraryCollection.document(itinerary.uid) docRef .delete() .addOnSuccessListener {
 * Log.d(LOG_DEBUG_TAG, "Delete success: uid=$itinerary.uid") } .addOnFailureListener { e ->
 * Log.d(LOG_DEBUG_TAG, "Delete failure: uid=$itinerary.uid. err=$e") } } }
 */

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

class ItineraryRepositoryImpl : ItineraryRepository {

  private val db = FirebaseFirestore.getInstance()
  private val itinerariesCollection = db.collection("itineraries")

  override fun getPublicItineraries(): Flow<List<Itinerary>> {
    return flow {
          val snap =
              itinerariesCollection
                  .whereEqualTo(ItineraryLabels.VISIBLE, true)
                  .get()
                  .addOnFailureListener {
                    Log.d("ItineraryRepository", "Failed to get public itineraries")
                  }
                  .addOnSuccessListener {
                    Log.d("ItineraryRepository", "Successfully got public itineraries")
                  }
                  .await()
          val itineraries = snap.map { it.toObject(Itinerary::class.java) }
          emit(itineraries)
        }
        .catch { emit(listOf()) }
  }

  override fun getUserItineraries(userUid: String): Flow<List<Itinerary>> {
    return flow {
          val snap =
              itinerariesCollection
                  .whereEqualTo(ItineraryLabels.USER_UID, userUid)
                  .get()
                  .addOnFailureListener {
                    Log.d("ItineraryRepository", "Failed to get public itineraries")
                  }
                  .addOnSuccessListener {
                    Log.d("ItineraryRepository", "Successfully got public itineraries")
                  }
                  .await()
          val itineraries = snap.map { it.toObject(Itinerary::class.java) }
          emit(itineraries)
        }
        .catch { emit(listOf()) }
  }

  override fun getItinerariesWithTags(tags: List<Tag>): Flow<List<Itinerary>> {
    return flow {
          val snap =
              itinerariesCollection
                  .whereArrayContainsAny(ItineraryLabels.TAGS, tags)
                  .get()
                  .addOnFailureListener {
                    Log.d("ItineraryRepository", "Failed to get public itineraries")
                  }
                  .addOnSuccessListener {
                    Log.d("ItineraryRepository", "Successfully got public itineraries")
                  }
                  .await()
          val itineraries = snap.map { it.toObject(Itinerary::class.java) }
          emit(itineraries)
        }
        .catch { emit(listOf()) }
  }

  override suspend fun getItinerary(uid: String): Itinerary {
    val document = itinerariesCollection.document(uid).get().await()
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
        .addOnFailureListener { Log.d("ItineraryRepository", "Failed to set itinerary") }
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
        .addOnFailureListener { Log.d("ItineraryRepository", "Failed to update itinerary") }
  }

  override fun deleteItinerary(itinerary: Itinerary) {
    itinerariesCollection
        .document(itinerary.uid)
        .delete()
        .addOnSuccessListener { Log.d("ItineraryRepository", "Successfully deleted itinerary") }
        .addOnFailureListener { Log.d("ItineraryRepository", "Failed to delete itinerary") }
  }
}
