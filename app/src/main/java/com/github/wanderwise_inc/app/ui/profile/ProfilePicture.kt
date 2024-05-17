package com.github.wanderwise_inc.app.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.flow

/** @brief profile picture composable and coroutine friendly uwu rawr */
@Composable
fun ProfilePicture(profile: Profile?, profileViewModel: ProfileViewModel, modifier: Modifier) {
  // calling `remember` limits recomposition to `Profile` changes
  val profilePictureFlow =
      remember(profile) {
        if (profile != null) profileViewModel.getProfilePicture(profile) else flow { emit(null) }
      }

  val profilePicture by profilePictureFlow.collectAsState(initial = null)
    val model = ImageRequest.Builder(LocalContext.current).data(profilePicture).error(R.drawable.profile_icon).crossfade(500).build()
    val painter = rememberAsyncImagePainter(model = model)
    Image(painter = painter, contentDescription = "profile_icon", modifier = modifier)
    if (painter.state is AsyncImagePainter.State.Loading) {
        CircularProgressIndicator()
    }
}
