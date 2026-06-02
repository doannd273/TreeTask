package com.doannd3.treetask.core.model.stats

data class TaskStats(
    val total: Int,
    val todo: Int,
    val inProgress: Int,
    val pending: Int,
    val done: Int,
    val completionRate: Double,
    val recentTasks: List<RecentTaskSummary>,
)
