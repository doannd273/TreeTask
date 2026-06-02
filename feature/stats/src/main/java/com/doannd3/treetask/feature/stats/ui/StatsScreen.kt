package com.doannd3.treetask.feature.stats.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.doannd3.treetask.core.common.asString
import com.doannd3.treetask.core.designsystem.component.LocalGlobalAppState
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.model.stats.RecentTaskSummary
import com.doannd3.treetask.core.model.stats.TaskStats
import com.doannd3.treetask.core.model.task.TaskStatus
import java.time.Instant
import kotlin.math.roundToInt

@Composable
fun StatsRoute(viewModel: StatsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val globalAppState = LocalGlobalAppState.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    StatsScreen(
        state = state,
        onEvent = viewModel::onEvent,
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is StatsEffect.ShowErrorMessage -> {
                        val errorStr = effect.message.asString(context)
                        globalAppState.showError(errorStr)
                    }
                }
            }
        }
    }

    LaunchedEffect(viewModel.baseErrorEffect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.baseErrorEffect.collect { message ->
                globalAppState.showError(message.asString(context))
            }
        }
    }

    LaunchedEffect(state.isLoading) {
        if (state.isLoading) {
            globalAppState.showLoading()
        } else {
            globalAppState.hideLoading()
        }
    }
}

@Composable
internal fun StatsScreen(
    state: StatsState,
    onEvent: (StatsEvent) -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        StatsContent(
            modifier = Modifier.padding(paddingValues),
            state = state,
            onEvent = onEvent,
        )
    }
}

@Composable
internal fun StatsContent(
    modifier: Modifier = Modifier,
    state: StatsState,
    onEvent: (StatsEvent) -> Unit,
) {
    when {
        state.hasInitialLoadError -> {
            StatsErrorState(
                modifier = modifier,
                onRetry = { onEvent(StatsEvent.Refresh) },
            )
        }

        state.taskStats == null -> {
            Unit
        }

        state.isEmpty -> {
            StatsEmptyState(modifier = modifier)
        }

        else -> {
            StatsDataContent(
                modifier = modifier,
                stats = state.taskStats,
            )
        }
    }
}

@Composable
private fun StatsDataContent(
    modifier: Modifier = Modifier,
    stats: TaskStats,
) {
    val completionRate = stats.completionRate.roundToInt().coerceIn(0, 100)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item(key = "summary") {
            StatsSummaryCard(
                done = stats.done,
                total = stats.total,
                completionRate = completionRate,
            )
        }

        item(key = "status_tile") {
            StatsStatusTile(
                todo = stats.todo,
                inProgress = stats.inProgress,
                pending = stats.pending,
                done = stats.done,
            )
        }

        item(key = "workload_breakdown") {
            WorkloadBreakdownCard(
                todo = stats.todo,
                inProgress = stats.inProgress,
                pending = stats.pending,
                done = stats.done,
            )
        }

        if (stats.recentTasks.isNotEmpty()) {
            item(key = "recent_tasks") {
                RecentTasksCard(
                    tasks = stats.recentTasks,
                    onTaskClick = {},
                )
            }
        }
    }
}

@AppPreviewLightDark
@Composable
private fun StatsScreenDataPreview() {
    TreeTaskTheme {
        StatsScreen(
            state =
                StatsState(
                    taskStats =
                        TaskStats(
                            total = 37,
                            todo = 8,
                            inProgress = 6,
                            pending = 3,
                            done = 20,
                            completionRate = 54.0,
                            recentTasks =
                                listOf(
                                    RecentTaskSummary(
                                        id = "1",
                                        title = "Fix login token refresh",
                                        status = TaskStatus.DONE,
                                        createdAt = Instant.parse("2026-05-20T08:00:00Z"),
                                        dueDate = Instant.parse("2026-05-25T17:00:00Z"),
                                    ),
                                    RecentTaskSummary(
                                        id = "2",
                                        title = "Implement stats screen UI",
                                        status = TaskStatus.IN_PROGRESS,
                                        createdAt = Instant.parse("2026-05-28T09:00:00Z"),
                                        dueDate = Instant.parse("2026-06-05T17:00:00Z"),
                                    ),
                                    RecentTaskSummary(
                                        id = "3",
                                        title = "Write unit tests for auth flow",
                                        status = TaskStatus.TODO,
                                        createdAt = Instant.parse("2026-05-30T10:00:00Z"),
                                        dueDate = null,
                                    ),
                                    RecentTaskSummary(
                                        id = "4",
                                        title = "Design review with team",
                                        status = TaskStatus.PENDING,
                                        createdAt = Instant.parse("2026-05-29T14:00:00Z"),
                                        dueDate = Instant.parse("2026-06-03T12:00:00Z"),
                                    ),
                                ),
                        ),
                ),
            onEvent = {},
        )
    }
}
