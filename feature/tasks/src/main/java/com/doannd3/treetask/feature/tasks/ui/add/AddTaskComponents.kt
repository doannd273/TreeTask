package com.doannd3.treetask.feature.tasks.ui.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.component.CommonButton
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.model.task.TaskStatus
import com.doannd3.treetask.feature.tasks.R
import com.doannd3.treetask.feature.tasks.ui.model.labelRes

// region AddTaskTitleInput

@Composable
internal fun AddTaskTitleInput(
    title: String,
    enabled: Boolean,
    onTitleChange: (String) -> Unit,
    onImeNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        textStyle = TextStyle(fontSize = 15.sp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        keyboardActions = KeyboardActions(
            onNext = { onImeNext() },
        ),
        value = title,
        onValueChange = onTitleChange,
        label = { Text(text = stringResource(R.string.tasks_add_task_title_label)) },
        placeholder = {
            Text(text = stringResource(R.string.tasks_add_task_title_placeholder))
        },
    )
}

@AppPreviewLightDark
@Composable
private fun AddTaskTitleInputPreview() {
    TreeTaskTheme {
        AddTaskTitleInput(
            title = "Prepare sprint planning",
            enabled = true,
            onTitleChange = {},
            onImeNext = {},
        )
    }
}

// endregion

// region AddTaskDescriptionInput

@Composable
internal fun AddTaskDescriptionInput(
    description: String,
    enabled: Boolean,
    onDescriptionChange: (String) -> Unit,
    onImeNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        modifier =
        modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp),
        enabled = enabled,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        textStyle = TextStyle(fontSize = 15.sp),
        minLines = 4,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        keyboardActions = KeyboardActions(
            onNext = { onImeNext() },
        ),
        value = description,
        onValueChange = onDescriptionChange,
        label = { Text(text = stringResource(R.string.tasks_add_task_description_label)) },
        placeholder = {
            Text(text = stringResource(R.string.tasks_add_task_description_placeholder))
        },
    )
}

@AppPreviewLightDark
@Composable
private fun AddTaskDescriptionInputPreview() {
    TreeTaskTheme {
        AddTaskDescriptionInput(
            description = "Review backlog and define priorities for the next sprint.",
            enabled = true,
            onDescriptionChange = {},
            onImeNext = {},
        )
    }
}

// endregion

// region AddTaskStatusSelector

@Composable
internal fun AddTaskStatusSelector(
    selectedStatus: TaskStatus,
    enabled: Boolean,
    onStatusChange: (TaskStatus) -> Unit,
    modifier: Modifier = Modifier,
    statuses: List<TaskStatus> = TaskStatus.entries,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.tasks_add_task_status_label),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 15.sp,
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 8.dp),
        ) {
            items(
                items = statuses,
                key = { it.name },
            ) { status ->
                FilterChip(
                    selected = selectedStatus == status,
                    enabled = enabled,
                    onClick = { onStatusChange(status) },
                    label = {
                        Text(text = stringResource(status.labelRes()))
                    },
                )
            }
        }
    }
}

@AppPreviewLightDark
@Composable
private fun AddTaskStatusSelectorPreview() {
    TreeTaskTheme {
        AddTaskStatusSelector(
            selectedStatus = TaskStatus.IN_PROGRESS,
            enabled = true,
            onStatusChange = {},
        )
    }
}

// endregion

// region AddTaskDueDateInput

@Composable
internal fun AddTaskDueDateInput(
    dueDate: String,
    enabled: Boolean,
    onDueDateChange: (String) -> Unit,
    onImeDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        textStyle = TextStyle(fontSize = 15.sp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = { onImeDone() },
        ),
        value = dueDate,
        onValueChange = onDueDateChange,
        label = { Text(text = stringResource(R.string.tasks_add_task_due_date_label)) },
        placeholder = {
            Text(text = stringResource(R.string.tasks_add_task_due_date_placeholder))
        },
    )
}

@AppPreviewLightDark
@Composable
private fun AddTaskDueDateInputPreview() {
    TreeTaskTheme {
        AddTaskDueDateInput(
            dueDate = "2026-05-31",
            enabled = true,
            onDueDateChange = {},
            onImeDone = {},
        )
    }
}

// endregion

// region AddTaskSubmitButton

@Composable
internal fun AddTaskSubmitButton(
    isLoading: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CommonButton(
        modifier = modifier,
        buttonText =
        stringResource(
            if (isLoading) {
                R.string.tasks_add_task_submit_loading
            } else {
                R.string.tasks_add_task_submit
            },
        ),
        isEnable = !isLoading,
        onSubmit = onSubmit,
    )
}

@AppPreviewLightDark
@Composable
private fun AddTaskSubmitButtonPreview() {
    TreeTaskTheme {
        AddTaskSubmitButton(
            isLoading = false,
            onSubmit = {},
        )
    }
}

// endregion
