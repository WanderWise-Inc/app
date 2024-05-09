package com.github.wanderwise_inc.app.network

interface LocationsApiService {
    @GET("json?")
    fun
}

/** format of geocode maps response for de-serialization */
data class LocationsResponseBody(val routes: List<Route>) {
    data class Route(val legs: List<Leg>) {
        data class Leg(val )
    }
}
