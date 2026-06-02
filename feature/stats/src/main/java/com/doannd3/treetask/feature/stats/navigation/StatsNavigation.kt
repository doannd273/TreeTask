package com.doannd3.treetask.feature.stats.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.doannd3.treetask.feature.stats.ui.StatsRoute
import kotlinx.serialization.Serializable

@Serializable
data object StatsGraphDestination

@Serializable
data object StatsDestination

fun NavController.navigateToStatsGraph(navOptions: NavOptions? = null) {
    this.navigate(route = StatsGraphDestination, navOptions = navOptions)
}

fun NavController.navigateToStats(navOptions: NavOptions? = null) {
    this.navigate(route = StatsDestination, navOptions = navOptions)
}

fun NavGraphBuilder.statsGraph(onRecentTaskClick: (String) -> Unit) {
    navigation<StatsGraphDestination>(
        startDestination = StatsDestination,
    ) {
        composable<StatsDestination> {
            StatsRoute(onRecentTaskClick = onRecentTaskClick)
        }
    }
}
