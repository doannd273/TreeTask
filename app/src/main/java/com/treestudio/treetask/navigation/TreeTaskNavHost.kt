package com.treestudio.treetask.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.doannd3.treetask.feature.auth.navigation.authGraph
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
        startDestination = startDestination // Lấy hằng số tử bên feature:auth
    ) {
        // Lắp Feature Auth vào
        authGraph(
            onNavigateToHome = {
                // Ở đây app module gọi logic chuyển sang Task
                //navController.navigateToTasksGraph() // Hàm extension do bên feature:tasks định nghĩa
            }
        )
        // Lắp Feature Tasks vào
//        tasksGraph(
//            onNavigateToProfile = { navController.navigateToProfileGraph() }
//        )
    }
}