package com.doannd3.treetask.core.data.model

import com.doannd3.treetask.core.common.extension.toInstant
import com.doannd3.treetask.core.common.extension.toInstantOrNow
import com.doannd3.treetask.core.common.extension.toLong
import com.doannd3.treetask.core.database.model.TaskEntity
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.model.task.TaskStatus
import com.doannd3.treetask.core.network.model.response.TaskResponse

fun TaskResponse.toTaskDomain(): Task {
    return Task(
        id = this.id.orEmpty(),
        userId = this.userId.orEmpty(),
        title = this.title.orEmpty(),
        description = this.description.orEmpty(),
        status = TaskStatus.fromStatus(status),
        dueDate = this.dueDate.toInstantOrNow(),
        createdAt = this.createdAt.toInstantOrNow(),
        updatedAt = this.updatedAt.toInstantOrNow()
    )
}

fun List<TaskResponse>.toTasksDomain(): List<Task> {
    return this.map { it.toTaskDomain() }
}

fun Task.toTaskEntity(userId: String) = TaskEntity(
    id = this.id,
    userId = userId,
    title = this.title,
    description = this.description ?: "",
    status = this.status.name,
    dueDate = this.dueDate.toLong(),
    createdAt = this.createdAt.toLong(),
    updatedAt = this.updatedAt.toLong()
)

fun TaskEntity.toTaskDomain() = Task(
    id = this.id,
    userId = this.userId,
    title = this.title,
    description = this.description,
    status = TaskStatus.fromStatus(this.status),
    dueDate = this.dueDate.toInstant(),
    createdAt = this.createdAt.toInstant(),
    updatedAt = this.updatedAt.toInstant()
)

fun List<TaskEntity>.toTasksDomain(): List<Task> = this.map { it.toTaskDomain() }
