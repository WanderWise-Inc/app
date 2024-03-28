package com.github.wanderwise_inc.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

const val DB_ITINERARY_PATH: String = "itineraries"

// maintain consistency across logs related to this ViewModel
const val LOG_DEBUG_TAG: String = "MapViewModel"

/**
 * @brief ViewModel class for providing `Location`s and `Itinerary`s to the map UI
 */
class MapViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val itineraryCollection = db.collection(DB_ITINERARY_PATH)

    /**
     * @returns a freshly generated itinerary UID
     */
    private fun genItineraryUid(): String {
        return itineraryCollection.document().id
    }

    /**
     * @returns a flow of all public itineraries
     */
    public fun getPublicItineraries(): Flow<List<Itinerary>> {
        return flow {
            val snapshot = itineraryCollection
                .whereEqualTo("visible", true)
                .get()
                .await()
            val itineraryList = snapshot.map { document ->
                document.toObject(Itinerary::class.java)
            }
            emit(itineraryList)
        }
    }

    /**
     * @returns a flow of all `Itinerary`s associated to the currently logged in user
     */
    public fun getUserItineraries(): Flow<List<Itinerary>> {
        // add current user ID with firebase auth
        val currUserUid = 0
        return flow {
            val snapshot = itineraryCollection
                .whereEqualTo("associatedUserUid", currUserUid)
                .get()
                .addOnFailureListener{ e ->
                    Log.d(LOG_DEBUG_TAG, "Get failure: $e")
                }
                .await()
            val itineraryList = snapshot.map { document ->
                document.toObject(Itinerary::class.java)
            }
            emit(itineraryList)
        }
    }

    /**
     * @brief sets an itinerary in DB
     */
    public fun setItinerary(itinerary: Itinerary) {
        // if the itinerary has a blank UID we can generate one for it
        if (itinerary.uid.isBlank()) {
            itinerary.uid = genItineraryUid()
        }
        val docRef = itineraryCollection.document(itinerary.uid)
        docRef.set(itinerary)
            .addOnSuccessListener {
                Log.d(LOG_DEBUG_TAG, "Set success")
            }
            .addOnFailureListener { e ->
                Log.d(LOG_DEBUG_TAG, "Set failure: $e")
            }
    }


    public fun itineraryToPolyline(itinerary: Itinerary, googleMap: GoogleMap): Polyline {
        val polyline = googleMap.addPolyline(PolylineOptions()
            .clickable(true)
            .addAll(itinerary.locations.map{ it.toLatLng()}))

        polyline.tag = itinerary

        return polyline
    }

    private fun createMarkerOption(location: Location): MarkerOptions {
        return MarkerOptions().position(location.toLatLng())
    }
    private fun locationToMarker(location: Location, googleMap: GoogleMap): Marker? {
        return googleMap.addMarker(createMarkerOption(location))
    }
}