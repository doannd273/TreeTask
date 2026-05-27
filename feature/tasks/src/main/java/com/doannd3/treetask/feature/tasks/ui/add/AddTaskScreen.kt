package com.doannd3.treetask.feature.tasks.ui.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.doannd3.treetask.core.common.asString
import com.doannd3.treetask.core.common.extension.toYmdDate
import com.doannd3.treetask.core.common.extension.ymdToDmy
import com.doannd3.treetask.core.common.extension.ymdToEpochMillis
import com.doannd3.treetask.core.designsystem.component.CommonHeader
import com.doannd3.treetask.core.designsystem.component.LocalGlobalAppState
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.designsystem.util.rememberDebouncedClick
import com.doannd3.treetask.core.model.task.TaskStatus
import com.doannd3.treetask.feature.tasks.R

@Composable
fun AddTaskRoute(
    viewModel: AddTaskViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val globalAppState = LocalGlobalAppState.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AddTaskScreen(
        state = state,
        onEvent = viewModel::onEvent,
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is AddTaskEffect.ShowErrorMessage -> {
                        globalAppState.showError(effect.message.asString(context))
                    }

                    is AddTaskEffect.ShowSuccessMessage -> {
                        globalAppState.showSuccess(effect.message.asString(context)) {
                            viewModel.onEvent(AddTaskEvent.SuccessAcknowledged)
                        }
                    }

                    is AddTaskEffect.NavigateBack -> {
                        onNavigateBack()
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
internal fun AddTaskScreen(
    state: AddTaskState,
    onEvent: (AddTaskEvent) -> Unit,
) {
    Scaffold(
        contentWindowInsets =
        WindowInsets.safeDrawing.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
        ),
        topBar = {
            CommonHeader(
                title = stringResource(R.string.tasks_add_task_title),
                onNavigateBack = { onEvent(AddTaskEvent.BackClicked) },
            )
        },
    ) { paddingValues ->
        AddTaskContent(
            modifier = Modifier.padding(paddingValues),
            state = state,
            onEvent = onEvent,
        )
    }
}

@AppPreviewLightDark
@Composable
private fun AddTaskScreenPreview() {
    TreeTaskTheme {
        AddTaskScreen(
            state = AddTaskState(),
            onEvent = {},
        )
    }
}

@Composable
internal fun AddTaskContent(
    modifier: Modifier = Modifier,
    state: AddTaskState,
    onEvent: (AddTaskEvent) -> Unit,
) {
    val descriptionFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val onSubmitAddTaskDebounced =
        rememberDebouncedClick {
            onEvent(AddTaskEvent.SubmitAddTask)
        }
    val isInputEnabled = !state.isLoading

    var showDatePicker by remember { mutableStateOf(false) }
    val displayText =
        remember(state.dueDate) {
            state.dueDate.ymdToDmy()
        }
    val initialMillis =
        remember(state.dueDate) {
            state.dueDate.ymdToEpochMillis()
        }

    if (showDatePicker) {
        AppDatePickerDialog(
            selectedDateMillis = initialMillis,
            onDismiss = { showDatePicker = false },
            onDateSelected = { onEvent(AddTaskEvent.DueDateChanged(it.toYmdDate())) },
        )
    }

    Column(
        modifier =
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AddTaskTitleInput(
                title = state.title,
                enabled = isInputEnabled,
                onTitleChange = { onEvent(AddTaskEvent.TitleChanged(it)) },
                onImeNext = { descriptionFocusRequester.requestFocus() },
            )

            AddTaskDescriptionInput(
                modifier =
                Modifier.focusRequester(descriptionFocusRequester),
                description = state.description,
                enabled = isInputEnabled,
                onDescriptionChange = { onEvent(AddTaskEvent.DescriptionChanged(it)) },
                onImeNext = {
                    focusManager.clearFocus()
                },
            )

            AddTaskStatusSelector(
                selectedStatus = state.status,
                enabled = isInputEnabled,
                onStatusChange = { onEvent(AddTaskEvent.StatusChanged(it)) },
            )

            AddTaskDueDateInput(
                dueDate = displayText,
                enabled = isInputEnabled,
                onDueDateClick = { showDatePicker = true },
            )

            AddTaskSubmitButton(
                modifier = Modifier.padding(top = 8.dp),
                isLoading = state.isLoading,
                onSubmit = {
                    focusManager.clearFocus()
                    onSubmitAddTaskDebounced()
                },
            )
        }
    }
}

@AppPreviewLightDark
@Composable
private fun AddTaskContentPreview() {
    TreeTaskTheme {
        AddTaskContent(
            state =
            AddTaskState(
                title = "Prepare sprint planning",
                description = "Review backlog and define priorities for the next sprint.",
                status = TaskStatus.IN_PROGRESS,
                dueDate = "2026-05-31",
            ),
            onEvent = {},
        )
    }
}
