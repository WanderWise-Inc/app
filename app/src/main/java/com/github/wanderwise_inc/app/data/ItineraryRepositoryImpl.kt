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

class ItineraryRepositoryImpl(db: FirebaseFirestore) : ItineraryRepository {
  private val itinerariesCollection = db.collection("itineraries")

  override fun getPublicItineraries(): Flow<List<Itinerary>> {
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
        .catch { emit(listOf()) }
  }

  override fun getUserItineraries(userUid: String): Flow<List<Itinerary>> {
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

  override fun getItinerariesWithTags(tags: List<Tag>): Flow<List<Itinerary>> {
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
