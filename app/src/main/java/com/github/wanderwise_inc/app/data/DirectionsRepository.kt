package com.github.wanderwise_inc.app.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.wanderwise_inc.app.network.DirectionsApiService
import com.github.wanderwise_inc.app.network.DirectionsResponseBody
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val DEBUG_TAG: String = "DIRECTIONS_REPOSITORY"

/** Handles interactions with maps API */
class DirectionsRepository(private val directionsApiService: DirectionsApiService) {
  fun getPolylineWayPoints(
      origin: String,
      destination: String,
      apiKey: String
  ): LiveData<List<LatLng>?> {
    val resultLiveData = MutableLiveData<List<LatLng>?>()

    directionsApiService
        .getPolylineWayPoints(origin, destination, apiKey)
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
