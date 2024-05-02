package com.github.wanderwise_inc.app.ui.signin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.data.GoogleSignInLauncher

@Composable
fun LoginScreen(googleSignInLauncher: GoogleSignInLauncher, modifier: Modifier = Modifier) {
  Box(modifier = modifier
      .requiredWidth(width = 1280.dp)
      .requiredHeight(height = 1100.dp)) {

    Image(
        painter = painterResource(id = R.drawable.logo_swent),
        contentDescription = "new_logo_swent 1",
        modifier =
        Modifier
            .size(width = 5.dp, height = 5.dp)
            .offset(x = 637.dp, y = 500.dp)
            .requiredWidth(width = 400.dp)
            .requiredHeight(height = 400.dp)
            .alpha(1f))
    Box(
        modifier =
        Modifier
            .align(alignment = Alignment.Center)
            .offset(x = 0.dp, y = 300.dp)
            .requiredWidth(width = 289.dp)
            .requiredHeight(height = 39.dp)
            .clip(shape = RoundedCornerShape(16.dp))
            .background(color = Color(0xFFE6F0FE))) {
          SignInButton(googleSignInLauncher)
        }
    Image(
        painter = painterResource(id = R.drawable.google__g__logo_svg),
        contentDescription = "google-logo-9808 1",
        modifier =
        Modifier
            .align(alignment = Alignment.TopStart)
            .offset(x = 510.dp, y = 841.dp)
            .requiredSize(size = 17.dp))
    Text(
        text = "Sign-In with Google",
        color = Color.Black,
        lineHeight = 1.em,
        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
        modifier =
        Modifier
            .align(alignment = Alignment.TopCenter)
            .offset(x = 50.dp, y = 840.dp)
            .requiredWidth(width = 250.dp)
            .requiredHeight(height = 35.dp))
    Text(
        text = "Start Wandering Now",
        color = Color.Black,
        lineHeight = 0.10.em,
        style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Medium),
        modifier = Modifier
            .align(alignment = Alignment.Center)
            .offset(x = 0.dp, y = 220.dp))
      Text(
          text = "You can either wander dumb or,",
          color = Color.Black,
          style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Medium, fontStyle = FontStyle.Italic),
          modifier = Modifier
              .align(alignment = Alignment.Center)
              .offset(x = 0.dp, y = (-300).dp)
      )
      Text(
          text = "WanderWise",
          color = Color.Black,
          style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Medium),
          modifier = Modifier
              .align(alignment = Alignment.Center)
              .offset(x = 0.dp, y = (-255).dp)
      )
  }
}

@Composable
fun SignInButton(googleSignInLauncher: GoogleSignInLauncher) {
  Button(
      onClick = { googleSignInLauncher.launchSignIn() },
      modifier =
      Modifier
          .requiredWidth(width = 289.dp)
          .requiredHeight(height = 39.dp)
          .testTag("Sign in button"),
      shape = RoundedCornerShape(16.dp),
      border = BorderStroke(1.dp, Color.Black),
      colors = ButtonDefaults.buttonColors(Color.Transparent)) {}
}
