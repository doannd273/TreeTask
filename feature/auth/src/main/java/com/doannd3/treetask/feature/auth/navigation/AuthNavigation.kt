package com.doannd3.treetask.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.doannd3.treetask.feature.auth.ui.LoginRoute

const val authGraphRoutePattern = "auth_graph"
const val loginRoute = "login_route"
const val registerRoute = "register_route"

//
fun NavController.navigateToAuthGraph(navOptions: NavOptions? = null) {
    this.navigate(route = authGraphRoutePattern, navOptions = navOptions)
}

fun NavGraphBuilder.authGraph(
    onNavigateToHome: () -> Unit
) {
    navigation(
        route = authGraphRoutePattern,
        startDestination = loginRoute
    ) {
        composable(route = loginRoute) {
            LoginRoute(
                onNavigateToHome = onNavigateToHome,
                onNavigateToRegister = {

                }
            )
        }

        composable(route = registerRoute) {

        }
    }
}