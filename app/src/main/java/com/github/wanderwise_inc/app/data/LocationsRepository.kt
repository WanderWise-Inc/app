package com.github.wanderwise_inc.app.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.network.LocationsApiService
import com.github.wanderwise_inc.app.network.LocationsResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val DEBUG_TAG: String = "LOCATIONS_REPOSITORY"

/** Handles interactions with geocoding API */
class LocationsRepository(private val locationsApiService: LocationsApiService) {
    fun getPlaces(
        name: String,
        limit: Int = 1, // for the moment only fetch 1 place, can be increased later
        apiKey: String,
    ): LiveData<List<Location>?> {
        val resultLiveData = MutableLiveData<List<Location>?>()
        
        locationsApiService
            .getLocation(name = name, key = apiKey)
            .enqueue(
                object : Callback<LocationsResponseBody> {
                    override fun onResponse(
                        call: Call<LocationsResponseBody>,
                        response: Response<LocationsResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            Log.d(DEBUG_TAG, "Response was successful!")
                            val locationsResponse = response.body()
                            Log.d(DEBUG_TAG, "num elements = ${locationsResponse!!.extractLocations().size}")
                            resultLiveData.value = locationsResponse.extractLocations().take(limit)
                        } else {
                            resultLiveData.value = null // or any other value that represents a network error
                        }
                    }

                    override fun onFailure(call: Call<LocationsResponseBody>, t: Throwable) {
                        Log.d(DEBUG_TAG, "request failed! ${t.message}")
                        resultLiveData.value = null // or any other value that represents a network error
                    }
                }
            )
        
        return resultLiveData
    }
}