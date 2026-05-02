package com.doannd3.treetask.core.model.task

import java.time.Instant

data class Task(
    val id: String,
    val userId: String,
    val title: String,
    val description: String?,
    val status: TaskStatus,
    val dueDate: Instant,
    val createdAt: Instant,
    val updatedAt: Instant,
)
