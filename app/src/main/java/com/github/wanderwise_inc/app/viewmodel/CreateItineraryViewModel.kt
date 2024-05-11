package com.github.wanderwise_inc.app.viewmodel

import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import java.io.InvalidObjectException

class CreateItineraryViewModel(
    private val itineraryRepository: ItineraryRepository,
    private val directionsRepository: DirectionsRepository,
    private val locationClient: LocationClient
) : ItineraryViewModel(itineraryRepository, directionsRepository, locationClient) {
  /** New itinerary that the signed in user is currently building */
  private var newItineraryBuilder: Itinerary.Builder? = null

  fun getNewItinerary(): Itinerary.Builder? {
    return newItineraryBuilder
  }

  /** initializes a new `MutableItinerary` */
  fun startNewItinerary(userUid: String) {
    newItineraryBuilder = Itinerary.Builder(userUid = userUid)
  }

  fun uploadNewItinerary() {
    if (newItineraryBuilder != null) itineraryRepository.setItinerary(newItineraryBuilder!!.build())
    else throw InvalidObjectException("Itinerary.Builder is `null`")
  }

  fun setNewItineraryTitle(title: String) {
    newItineraryBuilder?.title = title
  }

  fun setNewItineraryDescription(description: String) {
    newItineraryBuilder?.description = description
  }
}
