package com.doannd3.treetask.feature.tasks.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.doannd3.treetask.feature.tasks.ui.home.TasksRoute
import com.doannd3.treetask.feature.tasks.ui.taskform.TaskFormMode
import com.doannd3.treetask.feature.tasks.ui.taskform.TaskFormRoute
import kotlinx.serialization.Serializable

@Serializable
data object TasksGraphDestination

@Serializable
data object TasksDestination

@Serializable
data object AddTasksDestination

@Serializable
data class EditTaskDestination(
    val taskId: String,
    val mode: String,
)

@Serializable
data class ViewTaskDestination(
    val taskId: String,
    val mode: String,
)

fun NavController.navigateToTasksGraph(navOptions: NavOptions? = null) {
    this.navigate(route = TasksGraphDestination, navOptions = navOptions)
}

fun NavController.navigateToEditTask(
    taskId: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route =
            EditTaskDestination(
                taskId = taskId,
                mode = TaskFormMode.EDIT.name,
            ),
        navOptions = navOptions,
    )
}

fun NavController.navigateToViewTask(
    taskId: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route =
            ViewTaskDestination(
                taskId = taskId,
                mode = TaskFormMode.VIEW.name,
            ),
        navOptions = navOptions,
    )
}

fun NavController.navigateToAddTask(navOptions: NavOptions? = null) {
    this.navigate(route = AddTasksDestination, navOptions = navOptions)
}

fun NavGraphBuilder.tasksGraph(
    onTaskClick: (String) -> Unit,
    onAddTaskClick: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    navigation<TasksGraphDestination>(startDestination = TasksDestination) {
        composable<TasksDestination> {
            TasksRoute(
                onTaskClick = { task -> onTaskClick(task.id) },
                onAddTaskClick = onAddTaskClick,
            )
        }

        composable<EditTaskDestination> {
            TaskFormRoute(onNavigateBack = onNavigateBack)
        }

        composable<ViewTaskDestination> {
            TaskFormRoute(onNavigateBack = onNavigateBack)
        }

        composable<AddTasksDestination> {
            TaskFormRoute(
                onNavigateBack = onNavigateBack,
            )
        }
    }
}
