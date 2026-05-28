package com.doannd3.treetask.feature.tasks.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.doannd3.treetask.core.common.asString
import com.doannd3.treetask.core.designsystem.component.LocalGlobalAppState
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.model.task.TaskStatus
import com.doannd3.treetask.feature.tasks.R
import kotlinx.coroutines.flow.flowOf
import java.time.Instant

@Composable
fun TasksRoute(
    viewModel: TasksViewModel = hiltViewModel(),
    onTaskClick: (Task) -> Unit,
    onAddTaskClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val pagingItems = state.tasks.collectAsLazyPagingItems()

    val globalAppState = LocalGlobalAppState.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    TasksScreen(
        state = state,
        pagingItems = pagingItems,
        onEvent = viewModel::onEvent,
        onTaskClick = onTaskClick,
        onAddTaskClick = onAddTaskClick,
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is TasksEffect.ShowErrorMessage -> {
                        val errorStr = effect.message.asString(context)
                        globalAppState.showError(errorStr)
                    }
                }
            }
        }
    }

    // Lỗi crash/unexpected từ BaseViewModel (CoroutineExceptionHandler)
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
internal fun TasksScreen(
    state: TasksState,
    pagingItems: LazyPagingItems<Task>,
    onEvent: (TasksEvent) -> Unit,
    onTaskClick: (Task) -> Unit,
    onAddTaskClick: () -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTaskClick,
            ) {
                Icon(
                    painter = painterResource(R.drawable.tasks_ic_add),
                    contentDescription = stringResource(R.string.tasks_cd_add_task),
                )
            }
        },
    ) { paddingValues ->
        TasksContent(
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
            state = state,
            pagingItems = pagingItems,
            onEvent = onEvent,
            onTaskClick = onTaskClick,
        )
    }
}

@Composable
internal fun TasksContent(
    modifier: Modifier = Modifier,
    state: TasksState,
    pagingItems: LazyPagingItems<Task>,
    onEvent: (TasksEvent) -> Unit,
    onTaskClick: (Task) -> Unit,
) {
    Column(
        modifier =
        modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        SearchTaskInput(
            isLoadingSearch = state.isLoadingSearch,
            searchQuery = state.searchQuery,
            onSearchChange = { onEvent(TasksEvent.SearchChanged(it)) },
            onClearClick = { onEvent(TasksEvent.SearchQueryClear) },
        )

        TaskStatusChips(
            taskStatusSelected = state.taskStatusSelected,
            onFilterSelect = { onEvent(TasksEvent.FilterSelected(it)) },
        )

        LazyColumn(
            modifier = Modifier.padding(top = 6.dp),
        ) {
            items(
                count = pagingItems.itemCount,
                key = pagingItems.itemKey { it.id },
                contentType = pagingItems.itemContentType { "task" },
            ) { index ->
                val task = pagingItems[index]
                if (task != null) {
                    TaskItem(task = task, onClick = {
                        onTaskClick.invoke(task)
                    })
                }
            }

            pagingItems.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    loadState.append is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    loadState.append is LoadState.Error -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                TextButton(onClick = { retry() }) {
                                    Text(text = stringResource(R.string.tasks_error_retry))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@AppPreviewLightDark
@Composable
private fun TasksScreenPreview() {
    TreeTaskTheme {
        val sampleTasks = listOf(
            Task(
                id = "1",
                userId = "user_1",
                title = "Fix login bug",
                description = "Crash when login with Google",
                status = TaskStatus.PENDING,
                dueDate = Instant.parse("2026-04-20T10:00:00Z"),
                createdAt = Instant.parse("2026-04-10T08:00:00Z"),
                updatedAt = Instant.parse("2026-04-15T09:00:00Z"),
            ),
            Task(
                id = "2",
                userId = "user_1",
                title = "Design home screen",
                description = "Create UI with Compose",
                status = TaskStatus.TODO,
                dueDate = Instant.parse("2026-04-25T10:00:00Z"),
                createdAt = Instant.parse("2026-04-11T08:00:00Z"),
                updatedAt = Instant.parse("2026-04-12T09:00:00Z"),
            ),
        )

        val sampleTasksState =
            TasksState(
                isLoading = false,
                searchQuery = "",
                taskStatusSelected = TaskStatus.IN_PROGRESS,
                tasks = flowOf(PagingData.from(sampleTasks)),
            )

        val pagingItems = sampleTasksState.tasks.collectAsLazyPagingItems()

        TasksScreen(
            state = sampleTasksState,
            pagingItems = pagingItems,
            onEvent = {},
            onTaskClick = {},
            onAddTaskClick = {},
        )
    }
}
