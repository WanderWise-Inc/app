package com.github.wanderwise_inc.app.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.data.ImageRepository
import com.github.wanderwise_inc.app.model.location.Itinerary
import com.github.wanderwise_inc.app.model.location.Location
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.navigation.Destination
import com.github.wanderwise_inc.app.ui.navigation.NavigationActions
import com.github.wanderwise_inc.app.ui.profile.ProfilePicture
import com.github.wanderwise_inc.app.viewmodel.ItineraryViewModel
import com.github.wanderwise_inc.app.viewmodel.ProfileViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

/** @brief previews an itinerary */
@Composable
fun PreviewItineraryScreen(
    itineraryViewModel: ItineraryViewModel,
    profileViewModel: ProfileViewModel,
    imageRepository: ImageRepository,
    navController: NavHostController,
    creationMode: Boolean = false
) {
  val userLocation by itineraryViewModel.getUserLocation().collectAsState(null)
  val itinerary = itineraryViewModel.getFocusedItinerary()
  val context = LocalContext.current

  val followItinerary = { itineraryViewModel.followItineraryOnGoogleMaps(context, itinerary!!) }

  if (itinerary == null) {
    NullItinerary(userLocation)
  } else {
    val cameraPositionState =
        rememberCameraPositionState(key = itinerary.toString()) {
          position =
              CameraPosition.fromLatLngZoom(
                  itinerary.computeCenterOfGravity().toLatLng(),
                  itinerary.computeOptimalZoomLevel())
        }

    LaunchedEffect(Unit) { itineraryViewModel.fetchPolylineLocations(itinerary) }
    val polylinePoints by itineraryViewModel.getPolylinePointsLiveData().observeAsState()
    var isMinimized by remember { mutableStateOf(false) }
    val onMinimizedClick = { isMinimized = !isMinimized }

    Scaffold(
        bottomBar = {
          PreviewItineraryBanner(
              isMinimized,
              onMinimizedClick,
              itinerary,
              itineraryViewModel,
              profileViewModel,
              imageRepository,
              navController,
              creationMode)
        },
        modifier = Modifier.testTag(TestTags.MAP_PREVIEW_ITINERARY_SCREEN),
        floatingActionButton = {
          CenterButton(
              cameraPositionState,
              userLocation,
              itinerary,
          )
        },
        floatingActionButtonPosition = FabPosition.Start) { paddingValues ->
          Box {
            GoogleMap(
                modifier =
                    Modifier.fillMaxSize().padding(paddingValues).testTag(TestTags.MAP_GOOGLE_MAPS),
                cameraPositionState = cameraPositionState) {
                  userLocation?.let {
                    Marker(
                        tag = TestTags.MAP_USER_LOCATION,
                        state = MarkerState(position = it.toLatLng()),
                        icon =
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_AZURE))
                  }
                  itinerary.locations.map { location ->
                    AdvancedMarker(
                        state = MarkerState(position = location.toLatLng()),
                        title = location.title,
                    )
                    if (polylinePoints != null) {
                      Polyline(points = polylinePoints!!, color = MaterialTheme.colorScheme.primary)
                    }
                  }
                }
            FollowItineraryButton(
                followItinerary,
                Modifier.align(
                    Alignment
                        .TopCenter) // this parameter allows alignment wrt containing Box composable
                )
          }
        }
  }
}

/** @brief: Button which launches google maps with specified itinerary once pressed */
@Composable
fun FollowItineraryButton(followItinerary: () -> Unit, modifier: Modifier) {
  ExtendedFloatingActionButton(
      onClick = { followItinerary() },
      icon = {
        Icon(
            Icons.AutoMirrored.Filled.DirectionsWalk,
            contentDescription = "follow",
            modifier = Modifier.size(32.dp))
      },
      text = { Text(text = "Follow", color = Color.DarkGray) },
      containerColor = MaterialTheme.colorScheme.secondaryContainer,
      contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
      modifier = modifier.padding(12.dp).testTag(TestTags.START_NEW_ITINERARY_STARTING))
}

/**
 * @brief displayed beneath `Maps` composable in `PreviewItineraryScreen`. Variable height with
 *   different modes depending on whether it is minimized or maximized
 */
@Composable
fun PreviewItineraryBanner(
    isMinimized: Boolean,
    onMinimizedClick: () -> Unit,
    itinerary: Itinerary,
    itineraryViewModel: ItineraryViewModel,
    profileViewModel: ProfileViewModel,
    imageRepository: ImageRepository,
    navController: NavHostController,
    creationMode: Boolean
) {

  if (isMinimized)
      PreviewItineraryBannerMinimized(onMinimizedClick = onMinimizedClick, itinerary = itinerary)
  else
      PreviewItineraryBannerMaximized(
          onMinimizedClick = onMinimizedClick,
          itinerary = itinerary,
          itineraryViewModel = itineraryViewModel,
          profileViewModel = profileViewModel,
          imageRepository = imageRepository,
          navController = navController,
          creationMode = creationMode)
}

@Composable
private fun PreviewItineraryBannerMaximized(
    onMinimizedClick: () -> Unit,
    itinerary: Itinerary,
    itineraryViewModel: ItineraryViewModel,
    profileViewModel: ProfileViewModel,
    imageRepository: ImageRepository,
    navController: NavHostController,
    creationMode: Boolean
) {
  val titleFontSize = 32.sp
  val innerFontSize = 16.sp
  val ctr = remember { mutableIntStateOf(0) }

  val profilePictureModifier =
      Modifier.clip(RoundedCornerShape(5.dp)).size(50.dp).testTag(TestTags.MAP_PROFILE_PIC)

  val profile by profileViewModel.getProfile(itinerary.userUid).collectAsState(initial = null)

  Card(
      colors =
          CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
          ),
      modifier = Modifier.testTag(TestTags.MAP_MAXIMIZED_BANNER)) {
        val scrollState = rememberScrollState()
        Column(
            modifier =
                Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .padding(30.dp)
                    .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
          Row {
            // minimize button
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "minimize_button",
                modifier =
                    Modifier.clickable { onMinimizedClick() }.testTag(TestTags.MAP_BANNER_BUTTON))
            // Itinerary Title
            Text(
                text = itinerary.title,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
                fontSize = titleFontSize,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(2.dp).testTag(TestTags.MAP_ITINERARY_TITLE),
                textAlign = TextAlign.Center)
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically) {

                // NUMBER OF LIKES
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                          painter = painterResource(id = R.drawable.liked_icon),
                          contentDescription = "likes",
                          tint = MaterialTheme.colorScheme.secondary,
                          modifier = Modifier.size(width = 20.dp, height = 20.dp))
                      Text(
                          text = "${itinerary.numLikes}",
                          color = MaterialTheme.colorScheme.secondary,
                          fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                          fontSize = innerFontSize,
                          fontWeight = FontWeight.Normal,
                          modifier = Modifier.padding(5.dp),
                          textAlign = TextAlign.Center,
                      )
                    }

                Spacer(modifier = Modifier.width(15.dp))

                // DURATION
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                          painter = painterResource(id = R.drawable.hourglass),
                          contentDescription = "duration",
                          tint = MaterialTheme.colorScheme.secondary,
                          modifier = Modifier.size(width = 25.dp, height = 25.dp))
                      Text(
                          text = "${itinerary.time} hours",
                          color = MaterialTheme.colorScheme.secondary,
                          fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                          fontSize = innerFontSize,
                          fontWeight = FontWeight.Normal,
                          modifier = Modifier.padding(3.dp),
                          textAlign = TextAlign.Center,
                      )
                    }

                Spacer(modifier = Modifier.width(15.dp))

                // COST
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                          painter = painterResource(id = R.drawable.money),
                          contentDescription = "cost",
                          tint = MaterialTheme.colorScheme.secondary,
                          modifier = Modifier.size(width = 20.dp, height = 20.dp))
                      Text(
                          text = "${itinerary.price.toInt()} \$",
                          color = MaterialTheme.colorScheme.secondary,
                          fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                          fontSize = innerFontSize,
                          fontWeight = FontWeight.Normal,
                          modifier = Modifier.padding(3.dp),
                          textAlign = TextAlign.Center,
                      )
                    }
              }

          Spacer(modifier = Modifier.height(20.dp))

          // Profile details
          Row(
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.height(50.dp),
          ) {
            ProfilePicture(
                profile = profile,
                profileViewModel = profileViewModel,
                modifier = profilePictureModifier,
                ctr = ctr)

            Spacer(modifier = Modifier.width(10.dp))

            // NAME + local WanderGuide label
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {
              Text(
                  text = profile?.displayName ?: "John Doe",
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                  fontSize = innerFontSize,
                  fontWeight = FontWeight.SemiBold,
                  // modifier = Modifier.padding(2.dp),
                  textAlign = TextAlign.Center,
              )

              Text(
                  text = "Local WanderGuide",
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                  fontSize = innerFontSize,
                  fontWeight = FontWeight.Light,
                  modifier = Modifier.padding(2.dp).fillMaxHeight(),
                  textAlign = TextAlign.Center,
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Itinerary Description
          Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.Top,
          ) {
            Text(
                text = itinerary.description ?: "Looks like this Wander has no description!",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(2.dp).testTag(TestTags.MAP_ITINERARY_DESCRIPTION),
                textAlign = TextAlign.Center)
          }
          Spacer(modifier = Modifier.height(20.dp))
          val coroutineScope = rememberCoroutineScope()
          val uid = profileViewModel.getUserUid()
          if (uid == itinerary.userUid && !creationMode) {
            Button(
                onClick = {
                  coroutineScope.launch {
                    imageRepository.deleteImageFromStorage("itineraryPictures/${itinerary.uid}")
                    itineraryViewModel.setFocusedItinerary(null)
                    itineraryViewModel.deleteItinerary(itinerary)
                  }
                  NavigationActions(navController)
                      .navigateTo(Destination.TopLevelDestination.Profile)
                },
                colors =
                    ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.testTag(TestTags.MAP_DELETE_ITINERARY_BUTTON)
                // contentColor = MaterialTheme.colorScheme.onErrorContainer
                ) {
                  Text(text = "Delete Itinerary")
                }
          }
        }
      }
}

@Composable
private fun PreviewItineraryBannerMinimized(onMinimizedClick: () -> Unit, itinerary: Itinerary) {
  val titleFontSize = 32.sp

  Card(
      colors =
          CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
          ),
      modifier = Modifier.testTag(TestTags.MAP_MINIMIZED_BANNER)) {
        Column(
            modifier =
                Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                    .fillMaxWidth()
                    .aspectRatio(3f)
                    .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Row {
            // minimize button
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = "minimize_button",
                modifier =
                    Modifier.clickable { onMinimizedClick() }.testTag(TestTags.MAP_BANNER_BUTTON))
            // Itinerary Title
            Text(
                text = itinerary.title,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontFamily = MaterialTheme.typography.displayLarge.fontFamily,
                fontSize = titleFontSize,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(2.dp).testTag(TestTags.MAP_ITINERARY_TITLE),
                textAlign = TextAlign.Center)
          }
        }
      }
}

/**
 * @brief: Button which switches back and forth between centered on user and centered on itinerary
 */
@Composable
fun CenterButton(
    cameraPositionState: CameraPositionState,
    userLocation: Location?,
    itinerary: Itinerary
) {
  var centeredOnUser by remember { mutableStateOf(false) }

  // pressing on center button changes centerOnUser variable which launches camera movement
  LaunchedEffect(centeredOnUser) {
    try {
      cameraPositionState.move(
          if (centeredOnUser && userLocation != null) {
            val latLng = userLocation.toLatLng()
            CameraUpdateFactory.newLatLngZoom(latLng, 13f)
          } else {
            CameraUpdateFactory.newLatLngZoom(
                itinerary.computeCenterOfGravity().toLatLng(), itinerary.computeOptimalZoomLevel())
          })
    } catch (
        _: NullPointerException) { // This is used when testing, the CameraUpdateFactory is null
      /* Do nothing */
    }
  }

  FloatingActionButton(
      onClick = { centeredOnUser = !centeredOnUser },
      modifier = Modifier.testTag(TestTags.MAP_CENTER_CAMERA_BUTTON),
      containerColor = MaterialTheme.colorScheme.surfaceContainer) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = "Center on Me",
            tint = Color.DarkGray)
      }
}

@Composable
fun NullItinerary(userLocation: Location?) {
  // for now doesn't do anything, would be nice to center on location
  if (userLocation == null) {
    Column(
        modifier =
            Modifier.testTag(TestTags.MAP_NULL_ITINERARY)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
          Text("Loading...", modifier = Modifier.testTag(TestTags.MAP_NULL_ITINERARY))
        }
  } else {
    val userLocationLatLng = userLocation.toLatLng()
    val cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.fromLatLngZoom(userLocationLatLng, 13f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize().testTag(TestTags.MAP_NULL_ITINERARY),
        cameraPositionState = cameraPositionState) {
          Marker(
              tag = TestTags.MAP_USER_LOCATION,
              state = MarkerState(position = userLocationLatLng),
              icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        }
  }
}
