package com.treestudio.treetask.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.doannd3.treetask.core.designsystem.component.AppErrorDialog
import com.doannd3.treetask.core.designsystem.component.AppLoadingDialog
import com.doannd3.treetask.core.designsystem.component.GlobalAppState
import com.doannd3.treetask.core.designsystem.component.LocalGlobalAppState
import com.treestudio.treetask.navigation.TopLevelDestination
import com.treestudio.treetask.navigation.TreeTaskNavHost
import com.treestudio.treetask.ui.component.TreeTaskBottomBar

@Composable
fun TreeTaskApp(
    startDestination: Any,
    appState: TreeTaskAppState = rememberTreeTaskAppState()
) {
    // Toàn bộ logic Màn Hình Báo Lỗi/Loading Toàn Sự Kiện (Ở Module core:designsystem)
    // Sẽ được kéo vào đây!
    // 1. Tạo biến quản lý Lỗi Tròn hệ thống
    val globalAppState = remember { GlobalAppState() }

    CompositionLocalProvider(LocalGlobalAppState provides globalAppState) {
        Scaffold(
            bottomBar = {
                if (appState.isTopLevelDestination) {
                    TreeTaskBottomBar(
                        destinations = TopLevelDestination.entries,
                        currentDestination = appState.currentDestination,
                        onNavigateToDestination = { topLevelDest ->
                            // Nhờ AppState đổi Tab nội bộ
                            appState.navigateToTopLevelDestination(topLevelDest.route)
                        }
                    )
                }
            }
        ) { paddingValues ->
            TreeTaskNavHost(
                startDestination = startDestination,
                appState = appState,
                modifier = Modifier.padding(paddingValues)
            )

            // dialog
            AppErrorDialog(
                errorMessage = globalAppState.errorMessage,
                onDismiss = { globalAppState.clearError() }
            )

            // loading
            AppLoadingDialog(
                isLoading = globalAppState.isGlobalLoading
            )
        }
    }
}