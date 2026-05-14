package com.treestudio.treetask.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.doannd3.treetask.core.designsystem.component.AppLoadingDialog
import com.doannd3.treetask.core.designsystem.component.GlobalAppState
import com.doannd3.treetask.core.designsystem.component.LocalGlobalAppState
import com.doannd3.treetask.core.designsystem.component.message.AppMessageDialog
import com.treestudio.treetask.R
import com.treestudio.treetask.navigation.TopLevelDestination
import com.treestudio.treetask.navigation.TreeTaskNavHost
import com.treestudio.treetask.ui.component.TreeTaskBottomBar

@Composable
fun TreeTaskApp(
    startDestination: Any,
    isOnline: Boolean,
    appState: TreeTaskAppState = rememberTreeTaskAppState(),
) {
    // Toàn bộ logic Màn Hình Báo Lỗi/Loading Toàn Sự Kiện (Ở Module core:designsystem)
    // Sẽ được kéo vào đây!
    // 1. Tạo biến quản lý Lỗi Tròn hệ thống
    val globalAppState = remember { GlobalAppState() }

    CompositionLocalProvider(LocalGlobalAppState provides globalAppState) {
        Scaffold(
            // Top
            topBar = {
                AnimatedVisibility(
                    visible = !isOnline,
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                ) {
                    Box(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(Color.Red.copy(alpha = 0.8f))
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.app_offline_banner),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            },
            // bottom
            bottomBar = {
                if (appState.isTopLevelDestination) {
                    TreeTaskBottomBar(
                        destinations = TopLevelDestination.entries,
                        currentDestination = appState.currentDestination,
                        onNavigateToDestination = { topLevelDest ->
                            // Nhờ AppState đổi Tab nội bộ
                            appState.navigateToTopLevelDestination(topLevelDest.route)
                        },
                    )
                }
            },
        ) { paddingValues ->
            TreeTaskNavHost(
                startDestination = startDestination,
                appState = appState,
                modifier = Modifier.padding(paddingValues),
            )

            // dialog
            AppMessageDialog(
                appDialogState = globalAppState.appDialogState,
                onDismiss = {
                    globalAppState.clearError()
                    globalAppState.appDialogState?.onDismiss
                },
            )

            // loading
            AppLoadingDialog(
                isLoading = globalAppState.isGlobalLoading,
            )
        }
    }
}
