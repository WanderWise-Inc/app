package com.github.wanderwise_inc.app.data

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.github.wanderwise_inc.app.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * @brief data source for fetching and retrieving images
 */
interface ImageRepository {
    /**
     * @param imageUri the queried resource's URI
     * @return an image with given `uri` from the data source and returns its bitmap as a Flow
     */
    fun fetchImage(imageUri: Uri?): Flow<Bitmap>
}

/**
 * test image repository... Always returns the same static image resource
 */
class ImageRepositoryTestImpl(private val application: Application) :ImageRepository {
    override fun fetchImage(imageUri: Uri?): Flow<Bitmap> {
        return flow {
            // that app logo image from bootcamp. Doesn't matter much at this point in time
            val bitmap = BitmapFactory.decodeResource(application.applicationContext.resources,
                R.drawable.app_logo_figma)
            emit(bitmap)
        }
    }
}
