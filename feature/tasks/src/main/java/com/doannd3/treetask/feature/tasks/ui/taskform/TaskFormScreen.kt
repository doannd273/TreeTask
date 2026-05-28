package com.doannd3.treetask.feature.tasks.ui.taskform

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

@Composable
fun TaskFormRoute(
    viewModel: TaskFormViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val globalAppState = LocalGlobalAppState.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    TaskFormScreen(
        state = state,
        onEvent = viewModel::onEvent,
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is TaskFormEffect.ShowErrorMessage -> {
                        globalAppState.showError(effect.message.asString(context))
                    }

                    is TaskFormEffect.ShowSuccessMessage -> {
                        globalAppState.showSuccess(effect.message.asString(context)) {
                            viewModel.onEvent(TaskFormEvent.SuccessAcknowledged)
                        }
                    }

                    is TaskFormEffect.NavigateBack -> {
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
internal fun TaskFormScreen(
    state: TaskFormState,
    onEvent: (TaskFormEvent) -> Unit,
) {
    Scaffold(
        contentWindowInsets =
        WindowInsets.safeDrawing.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
        ),
        topBar = {
            CommonHeader(
                title = state.screenTitle?.asString(LocalContext.current).orEmpty(),
                onNavigateBack = { onEvent(TaskFormEvent.BackClicked) },
            )
        },
    ) { paddingValues ->
        TaskFormContent(
            modifier = Modifier.padding(paddingValues),
            state = state,
            onEvent = onEvent,
        )
    }
}

@AppPreviewLightDark
@Composable
private fun TaskFormScreenPreview() {
    TreeTaskTheme {
        TaskFormScreen(
            state = TaskFormState(),
            onEvent = {},
        )
    }
}

@Composable
internal fun TaskFormContent(
    modifier: Modifier = Modifier,
    state: TaskFormState,
    onEvent: (TaskFormEvent) -> Unit,
) {
    val descriptionFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val onSubmitTaskFormDebounced =
        rememberDebouncedClick {
            onEvent(TaskFormEvent.SubmitTaskForm)
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
            onDateSelected = { onEvent(TaskFormEvent.DueDateChanged(it.toYmdDate())) },
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
            TaskTitleInput(
                title = state.title,
                enabled = isInputEnabled,
                onTitleChange = { onEvent(TaskFormEvent.TitleChanged(it)) },
                onImeNext = { descriptionFocusRequester.requestFocus() },
            )

            TaskDescriptionInput(
                modifier =
                Modifier.focusRequester(descriptionFocusRequester),
                description = state.description,
                enabled = isInputEnabled,
                onDescriptionChange = { onEvent(TaskFormEvent.DescriptionChanged(it)) },
                onImeNext = {
                    focusManager.clearFocus()
                },
            )

            TaskStatusSelector(
                selectedStatus = state.status,
                enabled = isInputEnabled,
                onStatusChange = { onEvent(TaskFormEvent.StatusChanged(it)) },
            )

            TaskDueDateInput(
                dueDate = displayText,
                enabled = isInputEnabled,
                onDueDateClick = { showDatePicker = true },
            )

            TaskSubmitButton(
                modifier = Modifier.padding(top = 8.dp),
                isLoading = state.isLoading,
                isEditMode = state.isEditMode,
                onSubmit = {
                    focusManager.clearFocus()
                    onSubmitTaskFormDebounced()
                },
            )
        }
    }
}

@AppPreviewLightDark
@Composable
private fun TaskFormContentPreview() {
    TreeTaskTheme {
        TaskFormContent(
            state =
            TaskFormState(
                title = "Prepare sprint planning",
                description = "Review backlog and define priorities for the next sprint.",
                status = TaskStatus.IN_PROGRESS,
                dueDate = "2026-05-31",
            ),
            onEvent = {},
        )
    }
}
