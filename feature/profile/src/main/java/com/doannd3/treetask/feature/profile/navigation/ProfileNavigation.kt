package com.doannd3.treetask.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.doannd3.treetask.feature.profile.ui.profile.ProfileRoute
import kotlinx.serialization.Serializable

@Serializable
data object ProfileGraphDestination

@Serializable
data object ProfileDestination

fun NavController.navigateToProfileGraph(navOptions: NavOptions? = null) {
    this.navigate(route = ProfileGraphDestination, navOptions = navOptions)
}

fun NavController.navigateToProfile(navOptions: NavOptions? = null) {
    this.navigate(route = ProfileDestination, navOptions = navOptions)
}

fun NavGraphBuilder.profileGraph() {
    navigation<ProfileGraphDestination>(
        startDestination = ProfileDestination,
    ) {
        composable<ProfileDestination> {
            ProfileRoute()
        }
    }
}
