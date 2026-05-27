package com.treestudio.treetask.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.doannd3.treetask.feature.auth.navigation.AuthGraphDestination
import com.doannd3.treetask.feature.auth.navigation.authGraph
import com.doannd3.treetask.feature.auth.navigation.navigateToAuthGraph
import com.doannd3.treetask.feature.auth.navigation.navigateToForgotPassword
import com.doannd3.treetask.feature.auth.navigation.navigateToRegister
import com.doannd3.treetask.feature.chat.navigation.chatGraph
import com.doannd3.treetask.feature.profile.navigation.navigateToChangePassword
import com.doannd3.treetask.feature.profile.navigation.navigateToEditProfile
import com.doannd3.treetask.feature.profile.navigation.profileGraph
import com.doannd3.treetask.feature.stats.navigation.statsGraph
import com.doannd3.treetask.feature.tasks.navigation.navigateToAddTask
import com.doannd3.treetask.feature.tasks.navigation.navigateToEditTask
import com.doannd3.treetask.feature.tasks.navigation.navigateToTasksGraph
import com.doannd3.treetask.feature.tasks.navigation.tasksGraph
import com.treestudio.treetask.ui.TreeTaskAppState

@Composable
fun TreeTaskNavHost(
    startDestination: Any,
    appState: TreeTaskAppState,
    modifier: Modifier,
) {
    val navController = appState.navController
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        authGraph(
            onNavigateToHome = {
                navController.navigateToTasksGraph(
                    navOptions = navOptions {
                        popUpTo(AuthGraphDestination) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    },
                )
            },
            onNavigateToRegister = {
                navController.navigateToRegister()
            },
            onNavigateToForgotPassword = {
                navController.navigateToForgotPassword()
            },
            onRegisterBack = {
                navController.popBackStack()
            },
            onNavigateToLogin = {
                navController.popBackStack()
            },
            onForgotPasswordBack = {
                navController.popBackStack()
            },
        )

        tasksGraph(
            onTaskClick = { taskId ->
                navController.navigateToEditTask(taskId = taskId)
            },
            onAddTaskClick = {
                navController.navigateToAddTask()
            },
            onNavigateBack = {
                navController.popBackStack()
            },
        )

        chatGraph()

        statsGraph()

        profileGraph(
            onNavigateToLogin = {
                navController.navigateToAuthGraph(
                    navOptions = navOptions {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    },
                )
            },
            onNavigateBack = {
                navController.popBackStack()
            },
            onNavigateToChangePassword = {
                navController.navigateToChangePassword()
            },
            onNavigateToEditProfile = {
                navController.navigateToEditProfile()
            },
        )
    }
}
