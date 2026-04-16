package com.doannd3.treetask.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.doannd3.treetask.feature.auth.ui.forgotpassword.ForgotPasswordRoute
import com.doannd3.treetask.feature.auth.ui.login.LoginRoute
import com.doannd3.treetask.feature.auth.ui.register.RegisterRoute
import kotlinx.serialization.Serializable

/**
 * Khai báo các điểm đến
 */
@Serializable
data object AuthGraphRoute

@Serializable
data object LoginRoute

@Serializable
data object RegisterRoute

@Serializable
data object ForgotPasswordRoute

/**
 * các hàm mở rộng
 */
fun NavController.navigateToAuthGraph(navOptions: NavOptions? = null) {
    this.navigate(route = AuthGraphRoute, navOptions = navOptions)
}

fun NavController.navigateToRegister(navOptions: NavOptions? = null) {
    this.navigate(route = RegisterRoute, navOptions = navOptions)
}

fun NavController.navigateToForgotPassword(navOptions: NavOptions? = null) {
    this.navigate(route = ForgotPasswordRoute, navOptions = navOptions)
}

fun NavController.navigateToLogin(navOptions: NavOptions? = null) {
    this.navigate(route = LoginRoute, navOptions = navOptions)
}

fun NavGraphBuilder.authGraph(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onRegisterBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onForgotPasswordBack: () -> Unit
) {
    navigation<AuthGraphRoute>(startDestination = LoginRoute) {
        composable<LoginRoute> {
            LoginRoute(
                onNavigateToHome = onNavigateToHome,
                onNavigateToRegister = onNavigateToRegister,
                onNavigateToForgotPassword = onNavigateToForgotPassword
            )
        }

        composable<RegisterRoute> {
            RegisterRoute(
                onNavigateToHome = onNavigateToHome,
                onRegisterBack = onRegisterBack
            )
        }

        composable<ForgotPasswordRoute> {
            ForgotPasswordRoute(
                onNavigateToLogin = onNavigateToLogin,
                onForgotPasswordBack = onForgotPasswordBack
            )
        }
    }
}