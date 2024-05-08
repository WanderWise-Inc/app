package com.github.wanderwise_inc.app.data

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

/** @brief data source for fetching and retrieving images */
interface ImageRepository {
  /** @return a bitmap flow of queried image */
  fun fetchImage(pathToProfilePic: String): Flow<Bitmap?>

  /**
   * @param bitMap the image's bitMap we want to store
   * @return a boolean, true if the operation succeed, false if an error occurred
   */
  /*  fun storeImage(bitMap: Bitmap, currentUser: FirebaseAuth)*/

  /**
   * @param uri the current Uri that we want to convert to a bitmap
   * @return a flow of a bitmap, that can be use to be stored or to be displayed
   */
  /*fun getBitMap(uri: Uri?) : Flow<Bitmap?>*/

  /** @param fileName the fileName (path) that we want to store to the storage */
  fun uploadImageToStorage(fileName: String): Boolean

  /** @brief used to launch the activity to open the photo gallery */
  fun launchActivity(it: Intent)

  /**
   * @brief set the currentFile to this the Uri given, useful for other functions that will use the
   *   currentFile
   */
  fun setCurrentFile(uri: Uri?)

  fun getCurrentFile(): Uri?
}
