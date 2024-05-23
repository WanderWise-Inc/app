package com.github.wanderwise_inc.app.network

import com.google.gson.annotations.SerializedName
import okhttp3.HttpUrl
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/** Handles interactions with geocoding api */
interface LocationsApiService {
  @GET("search?")
  fun getLocation(@Query("q") name: String, @Query("api_key") key: String): Call<List<Place>>
}

/** format of geocode maps response for de-serialization */
data class Place(
    @SerializedName("place_id") val placeId: Long?,
    @SerializedName("licence") val licence: String?,
    @SerializedName("osm_type") val osmType: String?,
    @SerializedName("osm_id") val osmId: Long?,
    @SerializedName("boundingbox") val boundingBox: List<String>?,
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lon") val lng: Double?,
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("class") val placeClass: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("importance") val importance: Float?,
)

/** Factory for creating `ApiService` design patterns for Locations uwu */
object LocationsApiServiceFactory {
    private val BASE_URL = HttpUrl.Builder()
        .scheme("https")
        .host("geocode.maps.co")
        .build()

    fun createLocationsApiService(baseUrl: HttpUrl = BASE_URL): LocationsApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(LocationsApiService::class.java)
    }
}
