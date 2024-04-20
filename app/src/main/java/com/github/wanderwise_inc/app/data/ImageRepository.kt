package com.github.wanderwise_inc.app.data

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
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
import java.io.ByteArrayOutputStream

/** @brief data source for fetching and retrieving images */
interface ImageRepository {
  /**
   * @param imageUri the queried image's URI
   * @return a bitmap flow of queried image
   */
  fun fetchImage(imageUri: Uri?): Flow<Bitmap?>

  /**
   * @param imageUri the image's URI we want to store
   * @return a boolean, true if the operation succeed, false if an error occurred
   */
  fun storeImage(bitMap: Bitmap)

  fun getBitMap(url: Uri?) : Flow<Bitmap?>
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

class ImageRepositoryImpl(private val application: Application) : ImageRepository {
  private val storage = FirebaseStorage.getInstance()
  private val context = application.baseContext

  override fun storeImage(bitMap : Bitmap) {
      val currentUser = FirebaseAuth.getInstance()
      val storageRef = storage.reference
      val userProfilePictureRef = storageRef.child("images/profilePicture/${currentUser.uid}")
      val baos = ByteArrayOutputStream()
      bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
      val data = baos.toByteArray()

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

  override fun fetchImage(imageUri: Uri?): Flow<Bitmap?> {

    return flow {
      val storageRef = storage.reference
      val currentUser = FirebaseAuth.getInstance()
      val profilePictureRef = storageRef.child("images/profilePicture/${currentUser.uid}")
      var bitMap : Bitmap? = null
      profilePictureRef.getBytes(1024*1024)
        .addOnSuccessListener {
          bitMap = BitmapFactory.decodeByteArray(it, 0, it.size)
        }
        .addOnFailureListener {
          Log.w("BITMAP", it)
        }
      Log.d("BITMAP", "BITMAP VAL IS " + bitMap.toString())
      emit(bitMap)
    }
  }

    override fun getBitMap(url : Uri?) : Flow<Bitmap?> {
        return flow {
            if (url != null) {

                val loading = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .build()

                Log.d("STORAGE", "REQUEST OK")
                val result = (loading.execute(request) as SuccessResult).drawable
                emit((result as BitmapDrawable).bitmap)
            } else {
                emit(null)
            }
        }
  }
}
