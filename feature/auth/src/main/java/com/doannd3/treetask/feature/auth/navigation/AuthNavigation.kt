package com.doannd3.treetask.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.doannd3.treetask.feature.auth.ui.forgotpassword.ForgotPasswordRoute
import com.doannd3.treetask.feature.auth.ui.login.LoginRoute
import com.doannd3.treetask.feature.auth.ui.register.RegisterRoute

const val authGraphRoutePattern = "auth_graph"
const val loginRoute = "login_route"
const val registerRoute = "register_route"
const val forgotPasswordRoute = "forgot_password_route"

//
fun NavController.navigateToAuthGraph(navOptions: NavOptions? = null) {
    this.navigate(route = authGraphRoutePattern, navOptions = navOptions)
}

fun NavGraphBuilder.authGraph(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onRegisterBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onForgotPasswordBack: () -> Unit
) {
    navigation(
        route = authGraphRoutePattern,
        startDestination = loginRoute
    ) {
        composable(route = loginRoute) {
            LoginRoute(
                onNavigateToHome = onNavigateToHome,
                onNavigateToRegister = onNavigateToRegister,
                onNavigateToForgotPassword = onNavigateToForgotPassword
            )
        }

        composable(route = registerRoute) {
            RegisterRoute(
                onNavigateToHome = onNavigateToHome,
                onRegisterBack = onRegisterBack
            )
        }

        composable(route = forgotPasswordRoute) {
            ForgotPasswordRoute(
                onNavigateToLogin = onNavigateToLogin,
                onForgotPasswordBack = onForgotPasswordBack
            )
        }
    }
}