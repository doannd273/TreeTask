package com.doannd3.treetask.core.data.mapper

import com.doannd3.treetask.core.common.extension.toInstantOrNull
import com.doannd3.treetask.core.model.stats.RecentTaskSummary
import com.doannd3.treetask.core.model.stats.TaskStats
import com.doannd3.treetask.core.network.model.response.DashboardResponse
import com.doannd3.treetask.core.network.model.response.RecentTask

fun DashboardResponse.toTaskStatsOrNull(): TaskStats? {
    val totalCount = total ?: return null
    val todoCount = todo ?: return null
    val inProgressCount = inProgress ?: return null
    val pendingCount = pending ?: return null
    val doneCount = done ?: return null
    val taskCompletionRate = completionRate ?: return null
    val recentTaskSummaries =
        recentTasks.mapOrNull { recentTask ->
            recentTask.toRecentTaskSummaryOrNull()
        } ?: return null

    return TaskStats(
        total = totalCount,
        todo = todoCount,
        inProgress = inProgressCount,
        pending = pendingCount,
        done = doneCount,
        completionRate = taskCompletionRate,
        recentTasks = recentTaskSummaries,
    )
}

fun TaskStats.toDashboardResponse(): DashboardResponse {
    return DashboardResponse(
        total = total,
        todo = todo,
        inProgress = inProgress,
        pending = pending,
        done = done,
        completionRate = completionRate,
        recentTasks = recentTasks.map { recentTask -> recentTask.toRecentTaskResponse() },
    )
}

private fun RecentTask.toRecentTaskSummaryOrNull(): RecentTaskSummary? {
    val id = id.requiredStringOrNull() ?: return null
    val title = title.requiredStringOrNull() ?: return null
    val status = status.requiredTaskStatusOrNull() ?: return null
    val createdAt = createdAt.requiredInstantOrNull() ?: return null

    return RecentTaskSummary(
        id = id,
        title = title,
        status = status,
        createdAt = createdAt,
        dueDate = dueDate.toInstantOrNull(),
    )
}

private fun RecentTaskSummary.toRecentTaskResponse(): RecentTask {
    return RecentTask(
        id = id,
        title = title,
        status = status.apiValue,
        createdAt = createdAt.toString(),
        dueDate = dueDate?.toString(),
    )
}
