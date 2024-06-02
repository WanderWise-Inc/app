package com.github.wanderwise_inc.app.data

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.google.firebase.storage.StorageReference
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine

/** A class implementation of the Image Repository */
class ImageRepositoryImpl(
    private val activityLauncher: ActivityResultLauncher<Intent>,
    private val imageReference: StorageReference,
    uri: Uri?
) : ImageRepository {

  private var currentFileItinerary: Uri? = uri
  private var currentFileProfile: Uri? = uri
  private var isItineraryImage = false
  private var currentFile: Uri? = uri
  private var onImageSelected: ((Uri?) -> Unit)? = null

  /**
   * Fetch image Function. This function will fetch the picture from the Storage at a given path
   *
   * @param fileName the path of the picture in the storage
   * @return a flow of the Uri representation of the picture
   */
  override fun fetchImage(fileName: String): Flow<Uri?> {
    return flow {
          if (fileName.isBlank()) {
            // the path is empty, there should be no picture at this path
            emit(null)
          } else {
            val pictureRef = imageReference.child("images/${fileName}")

            // Get the Uri from storage
            val uriResult =
                suspendCancellableCoroutine<Uri?> { continuation ->
                  pictureRef.downloadUrl
                      .addOnSuccessListener { result ->
                        Log.d("FETCH IMAGE", "Download Success")
                        continuation.resume(result)
                      }
                      .addOnFailureListener { exception ->
                        Log.w("FETCH IMAGE", exception)
                        continuation.resumeWithException(exception)
                      }
                }
            emit(uriResult)
          }
        }
        .catch {
          // If there is an error, we emit null
          Log.d("FETCH IMAGE", "Error fetching image")
          emit(null)
        }
  }

  /**
   * Upload to the storage. This function will upload the currentFile (which will be the one
   * selected by the user in the photo gallery)
   *
   * @param fileName the fileName (path) where we want to store the currentFile
   * @return true if the upload was successful, false otherwise
   */
  override suspend fun uploadImageToStorage(fileName: String): Boolean {
    if (fileName == "") {
      throw Exception("fileName is empty")
    }
    val result =
        suspendCancellableCoroutine<Boolean> { continuation ->
          // choose the currentFile to upload between the itinerary image and the profile image
          currentFile = if (isItineraryImage) currentFileItinerary else currentFileProfile
          currentFile?.let {
            imageReference
                .child("images/${fileName}")
                .putFile(it)
                .addOnSuccessListener {
                  Log.d("UPLOAD IMAGE", "Upload Success")
                  continuation.resume(true)
                }
                .addOnFailureListener { error ->
                  Log.w("UPLOAD IMAGE", error)
                  continuation.resume(false)
                }
          }
              // If there is no currentFile, then we can't upload anything
              ?: run {
                Log.d("UPLOAD IMAGE", "No image to upload")
                continuation.resume(false)
              }
        }
    return result
  }

  /**
   * @param it the intent to launch the activity
   * @brief used to launch the activity to open the photo gallery
   */
  override fun launchActivity(it: Intent) {
    activityLauncher.launch(it)
  }

  /**
   * @param uri the uri of the currentFile
   * @brief set the currentFile to this the Uri given, useful for other functions that will use the
   *   currentFile
   */
  override fun setCurrentFile(uri: Uri?) {
    // Choice between the itinerary image and the profile image
    if (isItineraryImage) {
      currentFileItinerary = uri
    } else {
      currentFileProfile = uri
    }

    // Call the onImageSelected listener to notify that the currentFile has been set
    onImageSelected?.invoke(uri)
  }

  /**
   * @param listener the listener to set the currentFile
   * @brief set the onImageSelected listener that will be used when setting the current file
   */
  override fun setOnImageSelectedListener(listener: (Uri?) -> Unit) {
    onImageSelected = listener
  }

  /** @return the currentFile */
  override fun getCurrentFile(): Uri? {
    return if (isItineraryImage) {
      currentFileItinerary
    } else {
      currentFileProfile
    }
  }

  /** @return true if the currentFile is an itinerary image, false if it is a profile image */
  override fun getIsItineraryImage(): Boolean {
    return isItineraryImage
  }

  /**
   * @param isItineraryImageType true if the currentFile is an itinerary image, false if it is a
   *   profile image
   * @brief set the currentFile to be an itinerary image or a profile image
   */
  override fun setIsItineraryImage(isItineraryImageType: Boolean) {
    isItineraryImage = isItineraryImageType
  }

  /**
   * @param fileName the path of the image to delete
   * @brief delete the image at fileName path from the storage Firebase
   */
  override fun deleteImageFromStorage(fileName: String) {
    imageReference
        .child("images/${fileName}")
        .delete()
        .addOnSuccessListener { Log.d("DELETE IMAGE", "DELETE SUCCESS") }
        .addOnFailureListener { error -> Log.w("DELETE IMAGE", error) }
  }
}
