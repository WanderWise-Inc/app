package com.github.wanderwise_inc.app.network

import com.github.wanderwise_inc.app.model.location.Location
import com.google.gson.annotations.SerializedName
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/** Handles interactions with geocoding api */
interface LocationsApiService {
    @GET("json?")
    fun getLocation(
        @Query("name") name: String,
        @Query("key") key: String
    ): Call<LocationsResponseBody>
}

/** format of geocode maps response for de-serialization */
data class LocationsResponseBody(val places: List<Place>) {
    data class Place(
        @SerializedName("place_id") val placeId: Int,
        @SerializedName("licence") val licence: String,
        @SerializedName("osm_type") val osmType: String,
        @SerializedName("osm_id") val osmId: Int,
        @SerializedName("boundingbox") val boundingBox: BoundingBox,
        @SerializedName("lat") val lat: String,
        @SerializedName("lon") val lng: String,
        @SerializedName("display_name") val displayName: String,
        @SerializedName("class") val placeClass: String,
        @SerializedName("type") val type: String,
        @SerializedName("importance") val importance: Float,
    ) {
        data class BoundingBox(
            @SerializedName("0") val minLat: String,
            @SerializedName("1") val maxLat: String,
            @SerializedName("2") val minLng: String,
            @SerializedName("3") val maxLng: String,
        )
    }
    
    fun extractLocations(): List<Location> {
        val out = mutableListOf<Location>()
        for (place in places) {
            val displayNameSplit = place.displayName.split(",", limit = 2)
            assert(displayNameSplit.size == 2)
            out.add(Location(
                lat = place.lat.toDouble(),
                long = place.lng.toDouble(),
                title = displayNameSplit.first(),
                address = displayNameSplit.last(),
                googleRating = place.importance * 5
            ))
        //LatLng(place.lat.toDouble(), place.lng.toDouble())
        }
        return out
    }
}


/** Factory for creating `ApiService` design patterns for Locations uwu */
object LocationsApiServiceFactory {
    private const val BASE_URL = "https://geocode.maps.co/search/"

    fun createDirectionsApiService(): LocationsApiService {
        val retrofit =
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create(LocationsApiService::class.java)
    }
}