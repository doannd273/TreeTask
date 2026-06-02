package com.doannd3.treetask.core.model.stats

import com.doannd3.treetask.core.model.task.TaskStatus
import java.time.Instant

data class RecentTaskSummary(
    val id: String,
    val title: String,
    val status: TaskStatus,
    val createAt: Instant,
    val dueDate: Instant?,
)