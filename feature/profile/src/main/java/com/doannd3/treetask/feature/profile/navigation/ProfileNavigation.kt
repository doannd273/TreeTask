package com.doannd3.treetask.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation

const val profileGraphRoutePattern = "profile_graph"
const val profileRoute = "profile_route"

fun NavController.navigateToProfileGraph(navOptions: NavOptions? = null) {
    this.navigate(route = profileGraphRoutePattern, navOptions = navOptions)
}

fun NavGraphBuilder.profileGraph() {
    navigation(
        route = profileGraphRoutePattern,
        startDestination = profileRoute
    ) {

    }
}