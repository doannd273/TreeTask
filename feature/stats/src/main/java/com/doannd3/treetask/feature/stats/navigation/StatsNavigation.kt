package com.doannd3.treetask.feature.stats.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.doannd3.treetask.feature.stats.ui.chart.ChartRoute
import kotlinx.serialization.Serializable

@Serializable
data object StatsGraphRoute

@Serializable
data object ChartRoute

fun NavController.navigateToStatsGraph(navOptions: NavOptions? = null) {
    this.navigate(route = StatsGraphRoute, navOptions = navOptions)
}

fun NavController.navigateToChart(navOptions: NavOptions? = null) {
    this.navigate(route = ChartRoute, navOptions = navOptions)
}

fun NavGraphBuilder.statsGraph() {
    navigation<StatsGraphRoute>(
        startDestination = ChartRoute
    ) {
        composable<ChartRoute> {
            ChartRoute()
        }
    }
}