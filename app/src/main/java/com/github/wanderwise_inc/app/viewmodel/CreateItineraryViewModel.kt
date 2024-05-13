package com.github.wanderwise_inc.app.viewmodel

import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryDefaultValues
import com.github.wanderwise_inc.app.model.location.ItineraryLabels
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

  fun notSetValues() : List<String> {
    //look if all values have been set
    val notSetValues : MutableList<String> = mutableListOf();

    if(newItineraryBuilder == null){
      //this case shouldnt be possible
      //add error message
      throw InvalidObjectException("Itinerary.Builder is `null`")
    } else {
      if(newItineraryBuilder!!.price == ItineraryDefaultValues.PRICE){
        notSetValues.add(ItineraryLabels.PRICE)
      }
      if(newItineraryBuilder!!.time == ItineraryDefaultValues.TIME){
        notSetValues.add(ItineraryLabels.TIME)
      }
      if(newItineraryBuilder!!.description == ItineraryDefaultValues.DESCRIPTION){
        notSetValues.add(ItineraryLabels.DESCRIPTION)
      }
      if(newItineraryBuilder!!.title == ItineraryDefaultValues.TITLE){
        notSetValues.add(ItineraryLabels.TITLE)
      }
      if(newItineraryBuilder!!.locations.isEmpty()){
        notSetValues.add(ItineraryLabels.LOCATIONS)
      }
      if(newItineraryBuilder!!.tags.isEmpty()){
        notSetValues.add(ItineraryLabels.TAGS)
      }
    }

    //return the notSetValues (empty if everything has been set)
    return notSetValues
  }
}
