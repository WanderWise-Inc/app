package com.github.wanderwise_inc.app.ui.overview

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.content.ContentResolver
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContentProviderCompat.requireContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.github.wanderwise_inc.app.model.user.User
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.github.wanderwise_inc.app.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL

@Composable
fun OverviewScreen(userViewModel: UserViewModel, context: Context) {
    val coroutineScope = rememberCoroutineScope()
    val storage = FirebaseStorage.getInstance()
    Column {
        Text(text = "Welcome to the overview screen", modifier = Modifier.testTag("Overview screen"))
        val profilePicture = userViewModel.getUserProfilePicture()
        if (profilePicture != null) {
            Log.d("STORAGE", profilePicture.toString())
            AsyncImage(
                model = profilePicture,
                contentDescription = "profile picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )


/*            coroutineScope.launch {
                val storageRef = storage.reference
                val testRef = storageRef.child("images/${userViewModel.getUserId()}")
                val bitMap = getBitMap(context, profilePicture)
                val baos = ByteArrayOutputStream()
                bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                var uploadTask = testRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                    Log.d("STORAGE", "FAIL ON UPLOAD")
                }.addOnSuccessListener { taskSnapshot ->
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    // ...
                    Log.d("STORAGE", "SUCCESS ON UPLOAD")
                }

            }*/
            val storageRef = storage.reference
            val bitMapState = remember {mutableStateOf<Bitmap?>(null)}
            val profilePictureRef = storageRef.child("images/${userViewModel.getUserId()}")
            profilePictureRef.getBytes(1024*1024)
                .addOnSuccessListener {
                    val bitMap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    bitMapState.value = bitMap

                }
                .addOnFailureListener { e ->
                    Log.w("BITMAP", e)
                }

            if (bitMapState.value != null) {
                Image(
                    painter = BitmapPainter(bitMapState.value!!.asImageBitmap()),
                    contentDescription = null)
            }
        }
        Button(onClick = { /*TODO*/ }) {
            Text("Change Username")
        }
        Button(onClick = { /*TODO*/ }) {
            Text("Change PhoneNumber")
        }
    }

}

suspend fun getBitMap(context: Context, url : Uri) : Bitmap {
    val loading = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(url)
        .build()

    Log.d("STORAGE", "REQUEST OK")
    val result = (loading.execute(request) as SuccessResult).drawable
    return (result as BitmapDrawable).bitmap
}
