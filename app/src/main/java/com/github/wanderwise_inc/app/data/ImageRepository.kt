package com.github.wanderwise_inc.app.data

import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.flow.Flow

/** @brief data source for fetching and retrieving images */
interface ImageRepository {
  /** @return a Uri flow of queried image */
  fun fetchImage(fileName: String): Flow<Uri?>

  /** @param fileName the fileName (path) that we want to store to the storage */
  suspend fun uploadImageToStorage(fileName: String): Boolean

  /** @brief used to launch the activity to open the photo gallery */
  fun launchActivity(it: Intent)

  /** @brief set the onImageSelected listener that will be used when setting the current file */
  fun setOnImageSelectedListener(listener: (Uri?) -> Unit)

  /**
   * @brief set the currentFile to this the Uri given, useful for other functions that will use the
   *   currentFile. Also calls the onImageSelected listener
   */
  fun setCurrentFile(uri: Uri?)

  /** @return the currentFile */
  fun getCurrentFile(): Uri?

  /** @return true if the currentFile is an itinerary image, false if it is a profile image */
  fun getIsItineraryImage(): Boolean

  /** @brief set the currentFile to be an itinerary image or a profile image */
  fun setIsItineraryImage(isItineraryImage: Boolean)

  /** @brief delete the image at fileName path from the storage Firebase */
  fun deleteImageFromStorage(fileName: String)
}
