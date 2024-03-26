package com.github.wanderwise_inc.app.model.location

data class Itinerary(
    val locations: List<Location>,
    val creator: Int,
    val title: String,
    val description: String?,
    val visible: Boolean
)