package com.treestudio.treetask.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.doannd3.treetask.feature.auth.navigation.authGraph
import com.doannd3.treetask.feature.auth.navigation.navigateToForgotPassword
import com.doannd3.treetask.feature.auth.navigation.navigateToLogin
import com.doannd3.treetask.feature.auth.navigation.navigateToRegister
import com.doannd3.treetask.feature.chat.navigation.chatGraph
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
        startDestination = startDestination
    ) {

        authGraph(
            onNavigateToHome = {
                navController.navigateToTasksGraph()
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
                navController.navigateToLogin()
            },
            onForgotPasswordBack = {
                navController.popBackStack()
            }
        )

        tasksGraph(
            onTaskClick = { taskId ->
                navController.navigateToEditTask(taskId = taskId)
            },
            onAddTaskClick = {
                navController.navigateToAddTask()
            }
        )

        chatGraph()

        statsGraph()

        profileGraph()
    }
}