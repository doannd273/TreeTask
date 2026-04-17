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
data object AuthGraphDestination

@Serializable
data object LoginDestination

@Serializable
data object RegisterDestination

@Serializable
data object ForgotPasswordDestination

/**
 * các hàm mở rộng
 */
fun NavController.navigateToAuthGraph(navOptions: NavOptions? = null) {
    this.navigate(route = AuthGraphDestination, navOptions = navOptions)
}

fun NavController.navigateToRegister(navOptions: NavOptions? = null) {
    this.navigate(route = RegisterDestination, navOptions = navOptions)
}

fun NavController.navigateToForgotPassword(navOptions: NavOptions? = null) {
    this.navigate(route = ForgotPasswordDestination, navOptions = navOptions)
}

fun NavController.navigateToLogin(navOptions: NavOptions? = null) {
    this.navigate(route = LoginDestination, navOptions = navOptions)
}

fun NavGraphBuilder.authGraph(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onRegisterBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onForgotPasswordBack: () -> Unit
) {
    navigation<AuthGraphDestination>(startDestination = LoginDestination) {
        composable<LoginDestination> {
            LoginRoute(
                onNavigateToHome = onNavigateToHome,
                onNavigateToRegister = onNavigateToRegister,
                onNavigateToForgotPassword = onNavigateToForgotPassword
            )
        }

        composable<RegisterDestination> {
            RegisterRoute(
                onNavigateToHome = onNavigateToHome,
                onRegisterBack = onRegisterBack
            )
        }

        composable<ForgotPasswordDestination> {
            ForgotPasswordRoute(
                onNavigateToLogin = onNavigateToLogin,
                onForgotPasswordBack = onForgotPasswordBack
            )
        }
    }
}