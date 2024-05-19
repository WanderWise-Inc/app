package com.github.wanderwise_inc.app.ui.signin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.github.wanderwise_inc.app.R
import com.github.wanderwise_inc.app.ui.TestTags
import com.github.wanderwise_inc.app.ui.navigation.graph.Graph
import com.github.wanderwise_inc.app.viewmodel.LoginViewModel
import com.github.wanderwise_inc.app.viewmodel.SignInState

@Composable
fun LoginScreen(loginViewModel: LoginViewModel, navController: NavController) {
  val signInState by loginViewModel.signInState.observeAsState()

  LaunchedEffect(signInState) {
    if (signInState == SignInState.SUCCESS) {
      navController.navigate(Graph.HOME)
    }
  }

  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp)) {
        Text(
            text = "You can either wander dumb or,",
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            style =
                TextStyle(
                    fontSize = 22.sp, fontWeight = FontWeight.Medium, fontStyle = FontStyle.Italic),
            modifier = Modifier.padding(top = 16.dp).offset(y = 60.dp))

        Text(
            text = "WanderWise",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(top = 8.dp).offset(y = 60.dp))

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.logo_swent),
            contentDescription = "WanderWise logo",
            modifier = Modifier.size(275.dp).align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Start Wandering Now",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.offset(y = (-60).dp))

        SignInButton(loginViewModel)
      }
}

@Composable
fun SignInButton(loginViewModel: LoginViewModel) {
  Button(
      onClick = { loginViewModel.signIn() },
      shape = RoundedCornerShape(16.dp),
      border = BorderStroke(1.dp, Color.Black),
      colors = ButtonDefaults.buttonColors(Color.Transparent),
      modifier = Modifier.padding(bottom = 16.dp).testTag(TestTags.SIGN_IN_BUTTON)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Image(
              painter = painterResource(id = R.drawable.google__g__logo_svg),
              contentDescription = "Google logo",
              modifier = Modifier.size(17.dp))

          Spacer(modifier = Modifier.width(8.dp))

          Text(
              text = "Sign-In with Google",
              color = Color.Black,
              style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium))
        }
      }
}
