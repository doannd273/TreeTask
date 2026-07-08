package com.treestudio.treetask.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.treestudio.treetask.R
import com.treestudio.treetask.navigation.TopLevelDestination
import com.treestudio.treetask.navigation.TreeTaskNavHost
import com.treestudio.treetask.ui.component.TreeTaskBottomBar

@Composable
fun TreeTaskApp(
    startDestination: Any,
    isOnline: Boolean,
    pendingTaskId: String?,
    onPendingTaskConsumed: () -> Unit,
    appState: TreeTaskAppState = rememberTreeTaskAppState(),
) {
    Scaffold(
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
                        .background(MaterialTheme.colorScheme.error)
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.app_offline_banner),
                        color = MaterialTheme.colorScheme.onError,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        },
        bottomBar = {
            if (appState.isTopLevelDestination) {
                TreeTaskBottomBar(
                    destinations = TopLevelDestination.entries,
                    currentDestination = appState.currentDestination,
                    onNavigateToDestination = { topLevelDest ->
                        appState.navigateToTopLevelDestination(topLevelDest.route)
                    },
                )
            }
        },
    ) { paddingValues ->
        TreeTaskNavHost(
            startDestination = startDestination,
            appState = appState,
            pendingTaskId = pendingTaskId,
            onPendingTaskConsumed = onPendingTaskConsumed,
            modifier =
            Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues = paddingValues),
        )
    }
}
