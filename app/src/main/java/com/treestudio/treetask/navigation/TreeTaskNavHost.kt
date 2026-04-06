package com.treestudio.treetask.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.doannd3.treetask.feature.auth.navigation.authGraph
import com.doannd3.treetask.feature.auth.navigation.forgotPasswordRoute
import com.doannd3.treetask.feature.auth.navigation.loginRoute
import com.doannd3.treetask.feature.auth.navigation.registerRoute
import com.doannd3.treetask.feature.tasks.navigation.navigateToTasksGraph
import com.treestudio.treetask.ui.TreeTaskAppState

@Composable
fun TreeTaskNavHost(
    startDestination: String,
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
                navController.navigate(route = registerRoute)
            },
            onNavigateToForgotPassword = {
                navController.navigate(route = forgotPasswordRoute)
            },
            onRegisterBack = {
                navController.popBackStack()
            },
            onNavigateToLogin = {
                navController.navigate(route = loginRoute)
            },
            onForgotPasswordBack = {
                navController.popBackStack()
            }
        )


    }
}