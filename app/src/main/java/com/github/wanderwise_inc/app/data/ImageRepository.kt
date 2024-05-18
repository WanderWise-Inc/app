package com.github.wanderwise_inc.app.data

import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.flow.Flow

/** @brief data source for fetching and retrieving images */
interface ImageRepository {
  /** @return a bitmap flow of queried image */
  fun fetchImage(pathToProfilePic: String): Flow<Uri?>

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
  suspend fun uploadImageToStorage(fileName: String): Boolean

  /** @brief used to launch the activity to open the photo gallery */
  fun launchActivity(it: Intent)

  fun setOnImageSelectedListener(listener: (Uri?) -> Unit)

  /**
   * @brief set the currentFile to this the Uri given, useful for other functions that will use the
   *   currentFile
   */
  fun setCurrentFile(uri: Uri?)

  fun getCurrentFile(): Uri?

  fun deleteImageFromStorage(fileName: String)
}
