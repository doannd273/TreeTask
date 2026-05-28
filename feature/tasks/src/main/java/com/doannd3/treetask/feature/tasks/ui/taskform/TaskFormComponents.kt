package com.doannd3.treetask.feature.tasks.ui.taskform

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import java.time.LocalDate
import java.time.ZoneOffset

// region TaskTitleInput

@Composable
internal fun TaskTitleInput(
    title: String,
    enabled: Boolean,
    onTitleChange: (String) -> Unit,
    onImeNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        colors =
        OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        textStyle = TextStyle(fontSize = 15.sp),
        singleLine = true,
        keyboardOptions =
        KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        keyboardActions =
        KeyboardActions(
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
private fun TaskTitleInputPreview() {
    TreeTaskTheme {
        TaskTitleInput(
            title = "Prepare sprint planning",
            enabled = true,
            onTitleChange = {},
            onImeNext = {},
        )
    }
}

// endregion

// region TaskDescriptionInput

@Composable
internal fun TaskDescriptionInput(
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
        colors =
        OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        textStyle = TextStyle(fontSize = 15.sp),
        minLines = 4,
        keyboardOptions =
        KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        keyboardActions =
        KeyboardActions(
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
private fun TaskDescriptionInputPreview() {
    TreeTaskTheme {
        TaskDescriptionInput(
            description = "Review backlog and define priorities for the next sprint.",
            enabled = true,
            onDescriptionChange = {},
            onImeNext = {},
        )
    }
}

// endregion

// region TaskStatusSelector

@Composable
internal fun TaskStatusSelector(
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
private fun TaskStatusSelectorPreview() {
    TreeTaskTheme {
        TaskStatusSelector(
            selectedStatus = TaskStatus.IN_PROGRESS,
            enabled = true,
            onStatusChange = {},
        )
    }
}

// endregion

// region DatePickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppDatePickerDialog(
    selectedDateMillis: Long?,
    onDismiss: () -> Unit,
    onDateSelected: (Long?) -> Unit,
) {
    val today = remember {
        LocalDate.now(ZoneOffset.UTC)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    }
    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    utcTimeMillis >= today
            },
        )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                },
            ) {
                Text(text = stringResource(R.string.tasks_date_picker_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.tasks_date_picker_cancel))
            }
        },
    ) {
        DatePicker(state = datePickerState)
    }
}

@AppPreviewLightDark
@Composable
private fun AppDatePickerDialogPreview() {
    TreeTaskTheme {
        AppDatePickerDialog(
            selectedDateMillis = 1_779_667_200_000,
            onDismiss = {},
            onDateSelected = {},
        )
    }
}

// endregion

// region TaskDueDateInput

@Composable
internal fun TaskDueDateInput(
    modifier: Modifier = Modifier,
    dueDate: String,
    enabled: Boolean,
    onDueDateClick: () -> Unit,
) {
    Box(
        modifier =
        Modifier.clickable(enabled = enabled) {
            onDueDateClick()
        },
    ) {
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            // Always disabled to prevent touch/focus interception; Box.clickable handles tap.
            enabled = false,
            colors =
            if (enabled) {
                OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                OutlinedTextFieldDefaults.colors()
            },
            textStyle = TextStyle(fontSize = 15.sp),
            singleLine = true,
            value = dueDate,
            onValueChange = {},
            label = { Text(text = stringResource(R.string.tasks_add_task_due_date_label)) },
            placeholder = {
                Text(text = stringResource(R.string.tasks_add_task_due_date_placeholder))
            },
        )
    }
}

@AppPreviewLightDark
@Composable
private fun TaskDueDateInputPreview() {
    TreeTaskTheme {
        TaskDueDateInput(
            dueDate = "2026-05-31",
            enabled = true,
            onDueDateClick = {},
        )
    }
}

// endregion

// region TaskSubmitButton

@Composable
internal fun TaskSubmitButton(
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
private fun TaskSubmitButtonPreview() {
    TreeTaskTheme {
        TaskSubmitButton(
            isLoading = false,
            onSubmit = {},
        )
    }
}

// endregion
