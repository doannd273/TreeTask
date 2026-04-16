package com.doannd3.treetask.feature.stats.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
data object StatsGraphRoute

@Serializable
data object StatsRoute

fun NavController.navigateToStatsGraph(navOptions: NavOptions? = null) {
    this.navigate(route = StatsGraphRoute, navOptions = navOptions)
}

fun NavController.navigateToStats(navOptions: NavOptions? = null) {
    this.navigate(route = StatsRoute, navOptions = navOptions)
}

fun NavGraphBuilder.statsGraph() {
    navigation<StatsGraphRoute>(
        startDestination = StatsRoute
    ) {
        // composable<StatsRoute> { ... }
    }
}