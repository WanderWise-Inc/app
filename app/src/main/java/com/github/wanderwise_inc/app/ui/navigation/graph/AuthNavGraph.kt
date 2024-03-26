package com.github.wanderwise_inc.app.ui.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.github.wanderwise_inc.app.ui.navigation.Route

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = AuthScreen.SignIn.route,
    ) {
        composable(route = AuthScreen.SignIn.route) {
            // TODO: add signin screen like SigninScreen(navController)
        }
    }
}

sealed class AuthScreen(val route: String) {
    data object SignIn: AuthScreen(route = Route.SIGNIN)
    //data object LogIn: AuthScreen(route = Route.LOGIN)
}