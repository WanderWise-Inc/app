package com.github.wanderwise_inc.app.data

import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.flow.Flow

class E2EImageRepository : ImageRepository {
    override fun fetchImage(pathToProfilePic: String): Flow<Uri?> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadImageToStorage(fileName: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun launchActivity(it: Intent) {
        TODO("Not yet implemented")
    }

    override fun setCurrentFile(uri: Uri?) {
        TODO("Not yet implemented")
    }

    override fun getCurrentFile(): Uri? {
        TODO("Not yet implemented")
    }
}