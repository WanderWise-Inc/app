package com.github.wanderwise_inc.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.wanderwise_inc.app.BuildConfig
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryPreferences
import com.github.wanderwise_inc.app.model.location.Location
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

/** @brief ViewModel class for providing `Location`s and `Itinerary`s to the map UI */
class MapViewModel(private val itineraryRepository: ItineraryRepository) : ViewModel() {
  /** @return a flow of all public itineraries */
  fun getAllPublicItineraries(): Flow<List<Itinerary>> {
    return itineraryRepository.getPublicItineraries()
  }

  /** @return a flow of all `Itinerary`s associated to the currently logged in user */
  fun getUserItineraries(userUid: String): Flow<List<Itinerary>> {
    return itineraryRepository.getUserItineraries(userUid)
  }

  /** @return the total number of likes from a list of itineraries */
  fun getItineraryUpvotes(itineraries: List<Itinerary>): Int {
    var totalLikes: Int = 0
    for (itinerary in itineraries) totalLikes += itinerary.numLikes
    return totalLikes
  }

  /**
   * @param preferences user query preferences
   * @return a list of itineraries matching a user's query preferences
   * @see Itinerary.scoreFromPreferences for sorting the list from View
   */
  fun getItinerariesFromPreferences(preferences: ItineraryPreferences): Flow<List<Itinerary>> {
    return itineraryRepository.getItinerariesWithTags(preferences.tags)
  }

  /** @return a sorted list of itineraries scored based on preferences */
  fun sortItinerariesFromPreferences(
      itineraries: List<Itinerary>,
      preferences: ItineraryPreferences
  ): List<Itinerary> {
    // invert the sign so that the list is sorted in descending order
    return itineraries.sortedBy { -it.scoreFromPreferences(preferences) }
  }

  /** @brief sets an itinerary in DB */
  fun setItinerary(itinerary: Itinerary) {
    itineraryRepository.setItinerary(itinerary)
  }

  /** @brief deletes an itinerary from the database */
  fun deleteItinerary(itinerary: Itinerary) {
    itineraryRepository.deleteItinerary(itinerary)
  }

  suspend fun computeShortestPath(origin: Location, destination: Location, waypoints: List<Location>?
  ): List<Location>? {
    //https://lwgmnz.me/google-maps-and-directions-api-using-kotlin/
    //https://developers.google.com/maps/documentation/directions/get-directions
    Log.d("test", "1. entered function")
    //create the url from input parameters
    val urlDirection: String = directionsUrl(origin, destination, waypoints)
    Log.d("test", "2. url created : $urlDirection")

    var path : List<Location>? = null

    GlobalScope.launch {
      val responseBody = makeDirectionsRequest(urlDirection)
      if(responseBody != null) {
        Log.d("test", "3. done request : $responseBody")
        path = parseDirectionsResponse(responseBody)
        Log.d("test", "4. parsed response")
      }
    }
    return path
  }
  private suspend fun makeDirectionsRequest(url: String): String?{
    return try {
      val client = OkHttpClient()
      val request = Request.Builder().url(url).build()
      val response = client.newCall(request).execute()
      if (!response.isSuccessful) throw IOException("Unexpected response $response")
      Log.d("test", "-> response successful")
      response.body.toString()
    } catch (e: IOException) {
      Log.d("test", "-> response failed")
      null
    }
  }
  private fun parseDirectionsResponse(response: String): List<Location> {
    val path: MutableList<List<Location>> = mutableListOf()
    val jsonResponse = JSONObject(response)
    val routes = jsonResponse.getJSONArray("routes")
    val legs = routes.getJSONObject(0).getJSONArray("legs")
    val steps = legs.getJSONObject(0).getJSONArray("steps")
    for (i in 0 until steps.length()) {
      val points =
        steps.getJSONObject(i).getJSONObject("polyline").getString("points")
      path.add(
        PolyUtil.decode(points).map { latln -> Location.fromLatLng(latln) })
    }
    return path.flatten()
  }
  private fun directionsUrl(origin: Location, destination: Location,
                            waypoints: List<Location>?): String {
//        val sDestination: String =
//            "?destination=" + origin.lat.toString() + "," + origin.long.toString()
//        val sOrigin: String =
//            "&origin=" + destination.lat.toString() + "," + destination.long.toString()
//        //create the waypoints string if any
//        val sWaypoints: String = if (waypoints != null) {
//            StringBuilder().apply {
//                append("&waypoints=")
//                for ((i, loc) in waypoints.withIndex()) {
//                    append("via:" + loc.lat.toString() + "," + loc.long.toString())
//                    if (i < waypoints.size) {
//                        append("|")
//                    }
//                }
//            }.toString()
//        } else {
//            ""
//        }
//        //create the url from input parameters
//        return "https://maps.googleapis.com/maps/api/directions/json" +
//                sDestination +
//                sOrigin +
//                sWaypoints +
//                "&key=${MAPS_API_KEY}"
    return "https://maps.googleapis.com/maps/api/directions/json" +
            "?destination=Montreal" +
            "&origin=Toronto" +
            "&key=${BuildConfig.MAPS_API_KEY}"
  }
}
