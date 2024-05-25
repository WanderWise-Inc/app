package com.github.wanderwise_inc.app.network

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import okhttp3.HttpUrl
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApiService {
  @GET("json?")
  fun getPolylineWayPoints(
      @Query("origin") origin: String,
      @Query("destination") destination: String,
      @Query("waypoints") waypoints: List<String>,
      @Query("mode") mode: String,
      @Query("key") key: String
  ): Call<DirectionsResponseBody>
}

/** format of google maps response for de-serialization */
data class DirectionsResponseBody(val routes: List<Route>) {
  data class Route(val legs: List<Leg>) {
    data class Leg(val steps: List<Step>) {
      data class Step(
          @SerializedName("start_location") val startLocation: RespLocation,
          @SerializedName("end_location") val endLocation: RespLocation
      )

      data class RespLocation(val lat: Double, val lng: Double)
    }
  }

  /** returns the parsed response waypoints as a list of `LatLng` */
  fun toLatLngList(): List<LatLng> {
    var out: List<LatLng> = listOf()
    for (route in routes) {
      for (leg in route.legs) {
        for (step in leg.steps) {
          out = out + LatLng(step.endLocation.lat, step.endLocation.lng)
        }
      }
    }
    return out
  }
}

/** Factory for creating `ApiService` design patterns for Directions API uwu */
object DirectionsApiServiceFactory {
  private val BASE_URL = HttpUrl.Builder()
      .scheme("https")
      .host("maps.googleapis.com")
      .addPathSegment("maps")
      .addPathSegment("api")
      .addPathSegment("directions")
      .build()

  fun createDirectionsApiService(baseUrl: HttpUrl = BASE_URL): DirectionsApiService {
    val retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    return retrofit.create(DirectionsApiService::class.java)
  }
}
