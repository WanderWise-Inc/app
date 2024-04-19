package com.github.wanderwise_inc.app.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.wanderwise_inc.app.BuildConfig
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.network.DirectionsApiService
import com.github.wanderwise_inc.app.network.DirectionsResponseBody
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val DEBUG_TAG: String = "DIRECTIONS_REPOSITORY"

/**
 * Handles interactions with maps API
 */
class DirectionsRepository(private val directionsApiService: DirectionsApiService) {
  fun getPolylineWayPoints(origin: String, destination: String, apiKey: String): LiveData<List<LatLng>?> {
    val resultLiveData = MutableLiveData<List<LatLng>?>()

    directionsApiService.getPolylineWayPoints(origin, destination, apiKey).enqueue(object :
      Callback<DirectionsResponseBody> {
      override fun onResponse(call: Call<DirectionsResponseBody>, response: Response<DirectionsResponseBody>) {
        if (response.isSuccessful) {
          Log.d(DEBUG_TAG, "Response was successful!")
          val directionsResponse = response.body()
          Log.d(DEBUG_TAG, "num elements = ${directionsResponse!!.toLatLngList().size}")
          resultLiveData.value = directionsResponse.toLatLngList()
        } else {
          resultLiveData.value = null// or any other value that represents an error
        }
      }

      override fun onFailure(call: Call<DirectionsResponseBody>, t: Throwable) {
        Log.d(DEBUG_TAG, "request failed! ${t.message}")
        resultLiveData.value = null // or any other value that represents a network error
      }
    })

    return resultLiveData
  }

  fun getPolylineWayPointsFlow(origin: String, destination: String, apiKey: String): Flow<List<LatLng>> {
    return flow {
      val response =
        directionsApiService.getPolylineWayPoints(origin, destination, apiKey).execute()
      val returnedList = response.body()!!.toLatLngList()
      Log.d(DEBUG_TAG, "server responded : $returnedList")
      emit(returnedList)
    }
  }


  /**
   * builds a url for google directions request from an itinerary
   */
  private fun makeUrl(itinerary: Itinerary): String {
    assert(itinerary.locations.size <= 23)
    assert(itinerary.locations.size >= 2)

    val origin = itinerary.locations.first()
    val destination = itinerary.locations.last()

    // drop the first and last
    val remaining = itinerary.locations.drop(1).dropLast(1)

    val urlDirections = "https://maps.googleapis.com/maps/api/directions/json?"
    val builder = StringBuilder(urlDirections)

    builder.append("origin=${origin.lat},${origin.long}")
    builder.append("&destination=${destination.lat},${destination.long}")
    builder.append("&key=${BuildConfig.MAPS_API_KEY}")
    return builder.toString()
  }

  /**
   * parses a Directions API response following their spec
   */
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
}