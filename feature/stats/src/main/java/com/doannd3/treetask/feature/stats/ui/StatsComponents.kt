package com.doannd3.treetask.feature.stats.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.doannd3.treetask.core.common.extension.toMonthDay
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLight
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.designsystem.theme.treeTaskColors
import com.doannd3.treetask.core.model.stats.RecentTaskSummary
import com.doannd3.treetask.core.model.task.TaskStatus
import com.doannd3.treetask.core.ui.labelRes
import com.doannd3.treetask.core.ui.statusColors
import com.doannd3.treetask.feature.stats.R
import java.time.Instant

private const val MAX_RECENT_TASK_ROWS = 5

// region StatsSummaryCard

@Composable
internal fun StatsSummaryCard(
    done: Int,
    total: Int,
    completionRate: Int,
) {
    val progress = completionRate.coerceIn(0, 100) / 100f

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(10.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    progress = { progress },
                    color = MaterialTheme.treeTaskColors.success,
                    trackColor = MaterialTheme.treeTaskColors.success.copy(alpha = 0.2f),
                    strokeWidth = 5.dp,
                )

                Text(
                    text = stringResource(R.string.stats_completion_rate_value, completionRate),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.stats_completion_rate),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.stats_tasks_done, done, total),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@AppPreviewLight
@Composable
private fun StatsSummaryCardPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            StatsSummaryCard(
                done = 31,
                total = 50,
                completionRate = 62,
            )
        }
    }
}

// endregion

// region StatsStatusTile

@Composable
internal fun StatsStatusTile(
    todo: Int,
    inProgress: Int,
    pending: Int,
    done: Int,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatsStatusItem(
                modifier = Modifier.weight(1f),
                taskStatusCount = todo,
                taskStatus = TaskStatus.TODO,
            )

            StatsStatusItem(
                modifier = Modifier.weight(1f),
                taskStatusCount = inProgress,
                taskStatus = TaskStatus.IN_PROGRESS,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatsStatusItem(
                modifier = Modifier.weight(1f),
                taskStatusCount = pending,
                taskStatus = TaskStatus.PENDING,
            )

            StatsStatusItem(
                modifier = Modifier.weight(1f),
                taskStatusCount = done,
                taskStatus = TaskStatus.DONE,
            )
        }
    }
}

@AppPreviewLight
@Composable
private fun StatsStatusTilePreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            StatsStatusTile(
                todo = 8,
                inProgress = 6,
                pending = 3,
                done = 24,
            )
        }
    }
}

// endregion

// region StatsStatusItem

@Composable
internal fun StatsStatusItem(
    modifier: Modifier = Modifier,
    taskStatusCount: Int,
    taskStatus: TaskStatus,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(taskStatus.statusColors().containerColor),
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = taskStatusCount.toString(),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = stringResource(taskStatus.labelRes()),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@AppPreviewLight
@Composable
private fun StatsStatusItemPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            StatsStatusItem(
                taskStatusCount = 8,
                taskStatus = TaskStatus.TODO,
            )
        }
    }
}

// endregion

// region StatsStatusBreakdownBar

// endregion

// region RecentTaskRow

@Composable
internal fun RecentTaskRow(
    taskRecent: RecentTaskSummary,
    onTaskClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onTaskClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = taskRecent.title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Box(
            modifier =
                Modifier
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        taskRecent.status.statusColors().containerColor,
                    ).padding(horizontal = 4.dp, vertical = 2.dp),
        ) {
            Text(
                text = stringResource(taskRecent.status.labelRes()),
                style = MaterialTheme.typography.labelSmall,
                color = taskRecent.status.statusColors().contentColor,
            )
        }

        taskRecent.dueDate?.let { dueDate ->
            Text(
                text = dueDate.toMonthDay(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@AppPreviewLight
@Composable
private fun RecentTaskRowPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            RecentTaskRow(
                taskRecent =
                    RecentTaskSummary(
                        id = "1",
                        title = "Fix login token refresh bug",
                        status = TaskStatus.DONE,
                        createdAt = Instant.now(),
                        dueDate = Instant.now(),
                    ),
                onTaskClick = {},
            )
        }
    }
}

// endregion

// region StatsErrorState
@Composable
internal fun StatsErrorState(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.stats_error_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Button(onClick = onRetry) {
                Text(text = stringResource(R.string.stats_retry))
            }
        }
    }
}

// endregion

// region StatsEmptyState

@Composable
internal fun StatsEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.stats_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

// endregion

// region RecentTasksCard
@Composable
internal fun RecentTasksCard(
    tasks: List<RecentTaskSummary>,
    onTaskClick: (String) -> Unit,
) {
    val visibleTasks = tasks.take(MAX_RECENT_TASK_ROWS)

    Card(
        modifier =
            Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(10.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
    ) {
        Column {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.stats_recent_tasks),
                style = MaterialTheme.typography.titleMedium,
            )

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            visibleTasks.forEachIndexed { index, task ->
                RecentTaskRow(
                    taskRecent = task,
                    onTaskClick = { onTaskClick(task.id) },
                )

                if (index < visibleTasks.lastIndex) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
            }
        }
    }
}

@AppPreviewLight
@Composable
private fun RecentTasksCardPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            RecentTasksCard(
                tasks =
                    (1..6).map { index ->
                        RecentTaskSummary(
                            id = index.toString(),
                            title = "Recent task #$index with a long title to verify row cap and ellipsis",
                            status = TaskStatus.entries[index % TaskStatus.entries.size],
                            createdAt = Instant.parse("2026-05-20T08:00:00Z"),
                            dueDate =
                                if (index % 2 == 0) {
                                    null
                                } else {
                                    Instant.parse("2026-05-25T17:00:00Z")
                                },
                        )
                    },
                onTaskClick = {},
            )
        }
    }
}

// endregion

// region LegendItem

@Composable
private fun LegendItem(status: TaskStatus) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(10.dp)
                    .background(status.statusColors().containerColor, CircleShape),
        )

        Spacer(Modifier.width(6.dp))

        Text(
            text = stringResource(status.labelRes()),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@AppPreviewLight
@Composable
private fun LegendItemPreview() {
    LegendItem(TaskStatus.PENDING)
}

// endregion

// region WorkloadBreakdownCard

@Composable
@OptIn(ExperimentalLayoutApi::class)
internal fun WorkloadBreakdownCard(
    todo: Int,
    inProgress: Int,
    pending: Int,
    done: Int,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.stats_workload_breakdown),
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .clip(RoundedCornerShape(50)),
            ) {
                WorkloadSegment(todo, TaskStatus.TODO.statusColors().containerColor)
                WorkloadSegment(inProgress, TaskStatus.IN_PROGRESS.statusColors().containerColor)
                WorkloadSegment(pending, TaskStatus.PENDING.statusColors().containerColor)
                WorkloadSegment(done, TaskStatus.DONE.statusColors().containerColor)
            }

            Spacer(Modifier.height(16.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                LegendItem(TaskStatus.TODO)
                LegendItem(TaskStatus.IN_PROGRESS)
                LegendItem(TaskStatus.PENDING)
                LegendItem(TaskStatus.DONE)
            }
        }
    }
}

@AppPreviewLight
@Composable
private fun WorkloadBreakdownCardPreview() {
    WorkloadBreakdownCard(
        todo = 10,
        inProgress = 5,
        pending = 19,
        done = 12,
    )
}

// endregion

// region WorkloadSegment

@Composable
private fun RowScope.WorkloadSegment(
    value: Int,
    color: Color,
) {
    if (value > 0) {
        Box(
            modifier =
                Modifier
                    .weight(value.toFloat())
                    .fillMaxHeight()
                    .background(color),
        )
    }
}

// endregion
