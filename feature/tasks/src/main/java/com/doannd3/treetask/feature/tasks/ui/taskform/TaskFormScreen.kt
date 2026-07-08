package com.doannd3.treetask.feature.tasks.ui.taskform

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.runtime.rememberUpdatedState
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
import com.doannd3.treetask.core.designsystem.component.AppLoadingDialog
import com.doannd3.treetask.core.designsystem.component.CommonHeader
import com.doannd3.treetask.core.designsystem.component.message.AppDialogType
import com.doannd3.treetask.core.designsystem.component.message.AppMessage
import com.doannd3.treetask.core.designsystem.component.message.AppMessageDialogHost
import com.doannd3.treetask.core.designsystem.component.message.AppMessageId
import com.doannd3.treetask.core.designsystem.component.message.rememberAppMessageHostState
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

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val messageHostState = rememberAppMessageHostState()
    val currentContext by rememberUpdatedState(context)
    val currentOnNavigateBack by rememberUpdatedState(onNavigateBack)

    TaskFormScreen(
        state = state,
        onBackClick = { viewModel.onEvent(TaskFormEvent.BackClicked) },
        onTitleChange = { viewModel.onEvent(TaskFormEvent.TitleChanged(it)) },
        onDescriptionChange = { viewModel.onEvent(TaskFormEvent.DescriptionChanged(it)) },
        onStatusChange = { viewModel.onEvent(TaskFormEvent.StatusChanged(it)) },
        onDueDateChange = { viewModel.onEvent(TaskFormEvent.DueDateChanged(it)) },
        onSubmitTaskForm = { viewModel.onEvent(TaskFormEvent.SubmitTaskForm) },
    )

    AppMessageDialogHost(
        state = messageHostState,
        onAcknowledged = { message ->
            if (message.id == TaskFormMessageIds.Success) {
                viewModel.onEvent(TaskFormEvent.SuccessAcknowledged)
            }
        },
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is TaskFormEffect.ShowErrorMessage -> {
                        messageHostState.enqueue(
                            AppMessage(
                                id = TaskFormMessageIds.Error,
                                message = effect.message.asString(currentContext),
                                type = AppDialogType.Error,
                            ),
                        )
                    }

                    is TaskFormEffect.ShowSuccessMessage -> {
                        messageHostState.enqueue(
                            AppMessage(
                                id = TaskFormMessageIds.Success,
                                message = effect.message.asString(currentContext),
                                type = AppDialogType.Success,
                            ),
                        )
                    }

                    is TaskFormEffect.NavigateBack -> {
                        currentOnNavigateBack()
                    }
                }
            }
        }
    }

    LaunchedEffect(viewModel.baseErrorEffect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.baseErrorEffect.collect { message ->
                messageHostState.enqueue(
                    AppMessage(
                        id = TaskFormMessageIds.Error,
                        message = message.asString(currentContext),
                        type = AppDialogType.Error,
                    ),
                )
            }
        }
    }
}

@Composable
internal fun TaskFormScreen(
    state: TaskFormState,
    onBackClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onStatusChange: (TaskStatus) -> Unit,
    onDueDateChange: (String) -> Unit,
    onSubmitTaskForm: () -> Unit,
) {
    val context = LocalContext.current
    val screenTitle = state.screenTitle?.asString(context).orEmpty()
    val mode = state.mode
    val title = state.title
    val description = state.description
    val status = state.status
    val dueDate = state.dueDate
    val isLoading = state.isLoading

    Scaffold(
        contentWindowInsets =
        WindowInsets.safeDrawing,
        topBar = {
            CommonHeader(
                title = screenTitle,
                onNavigateBack = onBackClick,
            )
        },
    ) { paddingValues ->
        TaskFormContent(
            mode = mode,
            title = title,
            description = description,
            status = status,
            dueDate = dueDate,
            isLoading = isLoading,
            onTitleChange = onTitleChange,
            onDescriptionChange = onDescriptionChange,
            onStatusChange = onStatusChange,
            onDueDateChange = onDueDateChange,
            onSubmitTaskForm = onSubmitTaskForm,
            modifier = Modifier.padding(paddingValues),
        )
    }

    AppLoadingDialog(isLoading = isLoading || state.isLoadingTask)
}

@AppPreviewLightDark
@Composable
private fun TaskFormScreenPreview() {
    TreeTaskTheme {
        TaskFormScreen(
            state = TaskFormState(),
            onBackClick = {},
            onTitleChange = {},
            onDescriptionChange = {},
            onStatusChange = {},
            onDueDateChange = {},
            onSubmitTaskForm = {},
        )
    }
}

@Composable
internal fun TaskFormContent(
    mode: TaskFormMode,
    title: String,
    description: String,
    status: TaskStatus,
    dueDate: String,
    isLoading: Boolean,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onStatusChange: (TaskStatus) -> Unit,
    onDueDateChange: (String) -> Unit,
    onSubmitTaskForm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val descriptionFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val onSubmitTaskFormDebounced =
        rememberDebouncedClick {
            onSubmitTaskForm()
        }
    val isInputEnabled = !isLoading
    val isEditable = mode.isEditable
    val isReadOnly = !isEditable

    var showDatePicker by remember { mutableStateOf(false) }
    val displayText =
        remember(dueDate) {
            dueDate.ymdToDmy()
        }
    val initialMillis =
        remember(dueDate) {
            dueDate.ymdToEpochMillis()
        }

    if (showDatePicker && isEditable) {
        AppDatePickerDialog(
            selectedDateMillis = initialMillis,
            onDismiss = { showDatePicker = false },
            onDateSelected = { onDueDateChange(it.toYmdDate()) },
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
                title = title,
                enabled = isInputEnabled,
                readOnly = isReadOnly,
                onTitleChange = onTitleChange,
                onImeNext = { descriptionFocusRequester.requestFocus() },
            )

            TaskDescriptionInput(
                modifier =
                Modifier.focusRequester(descriptionFocusRequester),
                description = description,
                enabled = isInputEnabled,
                readOnly = isReadOnly,
                onDescriptionChange = onDescriptionChange,
                onImeNext = {
                    focusManager.clearFocus()
                },
            )

            TaskStatusSelector(
                selectedStatus = status,
                enabled = isInputEnabled,
                readOnly = isReadOnly,
                onStatusChange = onStatusChange,
            )

            TaskDueDateInput(
                dueDate = displayText,
                enabled = isInputEnabled,
                readOnly = isReadOnly,
                onDueDateClick = { showDatePicker = true },
            )

            if (isEditable) {
                TaskSubmitButton(
                    modifier = Modifier.padding(top = 8.dp),
                    isLoading = isLoading,
                    isEditMode = mode.isEdit,
                    onSubmit = {
                        focusManager.clearFocus()
                        onSubmitTaskFormDebounced()
                    },
                )
            }
        }
    }
}

@AppPreviewLightDark
@Composable
private fun TaskFormContentPreview() {
    TreeTaskTheme {
        TaskFormContent(
            mode = TaskFormMode.ADD,
            title = "Prepare sprint planning",
            description = "Review backlog and define priorities for the next sprint.",
            status = TaskStatus.IN_PROGRESS,
            dueDate = "2026-05-31",
            isLoading = false,
            onTitleChange = {},
            onDescriptionChange = {},
            onStatusChange = {},
            onDueDateChange = {},
            onSubmitTaskForm = {},
        )
    }
}

@AppPreviewLightDark
@Composable
private fun TaskFormContentReadOnlyPreview() {
    TreeTaskTheme {
        TaskFormContent(
            mode = TaskFormMode.VIEW,
            title = "Review dashboard analytics",
            description = "Check completion rate and recent task behavior before release.",
            status = TaskStatus.DONE,
            dueDate = "2026-06-02",
            isLoading = false,
            onTitleChange = {},
            onDescriptionChange = {},
            onStatusChange = {},
            onDueDateChange = {},
            onSubmitTaskForm = {},
        )
    }
}

private object TaskFormMessageIds {
    val Error = AppMessageId("task-form-error")
    val Success = AppMessageId("task-form-success")
}
