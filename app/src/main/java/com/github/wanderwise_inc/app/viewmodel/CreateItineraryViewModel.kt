package com.github.wanderwise_inc.app.viewmodel

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewModelScope
import com.github.wanderwise_inc.app.data.DirectionsRepository
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.data.ItineraryRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.ItineraryLabels
import com.github.wanderwise_inc.app.model.location.Tag
import java.io.InvalidObjectException
import kotlinx.coroutines.launch

class CreateItineraryViewModel(
    private val itineraryRepository: ItineraryRepository,
    private val directionsRepository: DirectionsRepository,
    private val imageRepository: ImageRepository,
    private val locationClient: LocationClient,
    context: Context,
) : ItineraryViewModel(itineraryRepository, directionsRepository, locationClient) {
  /** New itinerary that the signed in user is currently building */
  private var newItineraryBuilder: Itinerary.Builder? = null

  /** Nes itinerary banner picture * */
  private var newItineraryBannerPicture: Bitmap? = null

  init {
    viewModelScope.launch {
      imageRepository.getBitMap(imageRepository.getCurrentFile(), context).collect {
        newItineraryBannerPicture = it
      }
    }
  }

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

  fun setNewItineraryPrice(price: Float) {
    newItineraryBuilder?.price = price
  }

  fun setNewItineraryTime(time: Float) {
    newItineraryBuilder?.time = time
  }

  fun setNewItineraryTags(tags: MutableList<Tag>) {
    newItineraryBuilder?.tags?.clear()
    newItineraryBuilder?.tags?.addAll(tags)
  }

  /** @returns a list of ItineraryLabels of fields that haven't been set * */
  fun notSetValues(): List<String> {
    // look if all values have been set
    val notSetValues: MutableList<String> = mutableListOf()

    if (newItineraryBuilder == null) {
      // this case shouldnt be possible
      // add error message
      throw InvalidObjectException("Itinerary.Builder is `null`")
    } else {
      if (newItineraryBuilder!!.price == null) {
        notSetValues.add(ItineraryLabels.PRICE)
      }
      if (newItineraryBuilder!!.time == null) {
        notSetValues.add(ItineraryLabels.TIME)
      }
      if (newItineraryBuilder!!.description == null) {
        notSetValues.add(ItineraryLabels.DESCRIPTION)
      }
      if (newItineraryBuilder!!.title == null) {
        notSetValues.add(ItineraryLabels.TITLE)
      }
      if (newItineraryBuilder!!.locations.isEmpty()) {
        notSetValues.add(ItineraryLabels.LOCATIONS)
      }
      if (newItineraryBuilder!!.tags.isEmpty()) {
        notSetValues.add(ItineraryLabels.TAGS)
      }
    }

    // return the notSetValues (empty if everything has been set)
    return notSetValues
  }

  fun uploadItineraryBannerPictureToStorage(userUid: String) {
    imageRepository.uploadImageToStorage("profilePicture/${userUid}")
  }

  fun startItineraryBannerPictureUploadActivity() {
    Intent(Intent.ACTION_GET_CONTENT).also {
      it.type = "image/*" // Set type to any image format.
      imageRepository.launchActivity(it) // Launch activity to select an image.
    }
  }
}
