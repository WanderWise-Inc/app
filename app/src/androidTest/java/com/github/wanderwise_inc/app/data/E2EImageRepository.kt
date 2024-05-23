package com.github.wanderwise_inc.app.data

import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class E2EImageRepository : ImageRepository {
  override fun fetchImage(pathToProfilePic: String): Flow<Uri?> {
    return flow { emit(null) }
  }

  override suspend fun uploadImageToStorage(fileName: String): Boolean {
    return true
  }

  override fun launchActivity(it: Intent) {
    TODO("Not yet implemented")
  }

  override fun setOnImageSelectedListener(listener: (Uri?) -> Unit) {
    TODO("Not yet implemented")
  }

  override fun setCurrentFile(uri: Uri?) {
    TODO("Not yet implemented")
  }

  override fun getCurrentFile(): Uri? {
    TODO("Not yet implemented")
  }

  override fun getIsItineraryImage(): Boolean {
    TODO("Not yet implemented")
  }

  override fun setIsItineraryImage(isItineraryImage: Boolean) {
    TODO("Not yet implemented")
  }

  override fun deleteImageFromStorage(fileName: String) {
    TODO("Not yet implemented")
  }
}
