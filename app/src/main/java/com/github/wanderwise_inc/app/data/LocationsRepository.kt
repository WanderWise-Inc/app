package com.github.wanderwise_inc.app.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.network.LocationsApiService
import com.github.wanderwise_inc.app.network.Place
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import java.lang.NullPointerException

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
                object : Callback<List<Place>> {
                    override fun onResponse(
                        call: Call<List<Place>>,
                        response: Response<List<Place>>
                    ) {
                        if (response.isSuccessful) {
                            Log.d(DEBUG_TAG, "Response was successful!")
                            val locationsResponse = response.body()
                            Log.d(DEBUG_TAG, locationsResponse.toString())
                            Log.d(DEBUG_TAG, "num elements = ${locationsResponse.extractLocations().size}")
                            resultLiveData.value = locationsResponse.extractLocations().take(limit)
                        } else {
                            resultLiveData.value = null // or any other value that represents a network error
                        }
                    }

                    override fun onFailure(call: Call<List<Place>>, t: Throwable) {
                        Log.d(DEBUG_TAG, "request failed! ${t.message}")
                        resultLiveData.value = null // or any other value that represents a network error
                    }
                }
            )
        
        return resultLiveData
    }
}

private fun List<Place>?.extractLocations(): List<Location> {
    if (this == null) return emptyList()
    val out = mutableListOf<Location>()
    for (place in this) {
        if (place.displayName == null) throw NullPointerException("displayname is null")
        else if (place.lat == null || place.lng == null) throw NullPointerException("lat-lng are null")
        else if (place.importance == null) throw NullPointerException("importance is null")
        else {
            val displayNameSplit = place.displayName.split(",", limit = 2)
            assert(displayNameSplit.size == 2)
            out.add(Location(
                lat = place.lat.toDouble(),
                long = place.lng.toDouble(),
                title = displayNameSplit.first(),
                address = displayNameSplit.last(),
                googleRating = place.importance * 5
            ))
        }
    }
    return out
}
