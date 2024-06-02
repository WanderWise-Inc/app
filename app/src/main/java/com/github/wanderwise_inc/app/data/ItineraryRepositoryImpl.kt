package com.github.wanderwise_inc.app.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import com.github.wanderwise_inc.app.disk.toModel
import com.github.wanderwise_inc.app.isNetworkAvailable
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryLabels
import com.github.wanderwise_inc.app.model.location.Tag
import com.github.wanderwise_inc.app.proto.location.SavedItineraries
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

/**
 * Fetches and sets `Itinerary` data type on a best-effort basis
 * - if network is available, interaction will happen with firestore database
 * - if network is unavailable, interaction will happen with persistent storage
 */
class ItineraryRepositoryImpl(
    db: FirebaseFirestore,
    private val context: Context,
    private val datastore: DataStore<SavedItineraries>
) : ItineraryRepository {
  private val itinerariesCollection = db.collection("itineraries")

  /**
   * @return a `flow` of all public itineraries, or the list of saved itineraries from persistent
   *   storage when internet connection isn't available
   */
  override fun getPublicItineraries(): Flow<List<Itinerary>> {
    return when (context.isNetworkAvailable()) {
      true -> getPublicItinerariesFirebase()
      false -> getSavedItineraries()
    }
  }

  /** fetches all public itineraries from Firebase */
  private fun getPublicItinerariesFirebase(): Flow<List<Itinerary>> {
    Log.d("ItineraryRepository", "Fetching itineraries from firebase")
    return flow {
          val itineraries = suspendCancellableCoroutine { continuation ->
            itinerariesCollection
                .whereEqualTo(ItineraryLabels.VISIBLE, true)
                .get()
                .addOnSuccessListener { snap ->
                  val documents = snap.documents
                  val iti = documents.mapNotNull { it.toObject(Itinerary::class.java) }
                  continuation.resume(iti)
                }
                .addOnFailureListener { exception -> continuation.resumeWithException(exception) }
          }
          emit(itineraries)
        }
        .catch { emit(emptyList()) }
  }

  /**
   * @return a `flow` of all user-created itineraries, or the list of user-created itineraries from
   *   persistent storage when internet connection isn't available
   */
  override fun getUserItineraries(userUid: String): Flow<List<Itinerary>> {
    return when (context.isNetworkAvailable()) {
      true -> getUserItinerariesFireBase(userUid)
      false -> getSavedItineraries().map { list -> list.filter { it.userUid == userUid } }
    }
  }

  /**
   * @param userUid acts as a foreign key to user table
   * @return all itineraries created by user with uid `userUid`
   */
  private fun getUserItinerariesFireBase(userUid: String): Flow<List<Itinerary>> {
    return flow {
          val itineraries = suspendCancellableCoroutine { continuation ->
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

  /**
   * @param tags the list of tags to match
   * @return a list of `Itineraries` containing at least one matching tag with `tags`
   */
  override fun getItinerariesWithTags(tags: List<Tag>): Flow<List<Itinerary>> {
    return when (context.isNetworkAvailable()) {
      true -> getItinerariesWithTagsFirebase(tags)
      false ->
          getSavedItineraries().map { list ->
            list.filter { itinerary -> tags.any { tag -> itinerary.tags.contains(tag) } }
          }
    }
  }

  /**
   * @param tags the list of tags to match
   * @return all itineraries with at least one matching tag with `tags`
   */
  private fun getItinerariesWithTagsFirebase(tags: List<Tag>): Flow<List<Itinerary>> {
    return flow {
          val itineraries = suspendCancellableCoroutine { continuation ->
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

  /**
   * @param uid the UID of the itinerary
   * @return the `Itinerary` with uid `uid` from firebase (when connection is available) or
   *   persistent storage (when connection isn't available) or `null` if it isn't found
   */
  override suspend fun getItinerary(uid: String): Itinerary? {
    return when (context.isNetworkAvailable()) {
      true -> getItineraryFirebase(uid)
      false -> getItineraryLocal(uid)
    }
  }

  /**
   * gets an itinerary by its UID from firebase
   *
   * @param uid the UID of the itinerary
   * @return the `Itinerary` with uid `uid` from firebase
   */
  private suspend fun getItineraryFirebase(uid: String): Itinerary? {
    val document =
        suspendCancellableCoroutine<DocumentSnapshot> { continuation ->
          itinerariesCollection
              .document(uid)
              .get()
              .addOnSuccessListener { document -> continuation.resume(document) }
              .addOnFailureListener { exception -> continuation.resumeWithException(exception) }
        }
    return if (document.exists()) {
      document.toObject(Itinerary::class.java)!!
    } else {
      null
    }
  }

  /**
   * gets an itinerary by its UID from local storage
   *
   * @param uid the UID of the itinerary
   * @return the `Itinerary` with uid `uid` from disk
   */
  private suspend fun getItineraryLocal(uid: String): Itinerary? {
    Log.d("ItineraryRepositoryImpl", "Getting itinerary $uid from disk")
    return getSavedItineraries()
        .map { itineraryList ->
          Log.d("ItineraryRepositoryImpl", "Local itineraries = ${itineraryList.map{ it.uid }}")
          if (itineraryList.any { it.uid == uid }) itineraryList.first { it.uid == uid } else null
        }
        .first()
  }

  /**
   * Sets an itinerary on firebase.
   * - This itinerary is created if it doesn't exist yet
   * - if `Itinerary.uid` is empty, a fresh one will be generated
   *
   * @param itinerary the itinerary to set
   */
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

  /**
   * updates the fields of the `Itinerary` with uid `oldUid`
   *
   * @param oldUid the UID of the itinerary to update
   * @param new the new itinerary to set
   */
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

  /**
   * deletes an itinerary on firestore
   *
   * @param itinerary the itinerary to delete
   */
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

  /** @return a flow of saved itineraries from local storage */
  private fun getSavedItineraries(): Flow<List<Itinerary>> {
    Log.d("Itinerary Repository", "Reading itineraries from disk")
    val savedItineraries = datastore.data
    return savedItineraries.map { saved ->
      saved.itinerariesList.map { itineraryProto -> itineraryProto.toModel() }
    }
  }

  /** Replaces the stored saved itineraries protobuf with `itineraries` */
  override suspend fun writeItinerariesToDisk(itineraries: List<Itinerary>) {
    Log.d("Itinerary Repository", "writing itineraries to disk: ${itineraries.map { it.uid }}")
    val savedItineraries =
        SavedItineraries.newBuilder().addAllItineraries(itineraries.map { it.toProto() }).build()
    withContext(Dispatchers.IO) { datastore.updateData { savedItineraries } }
  }

  /** @return a new random Id for the itinerary */
  override fun getNewId(): String {
    return itinerariesCollection.document().id
  }
}
