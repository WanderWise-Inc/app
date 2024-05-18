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

  private var currentFile: Uri? = uri
    private var onImageSelected: ((Uri?) -> Unit)? = null

  /**
   * Fetch image Function. This function will fetch the profile picture from the Storage at a given
   * path
   *
   * @return a flow of the bitMap representation of the profile picture
   */
  override fun fetchImage(pathToProfilePic: String): Flow<Uri?> {
    Log.d("FETCH IMAGE COUNTER", "FETCHING IMAGE")
    return flow {
          if (pathToProfilePic.isBlank()) {
            // the path is empty, there should be no profilePicture at this path
            emit(null)
          } else {
            val profilePictureRef = imageReference.child("images/${pathToProfilePic}")

            // the byte array that is at the given path (if any)
            val uriResult =
                suspendCancellableCoroutine<Uri?> { continuation ->
                  profilePictureRef.downloadUrl
                      .addOnSuccessListener { result ->
                        Log.d("FETCH IMAGE", "FETCH SUCCESS")
                        continuation.resume(result) // Resume with byte array
                      }
                      .addOnFailureListener { exception ->
                        Log.w("FETCH IMAGE", exception)
                        continuation.resumeWithException(exception) // Resume with exception
                      }
                }
            emit(uriResult) // Emit bitmap
          }
        }
        .catch {
          Log.w("FETCH IMAGE", it)
          emit(null)
        }
  }

  /**
   * Upload to the storage. This function will upload the currentFile (which will be the one
   * selected by the user in the photo gallery)
   *
   * @param fileName the fileName (path) where we want to store the currentFile
   */
  override suspend fun uploadImageToStorage(fileName: String): Boolean {
    if (fileName == "") {
      throw Exception("fileName is empty")
    }
    val result =
        suspendCancellableCoroutine<Boolean> { continuation ->
          currentFile?.let {
            imageReference
                .child("images/${fileName}")
                .putFile(it)
                .addOnSuccessListener {
                  Log.d("STORE IMAGE", "STORE SUCCESS")
                  continuation.resume(true)
                }
                .addOnFailureListener { error ->
                  Log.w("STORE IMAGE", error)
                  continuation.resume(false)
                }
          }
              ?: run {
                Log.w("STORE IMAGE", "currentFile is null")
                continuation.resume(false)
              }
        }
    return result
  }

  /** @brief used to launch the activity to open the photo gallery */
  override fun launchActivity(it: Intent) {
    activityLauncher.launch(it)
  }

  /**
   * @brief set the currentFile to this the Uri given, useful for other functions that will use the
   *   currentFile
   */
  override fun setCurrentFile(uri: Uri?) {
    currentFile = uri
      onImageSelected?.invoke(uri)
  }

    override fun setOnImageSelectedListener(listener: (Uri?) -> Unit) {
        onImageSelected = listener
    }

  /** @return the currentFile */
  override fun getCurrentFile(): Uri? {
    return currentFile
  }
}
