package com.github.wanderwise_inc.app.network

import com.github.wanderwise_inc.app.model.location.Location
import com.google.gson.annotations.SerializedName
import com.google.android.gms.maps.model.LatLng
import com.google.common.primitives.UnsignedInteger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/** Handles interactions with geocoding api */
interface LocationsApiService {
    @GET("search?")
    fun getLocation(
        @Query("q") name: String,
        @Query("api_key") key: String
    ): Call<List<Place>>
}

/** format of geocode maps response for de-serialization */
data class Place(
    /*@SerializedName("place_id")*/ val placeId: Long,
    /*@SerializedName("licence")*/ val licence: String,
    /*@SerializedName("osm_type")*/ val osmType: String,
    /*@SerializedName("osm_id")*/ val osmId: Long,
    /*@SerializedName("boundingbox")*/ val boundingBox: List<String>,
    /*@SerializedName("lat")*/ val lat: String,
    /*@SerializedName("lon")*/ val lng: String,
    /*@SerializedName("display_name")*/ val displayName: String,
    /*@SerializedName("class")*/ val placeClass: String,    
    /*@SerializedName("type")*/ val type: String,
    /*@SerializedName("importance")*/ val importance: Float,
)



/** Factory for creating `ApiService` design patterns for Locations uwu */
object LocationsApiServiceFactory {
    private const val BASE_URL = "https://geocode.maps.co/"
    
    fun createLocationsApiService(): LocationsApiService {
        val retrofit =
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                //.client(OkHttpClient().newBuilder()
                    //.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    //.readTimeout(, TimeUnit.SECONDS)
                    //.writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    //.connectTimeout(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    //.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create(LocationsApiService::class.java)
    }
}