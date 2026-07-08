package com.doannd3.treetask.core.data.mapper

import com.doannd3.treetask.core.common.extension.toInstant
import com.doannd3.treetask.core.common.extension.toLong
import com.doannd3.treetask.core.database.model.TaskEntity
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.model.task.TaskStatus
import com.doannd3.treetask.core.network.model.response.TaskResponse

fun TaskResponse.toTaskOrNull(): Task? {
    val taskId = id.requiredStringOrNull() ?: return null
    val ownerId = userId.requiredStringOrNull() ?: return null
    val taskTitle = title.requiredStringOrNull() ?: return null
    val taskStatus = status.requiredTaskStatusOrNull() ?: return null
    val taskDueDate = dueDate.requiredInstantOrNull() ?: return null
    val taskCreatedAt = createdAt.requiredInstantOrNull() ?: return null
    val taskUpdatedAt = updatedAt.requiredInstantOrNull() ?: return null

    return Task(
        id = taskId,
        userId = ownerId,
        title = taskTitle,
        description = description,
        status = taskStatus,
        dueDate = taskDueDate,
        createdAt = taskCreatedAt,
        updatedAt = taskUpdatedAt,
    )
}

fun TaskResponse.toTaskEntityOrNull(): TaskEntity? = toTaskOrNull()?.toTaskEntity()

fun Task.toTaskEntity() =
    TaskEntity(
        id = this.id,
        userId = this.userId,
        title = this.title,
        description = this.description,
        status = this.status.apiValue,
        dueDate = this.dueDate.toLong(),
        createdAt = this.createdAt.toLong(),
        updatedAt = this.updatedAt.toLong(),
    )

fun TaskEntity.toTask() =
    Task(
        id = this.id,
        userId = this.userId,
        title = this.title,
        description = this.description,
        status = TaskStatus.fromStatus(this.status),
        dueDate = this.dueDate.toInstant(),
        createdAt = this.createdAt.toInstant(),
        updatedAt = this.updatedAt.toInstant(),
    )
