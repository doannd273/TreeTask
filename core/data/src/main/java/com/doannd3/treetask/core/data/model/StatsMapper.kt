package com.doannd3.treetask.core.data.model

import com.doannd3.treetask.core.common.extension.toInstantOrNow
import com.doannd3.treetask.core.common.extension.toInstantOrNull
import com.doannd3.treetask.core.model.stats.RecentTaskSummary
import com.doannd3.treetask.core.model.stats.TaskStats
import com.doannd3.treetask.core.model.task.TaskStatus
import com.doannd3.treetask.core.network.model.response.DashboardResponse
import com.doannd3.treetask.core.network.model.response.RecentTask

fun DashboardResponse.toTaskStats(): TaskStats {
    return TaskStats(
        total = total ?: 0,
        todo = todo ?: 0,
        inProgress = inProgress ?: 0,
        pending = pending ?: 0,
        done = done ?: 0,
        completionRate = completionRate ?: 0.0,
        recentTasks = recentTasks.map { recentTask -> recentTask.toRecentTaskSummary() },
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

private fun RecentTask.toRecentTaskSummary(): RecentTaskSummary {
    return RecentTaskSummary(
        id = id.orEmpty(),
        title = title.orEmpty(),
        status = TaskStatus.fromStatus(status),
        createdAt = createdAt.toInstantOrNow(),
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
