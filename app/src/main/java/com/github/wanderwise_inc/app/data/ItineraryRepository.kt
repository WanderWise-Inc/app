package com.github.wanderwise_inc.app.data

import android.util.Log
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryLabels
import com.github.wanderwise_inc.app.model.location.Tag
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

interface ItineraryRepository {
    /**
     * @return a list of all public itineraries
     */
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

    /**
     * @brief sets an itinerary. If the itinerary has a blank UID, one will be generated
     */
    fun setItinerary(itinerary: Itinerary)

    /**
     * @brief deletes an itinerary
     */
    fun deleteItinerary(itinerary: Itinerary)
}

const val ITINERARY_COLLECTION_PATH: String = "itineraries"

/**
 * @brief repository implementation using firestore as data source
 */
class ItineraryRepositoryFirestoreImpl: ItineraryRepository {
    private val LOG_DEBUG_TAG = "REPOSITORY_FIRESTORE"
    private val db = FirebaseFirestore.getInstance()
    private val itineraryCollection = db.collection(ITINERARY_COLLECTION_PATH)
    /**
     * @return a freshly generated UID for an itinerary
     */
    private fun genItineraryUid(): String = itineraryCollection.document().id

    override fun getPublicItineraries(): Flow<List<Itinerary>> {
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

    override fun getUserItineraries(userUid: String): Flow<List<Itinerary>> {
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
                    Log.d(LOG_DEBUG_TAG, "Get success")
                }
                .addOnFailureListener{ e ->
                    Log.d(LOG_DEBUG_TAG, "Get failure, err=$e")
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

    override fun setItinerary(itinerary: Itinerary) {
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

    override fun deleteItinerary(itinerary: Itinerary) {
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

/**
 * @brief repository used for testing viewmodel functionality
 */
class ItineraryRepositoryTestImpl: ItineraryRepository {
    private val itineraries = mutableListOf<Itinerary>()
    private var uidCtr = 0
    override fun getPublicItineraries(): Flow<List<Itinerary>> {
        print(itineraries)
        return flow { emit(itineraries.filter { it.visible }) }
    }

    override fun getUserItineraries(userUid: String): Flow<List<Itinerary>> {
        return flow {
            emit(itineraries.filter { it.userUid == userUid })
        }
    }

    override fun getItinerariesWithTags(tags: List<Tag>): Flow<List<Itinerary>> {
        return flow {
            emit( itineraries.filter {
                it.tags.toSet().intersect(tags.toSet()).isNotEmpty()
            })
        }
    }

    override fun setItinerary(itinerary: Itinerary) {
        itineraries.remove(itinerary) // remove if the itinerary is already present
        if (itinerary.uid.isBlank()) {
            itinerary.uid = uidCtr.toString()
            uidCtr++
        }
        itineraries.add(itinerary)
    }

    override fun deleteItinerary(itinerary: Itinerary) {
        itineraries.remove(itinerary)
    }
}
