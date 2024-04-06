package com.github.wanderwise_inc.app.ui.navigation.graph

import androidx.activity.viewModels
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.github.wanderwise_inc.app.ui.signin.LoginScreen
import com.github.wanderwise_inc.app.ui.navigation.Route
import com.github.wanderwise_inc.app.viewmodel.UserViewModel

fun NavGraphBuilder.authNavGraph(
    userViewModel: UserViewModel, 
    navController: NavHostController
) {
    
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = AuthScreen.SignIn.route,
    ) {
        composable(route = AuthScreen.SignIn.route) {
            LoginScreen(userViewModel = userViewModel, navController = navController)
        }
    }
}

sealed class AuthScreen(val route: String) {
    data object SignIn: AuthScreen(route = Route.SIGNIN)
    //data object LogIn: AuthScreen(route = Route.LOGIN)
}