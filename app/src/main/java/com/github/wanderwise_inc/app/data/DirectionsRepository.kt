package com.github.wanderwise_inc.app.data

import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng

/** Handles interactions with maps API */
interface DirectionsRepository {
  fun getPolylineWayPoints(
      origin: String,
      destination: String,
      waypoints: List<String>,
      apiKey: String
  ): LiveData<List<LatLng>?>
  ): LiveData<List<LatLng>?> {
    val resultLiveData = MutableLiveData<List<LatLng>?>()

    directionsApiService
        .getPolylineWayPoints(
            origin = origin,
            destination = destination,
            waypoints = waypoints,
            key = apiKey,
            mode = "walking")
        .enqueue(
            object : Callback<DirectionsResponseBody> {
              override fun onResponse(
                  call: Call<DirectionsResponseBody>,
                  response: Response<DirectionsResponseBody>
              ) {
                if (response.isSuccessful) {
                  Log.d(DEBUG_TAG, "Response was successful!")
                  val directionsResponse = response.body()
                  Log.d(DEBUG_TAG, "num elements = ${directionsResponse!!.toLatLngList().size}")
                  resultLiveData.value = directionsResponse.toLatLngList()
                } else {
                  resultLiveData.value = null // or any other value that represents an error
                }
              }

              override fun onFailure(call: Call<DirectionsResponseBody>, t: Throwable) {
                Log.d(DEBUG_TAG, "request failed! ${t.message}")
                resultLiveData.value = null // or any other value that represents a network error
              }
            })

    return resultLiveData
  }
}
