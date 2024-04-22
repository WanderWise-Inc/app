package com.github.wanderwise_inc.app.data

import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.github.wanderwise_inc.app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/** @brief data source for fetching and retrieving images */
interface ImageRepository {
  /**
   * @return a bitmap flow of queried image
   */
  fun fetchImage(pathToProfilePic : String): Flow<Bitmap?>

  /**
   * @param bitMap the image's bitMap we want to store
   * @return a boolean, true if the operation succeed, false if an error occurred
   */
  fun storeImage(bitMap: Bitmap)

    /**
     * @param uri the current Uri that we want to convert to a bitmap
     * @return a flow of a bitmap, that can be use to be stored or to be displayed
     */
  fun getBitMap(uri: Uri?) : Flow<Bitmap?>

    /**
     * @param fileName the fileName (path) that we want to store to the storage
     */
  fun uploadImageToStorage(fileName : String)

    /**
     * @brief used to launch the activity to open the photo gallery
     */
  fun launchActivity(it : Intent)

    /**
     * @brief set the currentFile to this the Uri given, useful for other functions
     * that will use the currentFile
     */
  fun setCurrentFile(uri: Uri?)
}

/** test image repository... Always returns the same static image resource */
/*class ImageRepositoryTestImpl(private val application: Application) : ImageRepository {
  override fun fetchImage(imageUri: Uri?): Flow<Bitmap> {
    return flow {
      // that app logo image from bootcamp. Doesn't matter much at this point in time
      val bitmap =
          BitmapFactory.decodeResource(
              application.applicationContext.resources, R.drawable.app_logo_figma)
      emit(bitmap)
    }
  }

  override fun storeImage(imageUri: Uri) {
    TODO("Not yet implemented")
  }
}*/

/**
 * A class implementation of the Image Repository
 */
class ImageRepositoryImpl(private val application: Application, private val activityLauncher: ActivityResultLauncher<Intent>) : ImageRepository {

    private val storage = FirebaseStorage.getInstance()
    private val context = application.baseContext
    private var currentFile : Uri? = null
    private var imageReference = storage.reference

    /**
     * Store image function. This function will create a reference in Firebase to store a bitmap
     * as an Image in Storage.
     *
     * @param bitMap a Bitmap that will be stored in the Firebase Storage
     */
    override fun storeImage(bitMap : Bitmap) {
        // We get the uid to create a reference (a path) to the image using the uid
        val currentUser = FirebaseAuth.getInstance()
        val storageRef = storage.reference
        val userProfilePictureRef = storageRef.child("images/profilePicture/${currentUser.uid}")
        val baos = ByteArrayOutputStream()
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        // Here we create the uploadTask to upload the Image into the Storage
        var uploadTask = userProfilePictureRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Log.d("STORAGE", "FAIL ON UPLOAD")
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            Log.d("STORAGE", "SUCCESS ON UPLOAD")
        }
    }

    /**
     * Fetch image Function. This function will fetch the profile picture
     * @return a flow of the bitMap representation of the profile picture
     */
    override fun fetchImage(pathToProfilePic: String): Flow<Bitmap?> {
        return flow {
            val storageRef = storage.reference
            val profilePictureRef = storageRef.child("images/${pathToProfilePic}")

            try {
                Log.d("FETCH IMAGE", "IN TRY")
                val byteResult = suspendCancellableCoroutine<ByteArray?> { continuation ->
                    profilePictureRef.getBytes(1024 * 1024)
                        .addOnSuccessListener { byteResult ->
                            Log.d("FETCH IMAGE", "GOT THE BYTES")
                            continuation.resume(byteResult) // Resume with byte array
                        }
                        .addOnFailureListener { exception ->
                            Log.d("FETCH IMAGE", "FETCH FAILED")
                            Log.w("FETCH IMAGE", exception)
                            continuation.resumeWithException(exception) // Resume with exception
                        }
                }

                Log.d("FETCH IMAGE", "BYTE RESULT IS " + byteResult.toString())

                // Decode bitmap if bytes are not null
                val bitmap = byteResult?.let {
                    BitmapFactory.decodeByteArray(it, 0, it.size)
                }

                Log.d("FETCH IMAGE", "BITMAP IS " + bitmap.toString())

                emit(bitmap) // Emit bitmap
            } catch (e: Exception) {
                Log.e("FETCH IMAGE", "Error fetching image: ${e.message}", e) // Log detailed error message
                emit(null) // Emit null in case of error
            }
        }
    }

    /**
     * Upload to the storage. This function will upload the currentFile
     * (which will be the one selected by the user in the photo gallery)
     * @param fileName the fileName (path) where we want to store the currentFile
     */
    override fun uploadImageToStorage(fileName : String) {
        try {
            // The currentFile is set with the "setCurrentFile()" function
            // This will put the given file (the one selected by the user) to store in storage
            currentFile?.let {
                imageReference.child("images/${fileName}").putFile(it)
                    .addOnSuccessListener {
                        Log.d("STORE IMAGE", "STORE SUCCESS")
                    }
                    .addOnFailureListener {
                        Log.d("STORE IMAGE", "STORE FAILED")
                    }
            }
        } catch (e : Exception) {
            Log.w("STORE IMAGE", e)
        }
    }

    /**
     * Get bit map function. This function will create a Bitmap representation given a Uri
     * @param uri the Uri link to the image
     * @return a flow of the bitMap representation of the image
     */
    override fun getBitMap(uri : Uri?) : Flow<Bitmap?> {
        return flow {
            if (uri != null) {
                val loading = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(uri)
                    .build()

                Log.d("STORAGE", "REQUEST OK")
                val result = (loading.execute(request) as SuccessResult).drawable
                Log.d("BITMAP", "BITMAP IS : " + (result as BitmapDrawable).bitmap.toString())
                emit((result as BitmapDrawable).bitmap)
            } else {
                Log.d("BITMAP", "URL IS NULL")
                emit(null)
            }
        }
    }

    /**
     * @brief used to launch the activity to open the photo gallery
     */
    override fun launchActivity(it : Intent) {
        activityLauncher.launch(it)
    }

    /**
     * @brief set the currentFile to this the Uri given, useful for other functions
     * that will use the currentFile
     */
    override fun setCurrentFile(uri : Uri?) {
        currentFile = uri
    }
}
