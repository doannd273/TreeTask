package com.doannd3.treetask.feature.tasks.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.doannd3.treetask.core.common.extension.toDayMonth
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.designsystem.theme.treeTaskColors
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.model.task.TaskStatus
import com.doannd3.treetask.feature.tasks.R
import com.doannd3.treetask.feature.tasks.ui.model.labelRes
import java.time.Instant

// region TaskStatusChips

@Composable
internal fun TaskStatusChips(
    taskStatusSelected: TaskStatus?,
    onFilterSelect: (TaskStatus?) -> Unit,
) {
    val statusList = TaskStatus.entries.toTypedArray()

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(end = 8.dp, top = 8.dp),
    ) {
        items(items = statusList) { taskStatus ->
            FilterChip(
                selected = taskStatusSelected == taskStatus,
                onClick = {
                    onFilterSelect(
                        if (taskStatusSelected == taskStatus) null else taskStatus,
                    )
                },
                label = {
                    Text(text = stringResource(taskStatus.labelRes()))
                },
                modifier = Modifier.padding(end = 8.dp),
            )
        }
    }
}

@AppPreviewLightDark
@Composable
private fun TaskStatusChipsPreview() {
    TreeTaskTheme {
        var selected by remember { mutableStateOf<TaskStatus?>(TaskStatus.TODO) }

        TaskStatusChips(
            taskStatusSelected = selected,
            onFilterSelect = { selected = it },
        )
    }
}

// endregion

// region SearchTaskInput

@Composable
internal fun SearchTaskInput(
    isLoadingSearch: Boolean,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onClearClick: () -> Unit,
) {
    TextField(
        modifier =
        Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(6.dp),
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(6.dp),
            ),
        colors =
        TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
        ),
        textStyle =
        MaterialTheme.typography.bodyMedium,
        singleLine = true,
        keyboardOptions =
        KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
        ),
        placeholder = {
            Text(text = stringResource(R.string.tasks_search_hint))
        },
        leadingIcon = {
            Icon(
                painterResource(R.drawable.tasks_ic_search),
                contentDescription = stringResource(R.string.tasks_cd_search),
            )
        },
        trailingIcon = {
            when {
                isLoadingSearch -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                searchQuery.isNotEmpty() -> {
                    IconButton(onClick = onClearClick) {
                        Icon(
                            painterResource(R.drawable.tasks_ic_clear),
                            contentDescription = stringResource(R.string.tasks_cd_clear_search),
                        )
                    }
                }
            }
        },
        value = searchQuery,
        onValueChange = onSearchChange,
    )
}

@AppPreviewLightDark
@Composable
private fun SearchTaskInputPreview() {
    TreeTaskTheme {
        SearchTaskInput(
            searchQuery = "",
            isLoadingSearch = true,
            onSearchChange = {},
            onClearClick = {},
        )
    }
}

// endregion

// region SwipeToDeleteTaskItem

@Composable
internal fun SwipeToDeleteTaskItem(
    task: Task,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDeleteClick()
            }
            false
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val isActive = dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isActive) MaterialTheme.colorScheme.error else Color.Transparent)
                    .padding(end = 16.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                if (isActive) {
                    Icon(
                        painter = painterResource(R.drawable.tasks_ic_delete),
                        contentDescription = stringResource(R.string.tasks_delete_task_icon_description),
                        tint = MaterialTheme.colorScheme.onError,
                    )
                }
            }
        },
    ) {
        TaskItem(task = task, onClick = onClick, onDeleteClick = onDeleteClick)
    }
}

// endregion

// region TaskItem

@Composable
internal fun TaskItem(
    modifier: Modifier = Modifier,
    task: Task,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier =
        modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = task.title,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                IconButton(
                    modifier = Modifier.size(40.dp),
                    onClick = onDeleteClick,
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.tasks_ic_delete),
                        contentDescription = stringResource(R.string.tasks_delete_task_icon_description),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
            if (task.description?.isNotBlank() == true) {
                Text(
                    text = task.description ?: "",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusBadge(status = task.status)

                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.tasks_due_date_label, task.dueDate.toDayMonth()),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

@Composable
internal fun StatusBadge(status: TaskStatus) {
    val colorScheme = MaterialTheme.colorScheme
    val (backgroundColor, textColor) = when (status) {
        TaskStatus.TODO -> colorScheme.surfaceVariant to colorScheme.onSurfaceVariant
        TaskStatus.IN_PROGRESS -> colorScheme.primary to colorScheme.onPrimary
        TaskStatus.PENDING -> colorScheme.error to colorScheme.onError
        TaskStatus.DONE -> MaterialTheme.treeTaskColors.success to MaterialTheme.treeTaskColors.onSuccess
    }

    Row(
        modifier =
        Modifier
            .background(
                shape = RoundedCornerShape(3.dp),
                color = backgroundColor,
            )
            .padding(vertical = 4.dp, horizontal = 8.dp),
    ) {
        Text(
            text = stringResource(status.labelRes()),
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@AppPreviewLightDark
@Composable
private fun StatusBadgeTodoPreview() {
    TreeTaskTheme {
        StatusBadge(status = TaskStatus.TODO)
    }
}

@AppPreviewLightDark
@Composable
private fun TaskItemPreview() {
    TreeTaskTheme {
        TaskItem(
            task =
            Task(
                id = "1",
                userId = "user_1",
                title = "Fix login bug",
                description = "Crash when login with Google",
                status = TaskStatus.IN_PROGRESS,
                dueDate = Instant.parse("2026-04-20T10:00:00Z"),
                createdAt = Instant.parse("2026-04-10T08:00:00Z"),
                updatedAt = Instant.parse("2026-04-15T09:00:00Z"),
            ),
            onClick = {},
            onDeleteClick = {},
        )
    }
}

@AppPreviewLightDark
@Composable
private fun StatusBadgePreview() {
    TreeTaskTheme {
        StatusBadge(status = TaskStatus.DONE)
    }
}

// endregion
