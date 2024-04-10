package com.github.wanderwise_inc.app.data

import android.util.Log
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryLabels
import com.github.wanderwise_inc.app.model.location.Tag
import com.github.wanderwise_inc.app.viewmodel.LOG_DEBUG_TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

interface ItineraryRepository {
    /**
     * @return a list of all public itineraries
     */
    suspend fun getPublicItineraries(): Flow<List<Itinerary>>

    /**
     * @param userUid acts as a foreign key to user table
     * @return all itineraries created by user with uid `userUid`
     */
    suspend fun getUserItineraries(userUid: String): Flow<List<Itinerary>>

    /**
     * @param tags
     * @return all itineraries with at least one matching tag with `tags`
     */
    fun getItinerariesWithTags(tags: List<Tag>): Flow<List<Itinerary>>

    /**
     * @brief sets an itinerary. If the itinerary has a blank UID, one will be generated
     */
    suspend fun setItinerary(itinerary: Itinerary)

    /**
     * @brief deletes an itinerary
     */
    suspend fun deleteItinerary(itinerary: Itinerary)
}

const val ITINERARY_COLLECTION_PATH: String = "itineraries"

class ItineraryRepositoryFirestoreImpl: ItineraryRepository {
    private val db = FirebaseFirestore.getInstance()
    private val itineraryCollection = db.collection(ITINERARY_COLLECTION_PATH)
    private val DEBUG_TAG = "FirestoreRepositoryImpl"
    /**
     * @return a freshly generated UID for an itinerary
     */
    private fun genItineraryUid(): String = itineraryCollection.document().id

    override suspend fun getPublicItineraries(): Flow<List<Itinerary>> {
        return flow {
            val snapshot = itineraryCollection
                .whereEqualTo("visible", true)
                .get()
                .addOnSuccessListener {
                    Log.d(LOG_DEBUG_TAG, "Get success")
                }
                .addOnFailureListener { e ->
                    Log.d(LOG_DEBUG_TAG, "Get failure: err=$e")
                }
                .await()
            val itineraryList = snapshot.map { document ->
                document.toObject(Itinerary::class.java)
            }
            emit(itineraryList)
        }
    }

    override suspend fun getUserItineraries(userUid: String): Flow<List<Itinerary>> {
        val currUserUid = FirebaseAuth.getInstance().currentUser?.uid

        return flow {

            // if the current user is non-null and equal to the queried user, return all itineraries.
            // otherwise, we only return the public ones
            val query = if (currUserUid != null && currUserUid == userUid) {
                itineraryCollection
                    .whereEqualTo(ItineraryLabels.USER_UID, userUid)
            } else {
                itineraryCollection
                    .whereEqualTo(ItineraryLabels.USER_UID, userUid)
                    .whereEqualTo(ItineraryLabels.VISIBLE, true)
            }

            val snapshot = query
                .get()
                .addOnSuccessListener{
                    Log.d(DEBUG_TAG, "Get success")
                }
                .addOnFailureListener{ e ->
                    Log.d(DEBUG_TAG, "Get failure, err=$e")
                }
                .await()

            val itineraryList = snapshot.map { document ->
                document.toObject(Itinerary::class.java)
            }
            emit(itineraryList)
        }
    }

    override fun getItinerariesWithTags(tags: List<Tag>): Flow<List<Itinerary>> {
        return flow {
            val query = itineraryCollection
                .whereArrayContainsAny(ItineraryLabels.TAGS, tags)

            val matches = query.get().await().map { document ->
                document.toObject(Itinerary::class.java)
            }
            Log.d(LOG_DEBUG_TAG, "got $matches")
            emit(matches)
        }
    }

    override suspend fun setItinerary(itinerary: Itinerary) {
        if (itinerary.uid.isBlank()) {
            itinerary.uid = genItineraryUid()
        }
        val docRef = itineraryCollection.document(itinerary.uid)
        docRef.set(itinerary.toMap())
            .addOnSuccessListener {
                Log.d(LOG_DEBUG_TAG, "Set success")
            }
            .addOnFailureListener { e ->
                Log.d(LOG_DEBUG_TAG, "Set failure: err=$e")
            }
    }

    override suspend fun deleteItinerary(itinerary: Itinerary) {
        val docRef = itineraryCollection.document(itinerary.uid)
        docRef.delete()
            .addOnSuccessListener {
                Log.d(LOG_DEBUG_TAG, "Delete success: uid=$itinerary.uid")
            }
            .addOnFailureListener { e ->
                Log.d(LOG_DEBUG_TAG, "Delete failure: uid=$itinerary.uid. err=$e")
            }
    }
}
