package com.doannd3.treetask.feature.tasks.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.doannd3.treetask.feature.tasks.ui.home.TasksRoute

const val tasksGraphRoutePattern = "tasks_graph"
const val tasksRoute = "tasks_route"

fun NavController.navigateToTasksGraph(navOptions: NavOptions? = null) {
    this.navigate(route = tasksGraphRoutePattern, navOptions = navOptions)
}

fun NavGraphBuilder.tasksGraph() {
    navigation(
        route = tasksGraphRoutePattern,
        startDestination = tasksRoute
    ) {
        composable(route = tasksRoute) {
            TasksRoute(

            )
        }
    }
}