package com.doannd3.treetask.feature.stats.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation

const val statsGraphRoutePattern = "stats_graph"
const val statsRoute = "stats_route"

fun NavController.navigateToStatsGraph(navOptions: NavOptions? = null) {
    this.navigate(route = statsGraphRoutePattern, navOptions = navOptions)
}

fun NavGraphBuilder.statsGraph() {
    navigation(
        route = statsGraphRoutePattern,
        startDestination = statsRoute
    ) {

    }
}