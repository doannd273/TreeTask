package com.doannd3.treetask.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.doannd3.treetask.feature.profile.ui.changepassword.ChangePasswordRoute
import com.doannd3.treetask.feature.profile.ui.profile.ProfileRoute
import kotlinx.serialization.Serializable

@Serializable
data object ProfileGraphDestination

@Serializable
data object ProfileDestination

@Serializable
data object ChangePasswordDestination

@Serializable
data object EditProfileDestination

fun NavController.navigateToProfileGraph(navOptions: NavOptions? = null) {
    this.navigate(route = ProfileGraphDestination, navOptions = navOptions)
}

fun NavController.navigateToProfile(navOptions: NavOptions? = null) {
    this.navigate(route = ProfileDestination, navOptions = navOptions)
}

fun NavController.navigateToChangePassword(navOptions: NavOptions? = null) {
    this.navigate(route = ChangePasswordDestination, navOptions = navOptions)
}

fun NavController.navigateToEditProfile(navOptions: NavOptions? = null) {
    this.navigate(route = EditProfileDestination, navOptions = navOptions)
}

fun NavGraphBuilder.profileGraph(
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
) {
    navigation<ProfileGraphDestination>(
        startDestination = ProfileDestination,
    ) {
        composable<ProfileDestination> {
            ProfileRoute(
                onNavigateToLogin = onNavigateToLogin,
                onNavigateToChangePassword = onNavigateToChangePassword,
                onNavigateToEditProfile = onNavigateToEditProfile,
            )
        }

        composable<ChangePasswordDestination> {
            ChangePasswordRoute(onNavigateBack = onNavigateBack)
        }
    }
}
