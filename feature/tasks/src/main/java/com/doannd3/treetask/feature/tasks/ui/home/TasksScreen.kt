package com.doannd3.treetask.feature.tasks.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.doannd3.treetask.core.common.asString
import com.doannd3.treetask.core.designsystem.component.LocalGlobalAppState
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.model.task.TaskStatus
import com.doannd3.treetask.feature.tasks.R
import java.time.Instant

@Composable
fun TasksRoute(
    viewModel: TasksViewModel = hiltViewModel(),
    onTaskClick: (Task) -> Unit,
    onAddTaskClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val globalAppState = LocalGlobalAppState.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    TasksScreen(
        state = state,
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
fun TasksScreen(
    state: TasksState,
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
                    contentDescription = null,
                )
            }
        },
    ) { paddingValues ->
        TasksContent(
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
            state = state,
            onEvent = onEvent,
            onTaskClick = onTaskClick,
        )
    }
}

@Composable
fun TasksContent(
    modifier: Modifier = Modifier,
    state: TasksState,
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
            items(items = state.tasks, key = { task -> task.id }) { task ->
                TaskItem(task = task, onClick = {
                    onTaskClick.invoke(task)
                })
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun TasksScreenPreview() {
    val sampleTasksState =
        TasksState(
            isLoading = false,
            searchQuery = "",
            taskStatusSelected = TaskStatus.IN_PROGRESS,
            tasks =
            listOf(
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
                Task(
                    id = "3",
                    userId = "user_2",
                    title = "Write unit test",
                    description = null,
                    status = TaskStatus.IN_PROGRESS,
                    dueDate = Instant.parse("2026-04-18T10:00:00Z"),
                    createdAt = Instant.parse("2026-04-09T08:00:00Z"),
                    updatedAt = Instant.parse("2026-04-14T09:00:00Z"),
                ),
                Task(
                    id = "4",
                    userId = "user_2",
                    title = "Fix payment issue",
                    description = "Timeout when calling API",
                    status = TaskStatus.DONE,
                    dueDate = Instant.parse("2026-04-15T10:00:00Z"),
                    createdAt = Instant.parse("2026-04-05T08:00:00Z"),
                    updatedAt = Instant.parse("2026-04-15T07:00:00Z"),
                ),
            ),
        )

    TasksScreen(
        state = sampleTasksState,
        onEvent = {},
        onTaskClick = {},
        onAddTaskClick = {},
    )
}
