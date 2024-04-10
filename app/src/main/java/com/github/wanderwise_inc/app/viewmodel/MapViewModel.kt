package com.github.wanderwise_inc.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryLabels
import com.github.wanderwise_inc.app.model.location.ItineraryPreferences
import com.github.wanderwise_inc.app.model.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

const val LOG_DEBUG_TAG: String = "MapViewModel" // for logging consistency
const val ITINERARY_COLLECTION_PATH: String = "itineraries"

/**
 * @brief ViewModel class for providing `Location`s and `Itinerary`s to the map UI
 */
class MapViewModel(val itineraryRepository: ItineraryRepository) : ViewModel() {
    /**
     * @return a flow of all public itineraries
     */
    fun getAllPublicItineraries(): Flow<List<Itinerary>> {
        return itineraryRepository.getPublicItineraries()
    }

    /**
     * @return a flow of all `Itinerary`s associated to the currently logged in user
     */
    fun getUserItineraries(userUid: String): Flow<List<Itinerary>> {
        return itineraryRepository.getUserItineraries(userUid)
    }

    /**
     * @param preferences user query preferences
     * @see Itinerary.scoreFromPreferences for sorting the list from View
     * @return a list of itineraries matching a user's query preferences
     */
    fun getItinerariesFromPreferences(preferences: ItineraryPreferences): Flow<List<Itinerary>> {
        return itineraryRepository.getItinerariesWithTags(preferences.tags)
    }

    /**
     * @return a sorted list of itineraries scored based on preferences
     */
    private fun sortItinerariesFromPreferences(itineraries: List<Itinerary>,
                                               preferences: ItineraryPreferences): List<Itinerary> {
        return itineraries.sortedBy { it.scoreFromPreferences(preferences)  }
    }

    /**
     * @brief sets an itinerary in DB
     */
    fun setItinerary(itinerary: Itinerary) {
        itineraryRepository.setItinerary(itinerary)
    }

    /**
     * @brief deletes an itinerary from the database
     */
    fun deleteItinerary(itinerary: Itinerary) {
        itineraryRepository.deleteItinerary(itinerary)
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