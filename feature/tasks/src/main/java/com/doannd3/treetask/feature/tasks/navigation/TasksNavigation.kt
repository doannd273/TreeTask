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
data object TasksGraphDestination

@Serializable
data object TasksDestination

@Serializable
data object AddTasksDestination

@Serializable
data class EditTaskDestination(val taskId: String)

fun NavController.navigateToTasksGraph(navOptions: NavOptions? = null) {
    this.navigate(route = TasksGraphDestination, navOptions = navOptions)
}

fun NavController.navigateToEditTask(taskId: String, navOptions: NavOptions? = null) {
    this.navigate(route = EditTaskDestination(taskId = taskId), navOptions = navOptions)
}

fun NavController.navigateToAddTask(navOptions: NavOptions? = null) {
    this.navigate(route = AddTasksDestination, navOptions = navOptions)
}

fun NavGraphBuilder.tasksGraph(
    onTaskClick: (String) -> Unit,
    onAddTaskClick: () -> Unit,
) {
    navigation<TasksGraphDestination>(startDestination = TasksDestination) {
        composable<TasksDestination> {
            TasksRoute(
                onTaskClick = { task -> onTaskClick(task.id) },
                onAddTaskClick = onAddTaskClick,
            )
        }

        composable<EditTaskDestination> { backStackEntry ->
            val args = backStackEntry.toRoute<EditTaskDestination>()
            val taskId = args.taskId

            EditTasksRoute()
        }

        composable<AddTasksDestination> {
        }
    }
}
