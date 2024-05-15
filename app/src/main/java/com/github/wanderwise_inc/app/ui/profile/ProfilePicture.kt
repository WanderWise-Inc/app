package com.github.wanderwise_inc.app.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.flow

/** @brief profile picture composable and coroutine friendly uwu rawr */
@Composable
fun ProfilePicture(profile: Profile?, profileViewModel: ProfileViewModel, modifier: Modifier/*, ctr : MutableState<Int>*/) {
  // calling `remember` limits recomposition to `Profile` changes
  val defaultProfilePictureFlow = remember(profile/*, ctr.value*/) { profileViewModel.getDefaultProfilePicture() }
  val profilePictureFlow =
      remember(profile/*, ctr.value*/) {
        if (profile != null) profileViewModel.getProfilePicture(profile) else flow { emit(null) }
      }

  val defaultProfilePicture by defaultProfilePictureFlow.collectAsState(initial = null)
  val profilePicture by profilePictureFlow.collectAsState(initial = null)

  val painter: Painter =
      if (profilePicture != null) BitmapPainter(profilePicture!!.asImageBitmap())
      else if (defaultProfilePicture != null) BitmapPainter(defaultProfilePicture!!.asImageBitmap())
      else painterResource(id = R.drawable.profile_icon)

  Image(painter = painter, contentDescription = "profile_icon", modifier = modifier)
}
