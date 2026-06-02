package com.doannd3.treetask.core.designsystem.theme

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.doannd3.treetask.core.designsystem.R
import com.doannd3.treetask.core.model.task.TaskStatus

@StringRes
fun TaskStatus.labelRes(): Int =
    when (this) {
        TaskStatus.TODO -> R.string.designsystem_status_todo
        TaskStatus.IN_PROGRESS -> R.string.designsystem_status_in_progress
        TaskStatus.PENDING -> R.string.designsystem_status_pending
        TaskStatus.DONE -> R.string.designsystem_status_done
    }

@Immutable
data class TaskStatusColors(
    val containerColor: Color,
    val contentColor: Color,
)

@Composable
@ReadOnlyComposable
fun TaskStatus.statusColors(): TaskStatusColors {
    val colorScheme = MaterialTheme.colorScheme
    return when (this) {
        TaskStatus.TODO ->
            TaskStatusColors(
                containerColor = colorScheme.secondary,
                contentColor = colorScheme.onSecondary,
            )

        TaskStatus.IN_PROGRESS ->
            TaskStatusColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
            )

        TaskStatus.PENDING ->
            TaskStatusColors(
                containerColor = colorScheme.error,
                contentColor = colorScheme.onError,
            )

        TaskStatus.DONE ->
            TaskStatusColors(
                containerColor = MaterialTheme.treeTaskColors.success,
                contentColor = MaterialTheme.treeTaskColors.onSuccess,
            )
    }
}
