package com.github.wanderwise_inc.app.ui.profile

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.compose.animation.expandHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.profile.Profile
import com.github.wanderwise_inc.app.ui.home.SearchBar
import com.github.wanderwise_inc.app.ui.itinerary.ItineraryBanner
import com.github.wanderwise_inc.app.ui.navigation.Route
import com.github.wanderwise_inc.app.ui.navigation.TopNavigationMenu
import com.github.wanderwise_inc.app.ui.navigation.graph.LikedNavGraph
import com.github.wanderwise_inc.app.ui.theme.WanderWiseTheme
import com.github.wanderwise_inc.app.viewmodel.MapViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.firebase.auth.FirebaseAuth

const val PROFILE_SCREEN_TEST_TAG: String = "profile_screen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(mapViewModel: MapViewModel, profileViewModel: ProfileViewModel) {
  val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
  val profile by profileViewModel.getProfile(currentUid).collectAsState(initial = null)
  WanderWiseTheme {
    if (profile != null) {
      androidx.compose.material.Scaffold(
        /*Top bar composable*/
        topBar = {
          TopAppBar(
            title = {Text("Profile")},
            actions= {
              FloatingActionButton(onClick = { /*Go to Edit Profile Screen*/ }) {
                Image(painter = painterResource(id = R.drawable.settings_icon), contentDescription = "Edit Profile", modifier = Modifier
                  .requiredWidth(35.dp)
                  .requiredHeight(35.dp))
              }
            },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onSecondary
          ))
        },
        modifier = Modifier
          .fillMaxSize(),
      ) { innerPadding ->
        Box(modifier = Modifier
          .padding(innerPadding)
          .fillMaxSize(),
          contentAlignment =  Alignment.TopCenter) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            profilePicture(profileViewModel, profile!!, modifier=Modifier.padding(20.dp))
            username(profile!!)
            WanderScore(profile!!)
          }
        }
      }
    }
  }
}



@Composable
fun profilePicture(profileViewModel: ProfileViewModel, profile: Profile, modifier:Modifier) {
  val picture by profileViewModel.getProfilePicture(profile).collectAsState(initial = null)

  if (picture != null) {
    Image(
        painter = BitmapPainter(picture!!.asImageBitmap()), contentDescription = "Profile picture", modifier= Modifier
        .requiredWidth(100.dp)
        .requiredHeight(100.dp))
  } else {
    Text("No Picture")
  }
}

@Composable
fun username(profile: Profile) {

    Text(text = "${profile.displayName}", modifier = Modifier
      .padding(8.dp),
      style = MaterialTheme.typography.headlineSmall)
}

@Composable
fun WanderScore(profile: Profile) {
  Box(modifier=Modifier.background(MaterialTheme.colorScheme.secondary)){
    Text(text = "WanderScore: Not Implemented Yet")
  }
}

//@Composable
//fun ItinerariesScrollable(mapViewModel: MapViewModel, uid: String) {
//  val itineraries by mapViewModel.getUserItineraries(uid).collectAsState(initial = emptyList())
//  if (itineraries.isNotEmpty()) {
//    Column {
//      Text("Your Itineraries:")
//      itineraries.forEach { itinerary -> ItineraryBanner(itinerary = itinerary) }
//    }
//  } else {
//    Text("You have not created any itineraries yet.")
//  }
//}

@Composable
fun ItineraryList(itineraries: List<Itinerary>) {
  LazyColumn {
    items(itineraries.count()) { it ->
        ItineraryCard()
    }
  }
}
@Composable
fun ItineraryCard() {
  Card(modifier = Modifier.padding(8.dp))/*onClick = { TODO* OPEN PREVIEW OF ITINERARY}*/
  {
    Column {
      Text("Itinerary Name")
      Text("Itinerary Description")
    }
  }

}



