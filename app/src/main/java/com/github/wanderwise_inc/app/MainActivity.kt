package com.github.wanderwise_inc.app

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.github.wanderwise_inc.app.data.ImageRepositoryImpl
//import com.github.wanderwise_inc.app.data.ImageRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ItineraryRepositoryTestImpl
import com.github.wanderwise_inc.app.data.ProfileRepositoryImpl
import com.github.wanderwise_inc.app.data.ProfileRepositoryTestImpl
import com.github.wanderwise_inc.app.ui.navigation.graph.RootNavigationGraph
import com.github.wanderwise_inc.app.ui.theme.WanderWiseTheme
import com.github.wanderwise_inc.app.viewmodel.HomeViewModel
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
  private val homeViewModel by viewModels<HomeViewModel>()
  // private val profileViewModel by viewModels<ProfileViewModel>()

  private val itineraryRepository = ItineraryRepositoryTestImpl()
  private val mapViewModel = MapViewModel(itineraryRepository)
  private lateinit var imageRepository : ImageRepositoryImpl

  // declaration for use of storage
  private var currentFile : Uri? = null
  private var imageReference = FirebaseStorage.getInstance().reference

  private lateinit var profileViewModel: ProfileViewModel
  private var bitMap by mutableStateOf<Bitmap?>(null)

  // private lateinit var analytics : FirebaseAnalytics
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val profileRepository = ProfileRepositoryImpl()
    imageRepository = ImageRepositoryImpl(application)
    profileViewModel = ProfileViewModel(profileRepository, imageRepository)

    setContent {
      WanderWiseTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          Column {
            Text("HELLO WORLD")
            Button(onClick = {
              Intent(Intent.ACTION_GET_CONTENT).also {
                // we will get only images (filter)
                it.type = "image/*"
                imageLauncher.launch(it)
              }

            }) {
              Text(text = "SEARCH IMAGE")
            }

            Button(onClick = {
              uploadImageToStorage("1")
            }) {
              Text(text = "UPLOAD IMAGE")
            }
          }



          // HomeScreen(homeViewModel, mapViewModel)
/*          RootNavigationGraph(
              application.applicationContext,
              homeViewModel = homeViewModel,
              profileViewModel = profileViewModel,
              mapViewModel = mapViewModel,
              navController = rememberNavController())*/
        }
      }
    }

  }
  @Composable
  fun Greeting() {
    Text(text = "HELLO")
  }
  
  @Composable
  fun showPic(bitmap: Bitmap) {
    Image(painter = BitmapPainter(bitmap.asImageBitmap()), contentDescription = null)
  }
  
  suspend fun getBitMap(url : Uri?) : Bitmap? {
    if (url != null) {
      val loading = ImageLoader(this)
      val request = ImageRequest.Builder(this)
        .data(url)
        .build()

      Log.d("STORAGE", "REQUEST OK")
      val result = (loading.execute(request) as SuccessResult).drawable
      return (result as BitmapDrawable).bitmap
    } else {
      Log.d("BITMAP", "URL IS NULL")
      return null
    }
  }


  private val imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
    if (result.resultCode == RESULT_OK) {
      result?.data?.data.let {
        //use the global variable for access
        currentFile = it
        runBlocking {

        }
      }
    }
  }

  private fun uploadImageToStorage(fileName : String) {
    try {
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
}
