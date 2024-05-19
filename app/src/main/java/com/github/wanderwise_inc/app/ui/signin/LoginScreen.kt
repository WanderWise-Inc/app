package com.github.wanderwise_inc.app.ui.signin

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.data.GoogleSignInLauncher
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(googleSignInLauncher: GoogleSignInLauncher) {
  val imageList =
      listOf(
          R.drawable.buddah,
          R.drawable.street,
          R.drawable.venice,
          R.drawable.waterfall,
          R.drawable.cathedral)
  var currentImageIndex by remember { mutableStateOf(0) }
  val infiniteTransition = rememberInfiniteTransition()
  val alpha by
      infiniteTransition.animateFloat(
          initialValue = 0.6f,
          targetValue = 0.6f,
          animationSpec =
              infiniteRepeatable(
                  animation = tween(5000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
          label = "")

  LaunchedEffect(Unit) {
    while (true) {
      delay(5000) // Change the image every 5 seconds
      currentImageIndex = (currentImageIndex + 1) % imageList.size
    }
  }

  MaterialTheme(
      colorScheme =
          lightColorScheme(
              primary = Color(0xFFB0E0E6), // seafoam Green
              secondary = Color(0xFFF88379), // sunset Coral
              onPrimary = Color.Black,
              onSecondary = Color.White,
              background = Color(0xFFE6E6FA), // pastel Lavender
              surface = Color(0xFFE6CEA8), // warm Sand
              onBackground = Color.DarkGray,
              onSurface = Color.Black,
          )) {
        Box(modifier = Modifier.fillMaxSize()) {
          Crossfade(
              targetState = currentImageIndex,
              animationSpec = tween(durationMillis = 2000),
              label = "" // Adjust the duration to control the speed of the crossfade
              ) { index ->
                Image(
                    painter = painterResource(id = imageList[index]),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = alpha)
              }

          // Overlay surface for text and buttons
          Surface(
              modifier = Modifier.fillMaxSize(), color = Color.Transparent // Important for overlay
              ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                      Text(
                          text = "WanderWise",
                          color = MaterialTheme.colorScheme.onSurface,
                          fontWeight = FontWeight.Bold,
                          fontSize = 30.sp,
                          fontFamily =
                              FontFamily(
                                  Font(R.font.dm_sans_regular, FontWeight.Normal),
                                  Font(R.font.dm_sans_medium, FontWeight.Medium),
                                  Font(R.font.dm_sans_bold, FontWeight.Bold)))
                      Spacer(modifier = Modifier.height(8.dp))
                      Text(
                          text = "You can either wander dumb or wander wise",
                          color = MaterialTheme.colorScheme.onBackground,
                          fontSize = 16.sp,
                      )
                      Spacer(modifier = Modifier.height(450.dp))
                      SignInButton(googleSignInLauncher)
                    }
              }
        }
      }
}

@Composable
fun SignInButton(googleSignInLauncher: GoogleSignInLauncher) {
  Button(
      onClick = { googleSignInLauncher.launchSignIn() },
      modifier =
          Modifier.fillMaxWidth() // Changed to fill the maximum width for better visibility
              .height(80.dp) // Ensure the height is constant
              .padding(horizontal = 32.dp, vertical = 16.dp), // Added horizontal padding
      shape = RoundedCornerShape(16.dp),
      colors =
          ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.background, // Ensuring high contrast
              contentColor = MaterialTheme.colorScheme.onPrimary)) {
        Icon(
            painter = painterResource(R.drawable.google__g__logo_svg),
            contentDescription = "Google Logo",
            modifier = Modifier.size(24.dp).padding(end = 8.dp))
        Text("Sign In with Google", style = MaterialTheme.typography.bodyLarge)
      }
}
