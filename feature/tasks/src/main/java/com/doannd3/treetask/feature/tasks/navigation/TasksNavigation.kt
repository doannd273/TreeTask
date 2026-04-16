package com.doannd3.treetask.feature.tasks.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.doannd3.treetask.feature.tasks.ui.edit.EditTasksRoute
import com.doannd3.treetask.feature.tasks.ui.home.TasksRoute
import kotlinx.serialization.Serializable

@Serializable
data object TasksGraphRoute

@Serializable
data object TasksRoute

@Serializable
data object AddTasksRoute

@Serializable
data class EditTaskRoute(val taskId: String)

fun NavController.navigateToTasksGraph(navOptions: NavOptions? = null) {
    this.navigate(route = TasksGraphRoute, navOptions = navOptions)
}

fun NavController.navigateToEditTask(taskId: String, navOptions: NavOptions? = null) {
    this.navigate(route = EditTaskRoute(taskId = taskId), navOptions = navOptions)
}

fun NavController.navigateToAddTask(navOptions: NavOptions? = null) {
    this.navigate(route = AddTasksRoute, navOptions = navOptions)
}

fun NavGraphBuilder.tasksGraph(
    onTaskClick: (String) -> Unit,
    onAddTaskClick: () -> Unit
) {
    navigation<TasksGraphRoute>(startDestination = TasksRoute) {
        composable<TasksRoute> {
            TasksRoute(
                onTaskClick = { task -> onTaskClick(task.id) },
                onAddTaskClick = onAddTaskClick,
            )
        }

        composable<EditTaskRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<EditTaskRoute>()
            val taskId = args.taskId

            EditTasksRoute(

            )
        }
    }
}