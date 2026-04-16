package com.doannd3.treetask.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
data object ProfileGraphRoute

@Serializable
data object ProfileRoute

fun NavController.navigateToProfileGraph(navOptions: NavOptions? = null) {
    this.navigate(route = ProfileGraphRoute, navOptions = navOptions)
}

fun NavController.navigateToProfile(navOptions: NavOptions? = null) {
    this.navigate(route = ProfileRoute, navOptions = navOptions)
}

fun NavGraphBuilder.profileGraph() {
    navigation<ProfileGraphRoute>(
        startDestination = ProfileRoute
    ) {

    }
}