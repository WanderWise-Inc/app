package com.github.wanderwise_inc.app.ui.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.github.wanderwise_inc.app.ui.navigation.TopLevelRoute
import com.github.wanderwise_inc.app.ui.signin.LoginScreen
import com.github.wanderwise_inc.app.viewmodel.LoginViewModel

fun NavGraphBuilder.authNavGraph(loginViewModel: LoginViewModel, navController: NavController) {
  navigation(
      route = Graph.AUTHENTICATION,
      startDestination = AuthScreen.SignIn.route,
  ) {
    composable(route = AuthScreen.SignIn.route) { LoginScreen(loginViewModel, navController) }
  }
}

sealed class AuthScreen(val route: String) {
  data object SignIn : AuthScreen(route = TopLevelRoute.SIGNIN)
  // data object LogIn: AuthScreen(route = Route.LOGIN)
}
